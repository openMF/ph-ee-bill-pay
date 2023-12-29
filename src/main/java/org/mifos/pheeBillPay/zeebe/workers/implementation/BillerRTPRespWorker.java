package org.mifos.pheeBillPay.zeebe.workers.implementation;

import org.mifos.pheeBillPay.data.BillRTPResponseDTO;
import org.mifos.pheeBillPay.data.ResponseDTO;
import org.mifos.pheeBillPay.zeebe.workers.BaseWorker;
import org.mifos.pheeBillPay.zeebe.workers.Worker;
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

import static org.mifos.pheeBillPay.zeebe.ZeebeVariables.CALLBACK_URL;

@Component
public class BillerRTPRespWorker extends BaseWorker {
    @Override
    public void setup() {
        newWorker(Worker.BILLER_RTP_RESPONSE, (client, job) -> {
            Map<String, Object> variables = job.getVariablesAsMap();
            String tenantId = variables.get("tenantId").toString();
            String correlationId = variables.get("clientCorrelationId").toString();
            String callbackUrl = variables.get(CALLBACK_URL).toString();
            HttpHeaders headers = new HttpHeaders();
            String billId= variables.get("billId").toString();
            String billerName= variables.get("billerName").toString();
            String amount= variables.get("amount").toString();
            String rtpStatus= variables.get("rtpStatus").toString();
            String rejectReason= variables.get("rejectReason").toString();


            headers.set("X-Platform-TenantId", tenantId);
            headers.set("X-Client-Correlation-ID", correlationId);

            headers.setContentType(MediaType.APPLICATION_JSON);
            BillRTPResponseDTO billRTPResponseDTO =  new BillRTPResponseDTO();
            billRTPResponseDTO.setRequestId(correlationId);
            billRTPResponseDTO.setBillId(billId);
            billRTPResponseDTO.setRtpStatus(rtpStatus);
            billRTPResponseDTO.setRejectReason(rejectReason);



            HttpEntity<BillRTPResponseDTO> requestEntity = new HttpEntity<>(billRTPResponseDTO, headers);

            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<ResponseDTO> responseEntity = null;

            try {
                responseEntity = restTemplate.exchange(callbackUrl, HttpMethod.POST, requestEntity, ResponseDTO.class);
            } catch (HttpClientErrorException | HttpServerErrorException e) {
                logger.error(e.getMessage());
            }
        });
    }
}
