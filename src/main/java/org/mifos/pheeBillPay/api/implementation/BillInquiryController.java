package org.mifos.pheeBillPay.api.implementation;

import java.util.concurrent.ExecutionException;
import org.mifos.pheeBillPay.api.definition.BillInquiryApi;
import org.mifos.pheeBillPay.data.BillInquiryResponseDTO;
import org.mifos.pheeBillPay.service.BillInquiryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController()
public class BillInquiryController implements BillInquiryApi {

    @Autowired
    private BillInquiryService billInquiryService;

    @Override
    public ResponseEntity<BillInquiryResponseDTO> billInquiry(String tenantId, String correlationId, String callbackURL, String payerFspId,
            String billId, String field) throws ExecutionException, InterruptedException {
        BillInquiryResponseDTO billInquiryResponseDTO = new BillInquiryResponseDTO();
        try {
            billInquiryResponseDTO
                    .setTransactionId(billInquiryService.billInquiry(tenantId, correlationId, callbackURL, payerFspId, billId, field));

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(billInquiryResponseDTO);
    }
}
