package org.mifos.pheebillpay.service;

import static org.mifos.pheebillpay.zeebe.ZeebeVariables.BILL_ID;
import static org.mifos.pheebillpay.zeebe.ZeebeVariables.CALLBACK_URL;
import static org.mifos.pheebillpay.zeebe.ZeebeVariables.CLIENTCORRELATIONID;
import static org.mifos.pheebillpay.zeebe.ZeebeVariables.FIELD;
import static org.mifos.pheebillpay.zeebe.ZeebeVariables.PAYER_FSP_ID;
import static org.mifos.pheebillpay.zeebe.ZeebeVariables.TENANT_ID;

import java.util.HashMap;
import java.util.Map;
import org.mifos.pheebillpay.zeebe.ZeebeProcessStarter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BillInquiryService {

    private Logger logger = LoggerFactory.getLogger(BillInquiryService.class);

    @Autowired
    private ZeebeProcessStarter zeebeProcessStarter;

    @Value("${bpmn.flows.bill-pay}")
    String billPayFlow;

    @Value("${billPay.FspNotOnboarded}")
    private String fspNotOnboarded;

    String transactionId;

    public String billInquiry(String tenantId, String correlationId, String callbackUrl, String payerFspId, String billId, String field) {
        Map<String, Object> extraVariables = new HashMap<>();
        if (billId.equals(fspNotOnboarded)) {
            transactionId = "Participant Not Onboarded";
        } else {
            extraVariables.put(TENANT_ID, tenantId);
            extraVariables.put(CLIENTCORRELATIONID, correlationId);
            extraVariables.put(CALLBACK_URL, callbackUrl);
            extraVariables.put(PAYER_FSP_ID, payerFspId);
            extraVariables.put(BILL_ID, billId);
            extraVariables.put(FIELD, field);
            String tenantSpecificBpmn = billPayFlow.replace("{dfspid}", tenantId);
            try {
                transactionId = zeebeProcessStarter.startZeebeWorkflow(tenantSpecificBpmn, null, extraVariables);
            } catch (Exception e) {
                logger.info("Exception in starting workflow: {}", e.getMessage());
                transactionId = "Exception in starting workflow";
            }
        }
        return transactionId;
    }

}
