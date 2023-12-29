package org.mifos.pheeBillPay.zeebe.workers.implementation;



import com.fasterxml.jackson.databind.ObjectMapper;
import org.mifos.pheeBillPay.data.BillDetails;
import org.mifos.pheeBillPay.data.BillRTPReqDTO;
import org.mifos.pheeBillPay.data.PayerRequestDTO;
import org.mifos.pheeBillPay.data.ResponseDTO;
import org.mifos.pheeBillPay.zeebe.workers.BaseWorker;
import org.mifos.pheeBillPay.zeebe.workers.Worker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.mifos.pheeBillPay.zeebe.ZeebeVariables.BILL_RTP_REQ;
import static org.mifos.pheeBillPay.zeebe.ZeebeVariables.PAYER_RTP_REQ;

@Component
public class PayerRTPWorker extends BaseWorker {
    @Value("${ph_ee_connector.contactpoint}")
    private String connectorContactPoint;
    @Value("${ph_ee_bill_pay.contactpoint}")
    private String billPayContactPoint;
    @Value("${ph_ee_bill_pay.endpoint.payerRtpResponse}")
    private String payerRtpResponseEndpoint;
    @Value("${payer_fsp.tenant}")
    private String payerFspTenant;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void setup() {
        newWorker(Worker.PAYER_RTP, (client, job) -> {
            Map<String, Object> variables = job.getVariablesAsMap();
            String url = connectorContactPoint + "/billTransferRequests";
            String tenantId = variables.get("tenantId").toString();
            //String correlationId = variables.get("clientCorrelationId").toString();
            String body = variables.get(BILL_RTP_REQ).toString();
            String billerId  = variables.get("billerId").toString();
            BillRTPReqDTO billRTPReqDTO = objectMapper.readValue(body, BillRTPReqDTO.class);
            PayerRequestDTO payerRequestDTO = new PayerRequestDTO();
            payerRequestDTO.setRequestId(String.valueOf(job.getKey()));
            payerRequestDTO.setRtpId(123456);
            payerRequestDTO.setTransactionId("123234455");
            payerRequestDTO.setBillDetails(new BillDetails(billRTPReqDTO.getBillId(), billRTPReqDTO.getBill().getBillerName(), billRTPReqDTO.getBill().getAmount()));
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonPayload = objectMapper.writeValueAsString(payerRequestDTO);

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Platform-TenantId", payerFspTenant);
            headers.set("X-Client-Correlation-ID", String.valueOf(job.getKey()));
            headers.set("X-Biller-Id", billerId);
            headers.set("X-Callback-URL", billPayContactPoint + payerRtpResponseEndpoint);

            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> requestEntity = new HttpEntity<>(jsonPayload, headers);

            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<ResponseDTO> responseEntity = null;

            try {
                responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, ResponseDTO.class);
            } catch (HttpClientErrorException | HttpServerErrorException e) {
                logger.error(e.getMessage());
            }
            if(responseEntity!=null && responseEntity.getBody().getResponseCode().equals("00")){
                variables.put(PAYER_RTP_REQ, true);
            }
            else {
                variables.put(PAYER_RTP_REQ, false);
            }
            client.newCompleteCommand(job.getKey()).variables(variables).send();
        });
    }
}
