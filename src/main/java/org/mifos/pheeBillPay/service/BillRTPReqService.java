package org.mifos.pheeBillPay.service;

import static org.mifos.pheeBillPay.zeebe.ZeebeVariables.*;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mifos.pheeBillPay.data.BillRTPReqDTO;
import org.mifos.pheeBillPay.zeebe.ZeebeProcessStarter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class BillRTPReqService {

    @Autowired
    private ZeebeProcessStarter zeebeProcessStarter;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${bpmn.flows.bill-request}")
    String billPayFlow;

    String transactionId;

    @Async("asyncExecutor")
    public String billRtpReq(String tenantId, String correlationId, String callBackUrl, String billerId, BillRTPReqDTO body) {
        Map<String, Object> extraVariables = new HashMap<>();
        extraVariables.put(TENANT_ID, tenantId);
        extraVariables.put(CLIENTCORRELATIONID, correlationId);
        extraVariables.put(BILL_ID, body.getBillId());
        extraVariables.put(BILLER_ID, billerId);
        extraVariables.put(CALLBACK_URL, callBackUrl);
        String jsonString = null;

        try {
            jsonString = objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            e.printStackTrace(); // Handle the exception according to your requirements
        }

        extraVariables.put(BILL_RTP_REQ, jsonString);
        //adding a method to be implemented that checks the rrequest and perofrms als if needed
        //checkRequest(body);
        String tenantSpecificBpmn = billPayFlow.replace("{dfspid}", tenantId);
        transactionId = zeebeProcessStarter.startZeebeWorkflow(tenantSpecificBpmn,
                body.toString(), extraVariables);
        return transactionId;
    }
    // checkRequest(BillRTPReqDTO body){
    // if(body.getRequestType().equals("00"){
    // return true;
    //
    // }
    // else do lookup
}
