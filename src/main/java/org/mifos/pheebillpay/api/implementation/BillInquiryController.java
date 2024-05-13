package org.mifos.pheebillpay.api.implementation;

import java.util.concurrent.ExecutionException;
import org.mifos.pheebillpay.api.definition.BillInquiryApi;
import org.mifos.pheebillpay.data.BillInquiryResponseDTO;
import org.mifos.pheebillpay.service.BillInquiryService;
import org.mifos.pheebillpay.service.ValidateHeaders;
import org.mifos.pheebillpay.utils.HeaderConstants;
import org.mifos.pheebillpay.validators.HeaderValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController()
public class BillInquiryController implements BillInquiryApi {

    @Autowired
    private BillInquiryService billInquiryService;

    @Override
    @ValidateHeaders(requiredHeaders = { HeaderConstants.PLATFORM_TENANT_ID, HeaderConstants.X_CORRELATION_ID,
            HeaderConstants.X_CALLBACKURL,
            HeaderConstants.PAYER_FSP_ID }, validatorClass = HeaderValidator.class, validationFunction = "validateBillInquiryRequest")
    public <T> ResponseEntity<T> billInquiry(String tenantId, String correlationId, String callbackURL, String payerFspId, String billId,
            String field) throws ExecutionException, InterruptedException {
        BillInquiryResponseDTO billInquiryResponseDTO = new BillInquiryResponseDTO();
        try {
            billInquiryResponseDTO
                    .setTransactionId(billInquiryService.billInquiry(tenantId, correlationId, callbackURL, payerFspId, billId, field));

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
        if (billInquiryResponseDTO.getTransactionId().equals("Exception in starting workflow")
                || billInquiryResponseDTO.getTransactionId().equals("Participant Not Onboarded")) {
            return (ResponseEntity<T>) ResponseEntity.status(HttpStatus.NOT_FOUND).body(billInquiryResponseDTO);
        } else {
            return (ResponseEntity<T>) ResponseEntity.status(HttpStatus.ACCEPTED).body(billInquiryResponseDTO);
        }
    }
}
