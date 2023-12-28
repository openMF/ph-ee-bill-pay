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
import org.mifos.pheeBillPay.utils.Headers;
import org.mifos.pheeBillPay.utils.SpringWrapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
