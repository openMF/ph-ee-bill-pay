package org.mifos.pheebillpay.service;

import static org.mifos.pheebillpay.zeebe.ZeebeVariables.BILLER_ID;
import static org.mifos.pheebillpay.zeebe.ZeebeVariables.BILL_ID;
import static org.mifos.pheebillpay.zeebe.ZeebeVariables.BILL_RTP_REQ;
import static org.mifos.pheebillpay.zeebe.ZeebeVariables.CALLBACK_URL;
import static org.mifos.pheebillpay.zeebe.ZeebeVariables.CLIENTCORRELATIONID;
import static org.mifos.pheebillpay.zeebe.ZeebeVariables.TENANT_ID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.mifos.connector.common.channel.dto.PhErrorDTO;
import org.mifos.pheebillpay.data.BillRTPReqDTO;
import org.mifos.pheebillpay.zeebe.ZeebeProcessStarter;
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
    private BillValidatorService billValidatorService;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${bpmn.flows.bill-request}")
    String billPayFlow;

    String transactionId;
    private static final Logger logger = LoggerFactory.getLogger(BillRTPReqService.class);

    public PhErrorDTO billRtpReq(String tenantId, String correlationId, String callBackUrl, String billerId, BillRTPReqDTO body) {
        PhErrorDTO phErrorDTO = billValidatorService.validateCreateVoucher(body);
        if (phErrorDTO == null) {
            Map<String, Object> extraVariables = new HashMap<>();
            extraVariables.put(TENANT_ID, tenantId);
            extraVariables.put(CLIENTCORRELATIONID, correlationId);
            extraVariables.put(BILL_ID, body.getBillId());
            extraVariables.put(BILLER_ID, billerId);
            extraVariables.put(CALLBACK_URL, callBackUrl);
            extraVariables.put("payerFspId", body.getPayerFspDetail().getPayerFspId());
            extraVariables.put("payeePartyIdType", "Bill");
            extraVariables.put("payeePartyId", body.getBillId());
            extraVariables.put("payerPartyIdType", "Bill");
            extraVariables.put("payerPartyId", billerId);
            String jsonString = null;

            try {
                jsonString = objectMapper.writeValueAsString(body);
            } catch (JsonProcessingException e) {
                logger.error(e.getMessage());
            }

            extraVariables.put(BILL_RTP_REQ, jsonString);
            // adding a method to be implemented that checks the rrequest and perofrms als if needed
            // checkRequest(body);
            String tenantSpecificBpmn = billPayFlow.replace("{dfspid}", tenantId);
            transactionId = zeebeProcessStarter.startZeebeWorkflow(tenantSpecificBpmn, body.toString(), extraVariables);
        }

        return phErrorDTO;
    }
}
