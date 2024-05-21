package org.mifos.pheebillpay.api.implementation;

import java.util.concurrent.ExecutionException;
import org.mifos.pheebillpay.api.definition.BillStatusApi;
import org.mifos.pheebillpay.data.BillStatusReqDTO;
import org.mifos.pheebillpay.data.BillStatusResponseDTO;
import org.mifos.pheebillpay.data.TrasactionDTO;
import org.mifos.pheebillpay.service.BillStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BillStatusController implements BillStatusApi {

    @Autowired
    private BillStatusService billStatusService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public ResponseEntity<BillStatusResponseDTO> billStatus(String tenantId, String correlationId, String billerId, String billId,
            String transferRequestId, BillStatusReqDTO body) throws ExecutionException, InterruptedException {
        logger.info("Bill Status API called");
        BillStatusResponseDTO bill = new BillStatusResponseDTO();
        TrasactionDTO trasactionDTO = new TrasactionDTO();
        try {
            logger.info("Bill Status API called");
            trasactionDTO = billStatusService.billStatus(tenantId, correlationId, billerId, billId, transferRequestId, body);
            bill = convertToBillStatusResponseDTO(trasactionDTO, body);
            if (trasactionDTO.getId() == 0) {
                logger.info("Transaction not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(bill);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
        logger.info("Txn found response sent");
        return ResponseEntity.status(HttpStatus.OK).body(bill);
    }

    private BillStatusResponseDTO convertToBillStatusResponseDTO(TrasactionDTO trasactionDTO, BillStatusReqDTO body) {
        BillStatusResponseDTO bill = new BillStatusResponseDTO();
        if (trasactionDTO.getId() == 0) {
            bill.setResponseCode("01");
            bill.setResponseDescription("Transaction not found");
            bill.setRtpId(body.getRtpId());
            bill.setRequestStatus("Failed");
        } else {
            bill.setResponseCode("00");
            bill.setResponseDescription("Request Successfully received by Payments BB");
            bill.setRtpId(body.getRtpId());
            if (trasactionDTO.getState().equals("SUCCESS")) {
                bill.setRequestStatus("COM");
            } else if (trasactionDTO.getState().equals("IN_PROGRESS")) {
                bill.setRequestStatus("PND");
            } else if (trasactionDTO.getState().equals("INITIATED")) {
                bill.setRequestStatus("PND");
            } else if (trasactionDTO.getState().equals("ACCEPTED")) {
                bill.setRequestStatus("ACCEPTED");
            } else if (trasactionDTO.getState().equals("REQUEST_ACCEPTED")) {
                bill.setRequestStatus("REQUEST_ACCEPTED");
            } else if (trasactionDTO.getState().equals("FAILED")) {
                bill.setRequestStatus("FAILED");
            }
            bill.setLastUpdateDate(trasactionDTO.getCompletedAt());
        }
        return bill;
    }
}
