package org.mifos.pheebillpay.api.implementation;

import static org.mifos.pheebillpay.utils.BillPayEnum.FAILED_RESPONSE_CODE;
import static org.mifos.pheebillpay.utils.BillPayEnum.FAILED_RESPONSE_MESSAGE;
import static org.mifos.pheebillpay.utils.BillPayEnum.SUCCESS_RESPONSE_CODE;
import static org.mifos.pheebillpay.utils.BillPayEnum.SUCCESS_RESPONSE_MESSAGE;

import java.util.concurrent.ExecutionException;
import org.mifos.connector.common.channel.dto.PhErrorDTO;
import org.mifos.pheebillpay.api.definition.BillRtpReqApi;
import org.mifos.pheebillpay.data.BillRTPReqDTO;
import org.mifos.pheebillpay.data.ResponseDTO;
import org.mifos.pheebillpay.service.BillRTPReqService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BillRTPReqController implements BillRtpReqApi {

    private Logger logger = LoggerFactory.getLogger(BillRTPReqController.class);

    @Autowired
    private BillRTPReqService billRTPReqService;

    @Override
    public <T> ResponseEntity<T> billRTPReq(String tenantId, String correlationId, String callbackUrl, String billerId,
            BillRTPReqDTO billRTPReqDTO) throws ExecutionException, InterruptedException {
        try {
            PhErrorDTO phErrorDTO = billRTPReqService.billRtpReq(tenantId, correlationId, callbackUrl, billerId, billRTPReqDTO);

            if (phErrorDTO != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body((T) phErrorDTO);
            }

        } catch (Exception e) {
            logger.info(e.getMessage());
            ResponseDTO responseDTO = new ResponseDTO(FAILED_RESPONSE_CODE.getValue(), FAILED_RESPONSE_MESSAGE.getValue(), correlationId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body((T) responseDTO);
        }
        ResponseDTO responseDTO = new ResponseDTO(SUCCESS_RESPONSE_CODE.getValue(), SUCCESS_RESPONSE_MESSAGE.getValue(), correlationId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body((T) responseDTO);
    }
}
