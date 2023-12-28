package org.mifos.pheeBillPay.zeebe;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Start a Zeebe workflow
 */
@Component
public class ZeebeProcessStarter {

    private static Logger logger = LoggerFactory.getLogger(ZeebeProcessStarter.class);

    @Autowired
    private ZeebeClient zeebeClient;

    @Value("${transaction-id-length}")
    private int transactionIdLength;

    public String startZeebeWorkflow(String workflowId, String request, Map<String, Object> extraVariables) {
        String transactionId = generateTransactionId();

        Map<String, Object> variables = new HashMap<>();
        variables.put(ZeebeVariables.TRANSACTION_ID, transactionId);
        variables.put(ZeebeVariables.CHANNEL_REQUEST, request);
        variables.put(ZeebeVariables.ORIGIN_DATE, Instant.now().toEpochMilli());
        if (extraVariables != null) {
            variables.putAll(extraVariables);
        }

        logger.info("starting workflow HERE:");
        // TODO if successful transfer response arrives in X timeout return it otherwise do callback
        ProcessInstanceEvent instance = zeebeClient.newCreateInstanceCommand().bpmnProcessId(workflowId).latestVersion()
                .variables(variables).send().join();

        logger.info("zeebee workflow instance from process {} started with transactionId {}, instance key: {}", workflowId, transactionId,
                instance.getProcessInstanceKey());
        return transactionId;
    }

    // TODO generate proper cluster-safe transaction id
    private String generateTransactionId() {
        return UUID.randomUUID().toString();
    }

    private String randomCharOfSize(int size) {
        String data = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        char[] arr = data.toCharArray();
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < size; i++) {
            int index = (int) (Math.random() * (data.length()));
            s.append(arr[index]);
        }
        return s.toString();
    }

}
