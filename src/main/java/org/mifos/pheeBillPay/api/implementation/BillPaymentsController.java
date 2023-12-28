package org.mifos.pheeBillPay.api.implementation;

import java.util.concurrent.ExecutionException;
import org.mifos.pheeBillPay.api.definition.BillPaymentsApi;
import org.mifos.pheeBillPay.data.BillInquiryResponseDTO;
import org.mifos.pheeBillPay.data.BillPaymentsReqDTO;
import org.mifos.pheeBillPay.service.BillPaymentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BillPaymentsController implements BillPaymentsApi {

    @Autowired
    private BillPaymentsService billPaymentsService;

    @Override
    public ResponseEntity<BillInquiryResponseDTO> billPayments(String tenantId, String correlationId, String callbackURL, String payerFspId,
            BillPaymentsReqDTO body) throws ExecutionException {
        BillInquiryResponseDTO billInquiryResponseDTO = new BillInquiryResponseDTO();
        try {
            billInquiryResponseDTO
                    .setTransactionId(billPaymentsService.billPayments(tenantId, correlationId, callbackURL, payerFspId, body));

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(billInquiryResponseDTO);
    }

}
