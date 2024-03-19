package org.mifos.pheebillpay.service;

import static org.mifos.pheebillpay.zeebe.ZeebeVariables.BILL_ID;
import static org.mifos.pheebillpay.zeebe.ZeebeVariables.BILL_PAYMENTS_REQ;
import static org.mifos.pheebillpay.zeebe.ZeebeVariables.BILL_REQ_ID;
import static org.mifos.pheebillpay.zeebe.ZeebeVariables.CALLBACK_URL;
import static org.mifos.pheebillpay.zeebe.ZeebeVariables.CLIENTCORRELATIONID;
import static org.mifos.pheebillpay.zeebe.ZeebeVariables.PAYER_FSP_ID;
import static org.mifos.pheebillpay.zeebe.ZeebeVariables.PAYMENTS_REF_ID;
import static org.mifos.pheebillpay.zeebe.ZeebeVariables.TENANT_ID;

import java.util.HashMap;
import java.util.Map;
import org.mifos.pheebillpay.data.BillPaymentsReqDTO;
import org.mifos.pheebillpay.zeebe.ZeebeProcessStarter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BillPaymentsService {

    @Autowired
    private ZeebeProcessStarter zeebeProcessStarter;

    @Value("${bpmn.flows.payment-notification}")
    String paymentNotificationFlow;

    String transactionId;

    public String billPayments(String tenantId, String correlationId, String callbackUrl, String payerFspId, BillPaymentsReqDTO body) {
        Map<String, Object> extraVariables = new HashMap<>();
        extraVariables.put(TENANT_ID, tenantId);
        extraVariables.put(CLIENTCORRELATIONID, correlationId);
        extraVariables.put(PAYER_FSP_ID, payerFspId);
        extraVariables.put(BILL_ID, body.getBillId());
        extraVariables.put(PAYMENTS_REF_ID, body.getPaymentReferenceID());
        extraVariables.put(BILL_REQ_ID, body.getBillInquiryRequestId());
        extraVariables.put(CALLBACK_URL, callbackUrl);
        extraVariables.put(BILL_PAYMENTS_REQ, body);
        extraVariables.put("payeePartyIdType", "Bill");
        extraVariables.put("payeePartyId", body.getBillId());
        extraVariables.put("payerPartyIdType", "Bill");
        extraVariables.put("payerPartyId", payerFspId);
        String tenantSpecificBpmn = paymentNotificationFlow.replace("{dfspid}", tenantId);
        transactionId = zeebeProcessStarter.startZeebeWorkflow(tenantSpecificBpmn, body.toString(), extraVariables);
        return transactionId;
    }

}
