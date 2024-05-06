package org.mifos.pheebillpay.api.implementation;

import java.util.Set;
import java.util.concurrent.ExecutionException;
import javax.servlet.http.HttpServletRequest;
import org.mifos.connector.common.channel.dto.PhErrorDTO;
import org.mifos.pheebillpay.api.definition.BillInquiryApi;
import org.mifos.pheebillpay.data.BillInquiryResponseDTO;
import org.mifos.pheebillpay.service.BillInquiryService;
import org.mifos.pheebillpay.service.PrepareHeader;
import org.mifos.pheebillpay.service.PrepareHeaderImpl;
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

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HeaderValidator headerValidator;

    @PrepareHeader(values = { HeaderConstants.PLATFORM_TENANT_ID, HeaderConstants.X_CORRELATION_ID, HeaderConstants.X_CALLBACKURL,
            HeaderConstants.PAYER_FSP_ID })
    Set<String> requiredHeaders;

    public BillInquiryController() throws IllegalAccessException {
        requiredHeaders = PrepareHeaderImpl.process(this);
    }

    @Override
    public <T> ResponseEntity<T> billInquiry(String tenantId, String correlationId, String callbackURL, String payerFspId, String billId,
            String field) throws ExecutionException, InterruptedException {
        BillInquiryResponseDTO billInquiryResponseDTO = new BillInquiryResponseDTO();
        PhErrorDTO phErrorDTO = headerValidator.validateBillInquiryRequest(requiredHeaders, request);
        if (phErrorDTO != null) {
            return (ResponseEntity<T>) ResponseEntity.status(HttpStatus.BAD_REQUEST).body(phErrorDTO);
        }
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
