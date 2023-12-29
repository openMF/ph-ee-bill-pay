package org.mifos.pheeBillPay.zeebe;

import static org.mifos.pheeBillPay.zeebe.ZeebeVariables.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.json.JSONObject;
import org.mifos.pheeBillPay.data.BillDetails;
import org.mifos.pheeBillPay.data.BillRTPReqDTO;
import org.mifos.pheeBillPay.data.BillRTPResponseDTO;
import org.mifos.pheeBillPay.data.PayerRequestDTO;
import org.mifos.pheeBillPay.data.ResponseDTO;
import org.mifos.pheeBillPay.utils.Headers;
import org.mifos.pheeBillPay.utils.SpringWrapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

@Component
public class ZeebeWorkers {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ZeebeClient zeebeClient;

    @Autowired
    private ProducerTemplate producerTemplate;

    @Autowired
    private CamelContext camelContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${zeebe.client.evenly-allocated-max-jobs}")
    private int workerMaxJobs;
    @Value("${connector.contactpoint}")
    private String connectorContactPoint;
    @Value("${billpay.contactpoint}")
    private String billPayContactPoint;
    @Value("${billpay.endpoint.payerRtpResponse}")
    private String payerRtpResponseEndpoint;
    @Value("${payer_fsp.tenant}")
    private String payerFspTenant;
    @PostConstruct
    public void setupWorkers() {
        // billerdetails
        zeebeClient.newWorker().jobType("discover-biller").handler((client, job) -> {
            logger.info("Job '{}' started from process '{}' with key {}", job.getType(), job.getBpmnProcessId(), job.getKey());
            logWorkerDetails(job);
            Map<String, Object> variables = job.getVariablesAsMap();
            Headers headers = new Headers.HeaderBuilder().addHeader(PLATFORM_TENANT, variables.get(TENANT_ID).toString())
                    .addHeader(CLIENTCORRELATIONID, variables.get(CLIENTCORRELATIONID).toString())
                    .addHeader(PAYER_FSP, variables.get("payerFspId").toString()).addHeader(BILL_ID, variables.get("billId").toString())
                    .build();
            Exchange exchange = SpringWrapperUtil.getDefaultWrappedExchange(producerTemplate.getCamelContext(), headers, null);
            producerTemplate.send("direct:biller-fetch", exchange);
            Boolean response = exchange.getProperty(BILLER_FETCH_FAILED, Boolean.class);
            variables.put(BILLER_FETCH_FAILED, response);
            variables.put(BILLER_DETAILS, exchange.getProperty(BILLER_DETAILS, String.class));
            variables.put(BILLER_ID, exchange.getProperty(BILLER_ID, String.class));
            variables.put(BILLER_NAME, exchange.getProperty(BILLER_NAME, String.class));
            variables.put(BILLER_TYPE, exchange.getProperty(BILLER_TYPE, String.class));
            variables.put(BILLER_ACCOUNT, exchange.getProperty(BILLER_ACCOUNT, String.class));
            logger.info("Zeebe variable {}", job.getVariablesAsMap());
            zeebeClient.newCompleteCommand(job.getKey()).variables(variables).send().join();
            ;

        }).name("discover-biller").maxJobsActive(workerMaxJobs).open();

        // setting callback for inquiry api
        zeebeClient.newWorker().jobType("billFetchResponse").handler((client, job) -> {
            logger.info("Job '{}' started from process '{}' with key {}", job.getType(), job.getBpmnProcessId(), job.getKey());
            logWorkerDetails(job);
            Map<String, Object> variables = job.getVariablesAsMap();
            Headers headers = new Headers.HeaderBuilder().addHeader("Platform-TenantId", variables.get(TENANT_ID).toString())
                    .addHeader(CLIENTCORRELATIONID, variables.get(CLIENTCORRELATIONID).toString())
                    .addHeader(PAYER_FSP, variables.get("payerFspId").toString()).addHeader(BILL_ID, variables.get(BILL_ID).toString())
                    .build();
            Exchange exchange = SpringWrapperUtil.getDefaultWrappedExchange(producerTemplate.getCamelContext(), headers, null);
            exchange.setProperty(BILLER_DETAILS, variables.get(BILLER_DETAILS).toString());
            exchange.setProperty(CALLBACK_URL, variables.get(CALLBACK_URL).toString());
            exchange.setProperty(BILL_INQUIRY_RESPONSE, variables.get(BILL_INQUIRY_RESPONSE));
            producerTemplate.send("direct:bill-inquiry-response", exchange);
            variables.put(BILL_PAY_RESPONSE, exchange.getIn().getBody(String.class));
            variables.put(BILL_PAY_FAILED, exchange.getProperty(BILL_PAY_FAILED));
            zeebeClient.newCompleteCommand(job.getKey()).variables(variables).send();
            logger.info("Zeebe variable {}", job.getVariablesAsMap());
        }).name("billFetchResponse").maxJobsActive(workerMaxJobs).open();

        // setting response to callback url for payment status
        zeebeClient.newWorker().jobType("billPayResponse").handler((client, job) -> {
            logger.info("Job '{}' started from process '{}' with key {}", job.getType(), job.getBpmnProcessId(), job.getKey());
            logWorkerDetails(job);
            Map<String, Object> variables = job.getVariablesAsMap();
            Headers headers = new Headers.HeaderBuilder().addHeader("Platform-TenantId", variables.get(TENANT_ID).toString())
                    .addHeader(CLIENTCORRELATIONID, variables.get(CLIENTCORRELATIONID).toString())
                    .addHeader(PAYER_FSP, variables.get("payerFspId").toString()).addHeader(BILL_ID, variables.get(BILL_ID).toString())
                    .build();
            Exchange exchange = SpringWrapperUtil.getDefaultWrappedExchange(producerTemplate.getCamelContext(), headers, null);
            exchange.setProperty(CALLBACK_URL, variables.get(CALLBACK_URL).toString());
            exchange.setProperty(BILL_PAY_RESPONSE, variables.get(BILL_PAY_RESPONSE).toString());
            exchange.setProperty("code", variables.get("code"));
            exchange.setProperty("reason", variables.get("reason"));
            exchange.setProperty("status", variables.get("status"));
            producerTemplate.send("direct:paymentNotification-response", exchange);
            variables.put(BILL_PAY_RESPONSE, exchange.getProperty(BILL_PAY_RESPONSE));
            zeebeClient.newCompleteCommand(job.getKey()).variables(variables).send();
            logger.info("Zeebe variable {}", job.getVariablesAsMap());
        }).name("billPayResponse").maxJobsActive(workerMaxJobs).open();

        zeebeClient.newWorker().jobType("payerRtpRequest").handler((client, job) -> {
            logger.info("Job '{}' started from process '{}' with key {}", job.getType(), job.getBpmnProcessId(), job.getKey());
            Map<String, Object> variables = job.getVariablesAsMap();
            String url = connectorContactPoint + "/billTransferRequests";
            String tenantId = variables.get("tenantId").toString();
            String transactionId = "123456778";
            String clientCorrelation = (String) variables.get("X-CorrelationID");
            //String correlationId = variables.get("clientCorrelationId").toString();
            variables.put(TRANSACTION_ID, transactionId);
            variables.put("payerTenantId", payerFspTenant);
            variables.put("payerCallbackUrl", billPayContactPoint + payerRtpResponseEndpoint);
            String body = variables.get(BILL_RTP_REQ).toString();
            String billerId  = variables.get("billerId").toString();
            BillRTPReqDTO billRTPReqDTO = objectMapper.readValue(body, BillRTPReqDTO.class);
            variables.put(BILLER_NAME, billRTPReqDTO.getBill().getBillerName());
            variables.put(BILL_AMOUNT, billRTPReqDTO.getBill().getAmount());
            variables.put(RTP_ID, 123456);
            PayerRequestDTO payerRequestDTO = new PayerRequestDTO();
            payerRequestDTO.setRequestId(String.valueOf(job.getElementInstanceKey()));
            payerRequestDTO.setRtpId(123456);
            payerRequestDTO.setTransactionId("123234455");
            payerRequestDTO.setBillDetails(new BillDetails(billRTPReqDTO.getBillId(), billRTPReqDTO.getBill().getBillerName(), billRTPReqDTO.getBill().getAmount()));
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonPayload = objectMapper.writeValueAsString(payerRequestDTO);

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Platform-TenantId", payerFspTenant);
            headers.set("X-Client-Correlation-ID", clientCorrelation);
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
        }).name("payerRtpRequest").maxJobsActive(workerMaxJobs).open();;

        zeebeClient.newWorker().jobType("billerRtpResponse").handler((client, job) -> {
            logger.info("Job '{}' started from process '{}' with key {}", job.getType(), job.getBpmnProcessId(), job.getKey());
            Map<String, Object> variables = job.getVariablesAsMap();
            String tenantId = variables.get(TENANT_ID).toString();
            String correlationId = variables.get(CLIENTCORRELATIONID).toString();
            String callbackUrl = variables.get(CALLBACK_URL).toString();
            HttpHeaders headers = new HttpHeaders();
            String billId= variables.get(BILL_ID).toString();
            String billerName= variables.get(BILLER_NAME).toString();
            String amount= variables.get(BILL_AMOUNT).toString();
            String rtpStatus= variables.get(RTP_STATUS).toString();
            String rtpId = variables.get(RTP_ID).toString();


            headers.set("X-Platform-TenantId", tenantId);
            headers.set("X-Client-Correlation-ID", correlationId);

            headers.setContentType(MediaType.APPLICATION_JSON);
            BillRTPResponseDTO billRTPResponseDTO =  new BillRTPResponseDTO();
            billRTPResponseDTO.setRequestId(correlationId);
            billRTPResponseDTO.setBillId(billId);
            billRTPResponseDTO.setRtpStatus(rtpStatus);
            billRTPResponseDTO.setRtpId(rtpId);
            //billRTPResponseDTO.setRejectReason(rejectReason);



            HttpEntity<BillRTPResponseDTO> requestEntity = new HttpEntity<>(billRTPResponseDTO, headers);

            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<ResponseDTO> responseEntity = null;

            try {
                responseEntity = restTemplate.exchange(callbackUrl, HttpMethod.POST, requestEntity, ResponseDTO.class);
            } catch (HttpClientErrorException | HttpServerErrorException e) {
                logger.error(e.getMessage());
            }
            client.newCompleteCommand(job.getKey()).variables(variables).send();
        }).name("billerRtpResponse").maxJobsActive(workerMaxJobs).open();;

    }

    private void logWorkerDetails(ActivatedJob job) {
        JSONObject jsonJob = new JSONObject();
        jsonJob.put("bpmnProcessId", job.getBpmnProcessId());
        jsonJob.put("elementInstanceKey", job.getElementInstanceKey());
        jsonJob.put("jobKey", job.getKey());
        jsonJob.put("jobType", job.getType());
        jsonJob.put("workflowElementId", job.getElementId());
        jsonJob.put("workflowDefinitionVersion", job.getProcessDefinitionVersion());
        jsonJob.put("workflowKey", job.getProcessDefinitionKey());
        jsonJob.put("workflowInstanceKey", job.getProcessInstanceKey());
        logger.info("Job started: {}", jsonJob.toString(4));
    }
}
