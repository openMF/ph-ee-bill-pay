package org.mifos.pheebillpay.api.implementation;

import java.util.concurrent.ExecutionException;
import org.mifos.connector.common.channel.dto.PhErrorDTO;
import org.mifos.pheebillpay.api.definition.BillPaymentsApi;
import org.mifos.pheebillpay.data.BillInquiryResponseDTO;
import org.mifos.pheebillpay.data.BillPaymentsReqDTO;
import org.mifos.pheebillpay.service.BillPaymentsService;
import org.mifos.pheebillpay.validators.BillPayValidator;
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

    @Override
    public <T> ResponseEntity<T> billPayments(String tenantId, String correlationId, String callbackURL, String payerFspId,
            BillPaymentsReqDTO body) throws ExecutionException {
        BillInquiryResponseDTO billInquiryResponseDTO = new BillInquiryResponseDTO();

        PhErrorDTO phErrorDTO = billPayValidator.validateBillPayments(body);
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
