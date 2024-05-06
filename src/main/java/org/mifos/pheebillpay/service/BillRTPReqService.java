package org.mifos.pheebillpay.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.mifos.connector.common.channel.dto.PhErrorDTO;
import org.mifos.pheebillpay.data.BillRTPReqDTO;
import org.mifos.pheebillpay.validators.BillPayValidator;
import org.mifos.pheebillpay.zeebe.ZeebeProcessStarter;
import org.mifos.pheebillpay.zeebe.ZeebeVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BillRTPReqService {

    @Autowired
    private ZeebeProcessStarter zeebeProcessStarter;
    @Autowired
    private BillPayValidator billPayValidator;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${bpmn.flows.bill-request}")
    String billPayFlow;

    String transactionId;
    private static final Logger logger = LoggerFactory.getLogger(BillRTPReqService.class);

    public PhErrorDTO billRtpReq(String tenantId, String correlationId, String callBackUrl, String billerId, BillRTPReqDTO body) {
        PhErrorDTO phErrorDTO = billPayValidator.validateBillRTPRequest(body);
        if (phErrorDTO == null) {
            Map<String, Object> extraVariables = new HashMap<>();
            extraVariables.put(ZeebeVariables.TENANT_ID, tenantId);
            extraVariables.put(ZeebeVariables.CLIENTCORRELATIONID, correlationId);
            extraVariables.put(ZeebeVariables.BILL_ID, body.getBillID());
            extraVariables.put(ZeebeVariables.BILLER_ID, billerId);
            extraVariables.put(ZeebeVariables.CALLBACK_URL, callBackUrl);
            extraVariables.put("payerFspId", body.getPayerFspDetails().getPayerFSPID());
            extraVariables.put("payeePartyIdType", "Bill");
            extraVariables.put("payeePartyId", body.getBillID());
            extraVariables.put("payerPartyIdType", "Bill");
            extraVariables.put("payerPartyId", billerId);
            extraVariables.put("state", "INITIATED");
            String jsonString = null;

            try {
                jsonString = objectMapper.writeValueAsString(body);
            } catch (JsonProcessingException e) {
                logger.error(e.getMessage());
            }

            extraVariables.put(ZeebeVariables.BILL_RTP_REQ, jsonString);
            // adding a method to be implemented that checks the rrequest and perofrms als if needed
            // checkRequest(body);
            String tenantSpecificBpmn = billPayFlow.replace("{dfspid}", tenantId);
            transactionId = zeebeProcessStarter.startZeebeWorkflow(tenantSpecificBpmn, body.toString(), extraVariables);
        }

        return phErrorDTO;
    }
}
