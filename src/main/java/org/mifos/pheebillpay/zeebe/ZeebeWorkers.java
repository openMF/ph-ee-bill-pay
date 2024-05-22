package org.mifos.pheebillpay.zeebe;

import static org.mifos.pheebillpay.zeebe.ZeebeVariables.BILLER_ACCOUNT;
import static org.mifos.pheebillpay.zeebe.ZeebeVariables.BILLER_DETAILS;
import static org.mifos.pheebillpay.zeebe.ZeebeVariables.BILLER_FETCH_FAILED;
import static org.mifos.pheebillpay.zeebe.ZeebeVariables.BILLER_ID;
import static org.mifos.pheebillpay.zeebe.ZeebeVariables.BILLER_NAME;
import static org.mifos.pheebillpay.zeebe.ZeebeVariables.BILLER_TYPE;
import static org.mifos.pheebillpay.zeebe.ZeebeVariables.BILL_AMOUNT;
import static org.mifos.pheebillpay.zeebe.ZeebeVariables.BILL_ID;
import static org.mifos.pheebillpay.zeebe.ZeebeVariables.BILL_INQUIRY_RESPONSE;
import static org.mifos.pheebillpay.zeebe.ZeebeVariables.BILL_PAYMENTS_REQ;
import static org.mifos.pheebillpay.zeebe.ZeebeVariables.BILL_PAY_FAILED;
import static org.mifos.pheebillpay.zeebe.ZeebeVariables.BILL_PAY_RESPONSE;
import static org.mifos.pheebillpay.zeebe.ZeebeVariables.BILL_RTP_REQ;
import static org.mifos.pheebillpay.zeebe.ZeebeVariables.CALLBACK_URL;
import static org.mifos.pheebillpay.zeebe.ZeebeVariables.CLIENTCORRELATIONID;
import static org.mifos.pheebillpay.zeebe.ZeebeVariables.ERROR_INFORMATION;
import static org.mifos.pheebillpay.zeebe.ZeebeVariables.PAYER_FSP;
import static org.mifos.pheebillpay.zeebe.ZeebeVariables.PAYER_RTP_REQ;
import static org.mifos.pheebillpay.zeebe.ZeebeVariables.PLATFORM_TENANT;
import static org.mifos.pheebillpay.zeebe.ZeebeVariables.RTP_ID;
import static org.mifos.pheebillpay.zeebe.ZeebeVariables.RTP_STATUS;
import static org.mifos.pheebillpay.zeebe.ZeebeVariables.TENANT_ID;
import static org.mifos.pheebillpay.zeebe.ZeebeVariables.TRANSACTION_ID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.json.JSONObject;
import org.mifos.pheebillpay.data.BillDetails;
import org.mifos.pheebillpay.data.BillPaymentsReqDTO;
import org.mifos.pheebillpay.data.BillRTPReqDTO;
import org.mifos.pheebillpay.data.BillRTPResponseDTO;
import org.mifos.pheebillpay.data.PayerRequestDTO;
import org.mifos.pheebillpay.data.ResponseDTO;
import org.mifos.pheebillpay.utils.Headers;
import org.mifos.pheebillpay.utils.SpringWrapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
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
    @Value("${payer_fsp.mockPayerUnreachable.fspId}")
    private String mockPayerUnreachableFspId;
    @Value("${payer_fsp.mockPayerUnreachable.financialAddress}")
    private String mockPayerUnreachableFinancialAddress;
    @Value("${payer_fsp.mockDebitFailed.fspId}")
    private String mockDebitFailedFspId;
    @Value("${payer_fsp.mockDebitFailed.financialAddress}")
    private String mockDebitFailedFinancialAddress;

    @Value("${status.billAcceptedId}")
    private String billAcceptedId;

    @Value("${status.billTimeout}")
    private int billTimeout;

    private static final ScheduledExecutorService scheduledThreadPoolExecutor = Executors.newScheduledThreadPool(10);

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
            if (variables.get(BILLER_FETCH_FAILED).equals(false)) {
                variables.put(BILLER_DETAILS, exchange.getProperty(BILLER_DETAILS, String.class));
                variables.put(BILLER_ID, exchange.getProperty(BILLER_ID, String.class));
                variables.put(BILLER_NAME, exchange.getProperty(BILLER_NAME, String.class));
                variables.put(BILLER_TYPE, exchange.getProperty(BILLER_TYPE, String.class));
                variables.put(BILLER_ACCOUNT, exchange.getProperty(BILLER_ACCOUNT, String.class));
                variables.put(BILL_ID, exchange.getProperty(BILL_ID, String.class));
            } else {
                variables.put(ERROR_INFORMATION, exchange.getProperty(ERROR_INFORMATION));
            }
            logger.info("Zeebe variable {}", job.getVariablesAsMap());

            zeebeClient.newCompleteCommand(job.getKey()).variables(variables).send().join();

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
            if (variables.get(BILLER_DETAILS) != null) {
                exchange.setProperty(BILLER_DETAILS, variables.get(BILLER_DETAILS).toString());
            }
            exchange.setProperty(CALLBACK_URL, variables.get(CALLBACK_URL).toString());
            exchange.setProperty(BILL_INQUIRY_RESPONSE, variables.get(BILL_INQUIRY_RESPONSE));
            exchange.setProperty(ERROR_INFORMATION, variables.get(ERROR_INFORMATION));
            exchange.setProperty(CLIENTCORRELATIONID, variables.get(CLIENTCORRELATIONID));
            producerTemplate.send("direct:bill-inquiry-response", exchange);
            variables.put(BILL_PAY_RESPONSE, exchange.getIn().getBody(String.class));
            variables.put(BILL_PAY_FAILED, exchange.getProperty(BILL_PAY_FAILED));
            logger.info("Zeebe variable {}", job.getVariablesAsMap());
            client.newCompleteCommand(job.getKey()).variables(variables).send();
        }).name("billFetchResponse").maxJobsActive(workerMaxJobs).open();

        // setting response to callback url for payment status
        zeebeClient.newWorker().jobType("billPayResponse").handler((client, job) -> {
            logger.info("Job '{}' started from process '{}' with key {}", job.getType(), job.getBpmnProcessId(), job.getKey());
            logWorkerDetails(job);
            Map<String, Object> variables = job.getVariablesAsMap();
            String currentBillId = variables.get(BILL_ID).toString();
            if (currentBillId.equals(billAcceptedId)) {
                logger.debug("Bill Id matches, moving to execution pause");
                pauseExec();
            }
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
            variables.put("state", "SUCCESS");
            zeebeClient.newCompleteCommand(job.getKey()).variables(variables).send();
            logger.info("Zeebe variable {}", job.getVariablesAsMap());
        }).name("billPayResponse").maxJobsActive(workerMaxJobs).open();

        zeebeClient.newWorker().jobType("payerRtpRequest").handler((client, job) -> {
            logger.info("Job '{}' started from process '{}' with key {}", job.getType(), job.getBpmnProcessId(), job.getKey());
            Map<String, Object> variables = job.getVariablesAsMap();
            String url = connectorContactPoint + "/billTransferRequests";
            String tenantId = variables.get("tenantId").toString();
            String clientCorrelation = (String) variables.get("X-CorrelationID");

            variables.put("payerTenantId", payerFspTenant);
            variables.put("payerCallbackUrl", billPayContactPoint + payerRtpResponseEndpoint);
            String body = variables.get(BILL_RTP_REQ).toString();
            String billerId = variables.get("billerId").toString();
            BillRTPReqDTO billRTPReqDTO = objectMapper.readValue(body, BillRTPReqDTO.class);
            if (billRTPReqDTO.getPayerFspDetails() != null) {
                if (billRTPReqDTO.getPayerFspDetails().getPayerFSPID().equals(mockPayerUnreachableFspId)
                        && billRTPReqDTO.getPayerFspDetails().getFinancialAddress().equals(mockPayerUnreachableFinancialAddress)) {
                    variables.put("payerRtpRequestSuccess", false);
                    variables.put("errorInformation", "Payer FI was unreachable");
                } else if (billRTPReqDTO.getPayerFspDetails().getPayerFSPID().equals(mockDebitFailedFspId)
                        && billRTPReqDTO.getPayerFspDetails().getFinancialAddress().equals(mockDebitFailedFinancialAddress)) {
                    variables.put("payerRtpRequestSuccess", false);
                    variables.put("errorInformation", "Payer FSP is unable to debit amount");
                } else {
                    variables.put("payerRtpRequestSuccess", true);
                }
            }

            variables.put(BILLER_NAME, billRTPReqDTO.getBillDetails().getBillerName());
            variables.put(BILL_AMOUNT, billRTPReqDTO.getBillDetails().getAmount());
            variables.put(RTP_ID, 123456);
            PayerRequestDTO payerRequestDTO = new PayerRequestDTO();
            payerRequestDTO.setRequestId(String.valueOf(job.getElementInstanceKey()));
            payerRequestDTO.setRtpId(123456);
            payerRequestDTO.setTransactionId(variables.get(TRANSACTION_ID).toString());
            payerRequestDTO.setBillDetails(new BillDetails(billRTPReqDTO.getBillID(), billRTPReqDTO.getBillDetails().getBillerName(),
                    billRTPReqDTO.getBillDetails().getAmount()));
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

            CloseableHttpClient httpClient = HttpClients.custom()
                    .setSSLContext(new SSLContextBuilder().loadTrustMaterial(null, (certificate, authType) -> true).build())
                    .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));

            ResponseEntity<ResponseDTO> responseEntity = null;

            try {
                responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, ResponseDTO.class);
            } catch (HttpClientErrorException | HttpServerErrorException e) {
                logger.error(e.getMessage());
                variables.put(PAYER_RTP_REQ, false);
            }
            if (responseEntity != null && responseEntity.getBody().getResponseCode().equals("00")) {
                variables.put(PAYER_RTP_REQ, true);
            } else {
                variables.put(PAYER_RTP_REQ, false);
            }
            client.newCompleteCommand(job.getKey()).variables(variables).send();
        }).name("payerRtpRequest").maxJobsActive(workerMaxJobs).open();

        zeebeClient.newWorker().jobType("billerRtpResponse").handler((client, job) -> {
            logger.info("Job '{}' started from process '{}' with key {}", job.getType(), job.getBpmnProcessId(), job.getKey());
            Map<String, Object> variables = job.getVariablesAsMap();
            String tenantId = variables.get(TENANT_ID).toString();
            String correlationId = variables.get(CLIENTCORRELATIONID).toString();
            String callbackUrl = variables.get(CALLBACK_URL).toString();
            HttpHeaders headers = new HttpHeaders();

            String billId = variables.get(BILL_ID).toString();
            String billerName = variables.get(BILLER_NAME).toString();
            String amount = variables.get(BILL_AMOUNT).toString();
            String rtpStatus = variables.get(RTP_STATUS).toString();
            String rtpId = variables.get(RTP_ID).toString();
            BillPaymentsReqDTO billPaymentsReqDTO = new BillPaymentsReqDTO(correlationId, generateUniqueNumber(12), billId,
                    generateUniqueNumber(12));
            variables.put(BILL_PAYMENTS_REQ, billPaymentsReqDTO);

            headers.set("X-Platform-TenantId", tenantId);
            headers.set("X-Client-Correlation-ID", correlationId);

            headers.setContentType(MediaType.APPLICATION_JSON);
            BillRTPResponseDTO billRTPResponseDTO = new BillRTPResponseDTO();
            billRTPResponseDTO.setRequestId(correlationId);
            billRTPResponseDTO.setBillId(billId);
            billRTPResponseDTO.setRtpStatus(rtpStatus);
            billRTPResponseDTO.setRtpId(rtpId);
            variables.put("state", "REQUEST_ACCEPTED");
            // billRTPResponseDTO.setRejectReason(rejectReason);

            HttpEntity<BillRTPResponseDTO> requestEntity = new HttpEntity<>(billRTPResponseDTO, headers);

            RestTemplate restTemplate = new RestTemplate();

            CloseableHttpClient httpClient = HttpClients.custom()
                    .setSSLContext(new SSLContextBuilder().loadTrustMaterial(null, (certificate, authType) -> true).build())
                    .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));

            ResponseEntity<ResponseDTO> responseEntity = null;

            try {
                restTemplate.exchange(callbackUrl, HttpMethod.POST, requestEntity, ResponseDTO.class);
            } catch (HttpClientErrorException | HttpServerErrorException e) {
                logger.error(e.getMessage());
            }
            client.newCompleteCommand(job.getKey()).variables(variables).send();
        }).name("billerRtpResponse").maxJobsActive(workerMaxJobs).open();

        zeebeClient.newWorker().jobType("sendError").handler((client, job) -> {
            logger.info("Job '{}' started from process '{}' with key {}", job.getType(), job.getBpmnProcessId(), job.getKey());
            Map<String, Object> variables = job.getVariablesAsMap();
            String correlationId = variables.get(CLIENTCORRELATIONID).toString();

            RestTemplate restTemplate = new RestTemplate();
            CloseableHttpClient httpClient = HttpClients.custom()
                    .setSSLContext(new SSLContextBuilder().loadTrustMaterial(null, (certificate, authType) -> true).build())
                    .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));
            String callbackUrl = variables.get(CALLBACK_URL).toString();
            HttpHeaders headers = new HttpHeaders();
            headers.add("X-Client-Correlation-ID", correlationId);
            ResponseEntity<String> responseEntity = null;

            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String, Object> errorInfo = new HashMap<>();
            errorInfo.put("errorMessage", variables.get("errorInformation").toString());
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonBody = null;
            try {
                jsonBody = objectMapper.writeValueAsString(errorInfo);
            } catch (JsonProcessingException e) {
                // Handle the exception appropriately
                logger.error(e.getMessage());
            }
            HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);

            try {
                restTemplate.exchange(callbackUrl, HttpMethod.POST, requestEntity, String.class);
            } catch (HttpClientErrorException | HttpServerErrorException e) {
                logger.error(e.getMessage());
            }

            client.newCompleteCommand(job.getKey()).variables(variables).send();
        }).name("sendError").maxJobsActive(workerMaxJobs).open();

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

    public String generateUniqueNumber(int length) {
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length) {
            sb.append(UUID.randomUUID().toString().replaceAll("-", ""));
        }
        return sb.substring(0, length);
    }

    private void pauseExec() {
        try {
            logger.debug("Pausing execution for capturing intermediary status ");
            scheduledThreadPoolExecutor.schedule(() -> { }, billTimeout, TimeUnit.SECONDS).get();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
        logger.debug("Resuming execution post pause");
    }
}
