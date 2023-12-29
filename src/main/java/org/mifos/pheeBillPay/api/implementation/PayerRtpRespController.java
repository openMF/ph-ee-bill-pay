package org.mifos.pheeBillPay.api.implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.zeebe.client.ZeebeClient;
import org.mifos.pheeBillPay.api.definition.PayerRtpRespApi;
import org.mifos.pheeBillPay.data.PayerRTPResponse;
import org.mifos.pheeBillPay.data.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.mifos.pheeBillPay.utils.BillPayEnum.SUCCESS_RESPONSE_CODE;
import static org.mifos.pheeBillPay.utils.BillPayEnum.SUCCESS_RESPONSE_MESSAGE;

@Controller
public class PayerRtpRespController implements PayerRtpRespApi {

    @Autowired(required = false)
    private ZeebeClient zeebeClient;
    @Autowired
    private ObjectMapper objectMapper;
    @Override
    public ResponseEntity<ResponseDTO> billRTPResp(String tenantId, String correlationId, String billerId, PayerRTPResponse payerRTPResponse) throws ExecutionException, InterruptedException {
        Map<String, Object> variables = new HashMap<>();

        String transactionId = payerRTPResponse.getTxnId();
        variables.put("billStatus", payerRTPResponse.getRtpStatus());

        if(payerRTPResponse.getRtpStatus().equals("00")) {
            variables.put("billAccepted", true);
        } else {
            variables.put("billAccepted", true);
            variables.put("billRejectReason", payerRTPResponse.getRejectReason());
        }


        if (zeebeClient != null) {

            zeebeClient.newPublishMessageCommand().messageName("payerRtpResponse").correlationKey(transactionId)
                    .timeToLive(Duration.ofMillis(50000)).variables(variables).send();
        }
        ResponseDTO responseDTO = new ResponseDTO(SUCCESS_RESPONSE_CODE.getValue(), SUCCESS_RESPONSE_MESSAGE.getValue(), correlationId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(responseDTO);
    }
}
