package org.mifos.pheebillpay.api.implementation;

import java.util.Enumeration;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import javax.servlet.http.HttpServletRequest;
import org.mifos.connector.common.channel.dto.PhErrorDTO;
import org.mifos.pheebillpay.api.definition.BillPaymentsApi;
import org.mifos.pheebillpay.data.BillInquiryResponseDTO;
import org.mifos.pheebillpay.data.BillPaymentsReqDTO;
import org.mifos.pheebillpay.service.BillPaymentsService;
import org.mifos.pheebillpay.service.PrepareHeader;
import org.mifos.pheebillpay.service.PrepareHeaderImpl;
import org.mifos.pheebillpay.utils.HeaderConstants;
import org.mifos.pheebillpay.validators.BillPayValidator;
import org.mifos.pheebillpay.validators.HeaderValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BillPaymentsController implements BillPaymentsApi {

    @Autowired
    private BillPaymentsService billPaymentsService;

    @Autowired
    private BillPayValidator billPayValidator;

    @Autowired
    private HeaderValidator headerValidator;

    @Autowired
    private HttpServletRequest request;

    @PrepareHeader(values = { HeaderConstants.X_PLATFORM_TENANT_ID, HeaderConstants.X_CORRELATION_ID, HeaderConstants.X_CALLBACKURL,
            HeaderConstants.X_PAYER_FSP_ID })
    Set<String> requiredHeaders;

    public BillPaymentsController() throws IllegalAccessException {
        requiredHeaders = PrepareHeaderImpl.process(this);
    }

    @Override
    public <T> ResponseEntity<T> billPayments(String tenantId, String correlationId, String callbackURL, String payerFspId,
            BillPaymentsReqDTO body) throws ExecutionException {
        BillInquiryResponseDTO billInquiryResponseDTO = new BillInquiryResponseDTO();

        Enumeration<String> allHeaders = request.getHeaderNames();

        // validate for headers
        PhErrorDTO phErrorDTO = headerValidator.validateBillPaymentRequest(requiredHeaders, request);
        if (phErrorDTO != null) {
            return (ResponseEntity<T>) ResponseEntity.status(HttpStatus.BAD_REQUEST).body(phErrorDTO);
        }

        // validate for request body
        phErrorDTO = billPayValidator.validateBillPayments(body);
        if (phErrorDTO != null) {
            return (ResponseEntity<T>) ResponseEntity.status(HttpStatus.BAD_REQUEST).body(phErrorDTO);
        }

        if (phErrorDTO != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body((T) phErrorDTO);
        }

        try {
            billInquiryResponseDTO
                    .setTransactionId(billPaymentsService.billPayments(tenantId, correlationId, callbackURL, payerFspId, body));

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body((T) billInquiryResponseDTO);
    }

}
