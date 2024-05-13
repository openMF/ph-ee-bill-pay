package org.mifos.pheebillpay.api.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.mifos.connector.common.channel.dto.PhErrorDTO;
import org.mifos.pheebillpay.api.definition.BillPaymentsApi;
import org.mifos.pheebillpay.data.BillInquiryResponseDTO;
import org.mifos.pheebillpay.data.BillPaymentsReqDTO;
import org.mifos.pheebillpay.service.BillPaymentsService;
import org.mifos.pheebillpay.service.ValidateHeaders;
import org.mifos.pheebillpay.utils.HeaderConstants;
import org.mifos.pheebillpay.validators.BillPayValidator;
import org.mifos.pheebillpay.validators.HeaderValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class BillPaymentsController implements BillPaymentsApi {

    @Autowired
    private BillPaymentsService billPaymentsService;

    @Autowired
    private BillPayValidator billPayValidator;

    @Override
    @ValidateHeaders(requiredHeaders = { HeaderConstants.X_PLATFORM_TENANT_ID, HeaderConstants.X_CORRELATION_ID,
            HeaderConstants.X_CALLBACKURL,
            HeaderConstants.X_PAYER_FSP_ID }, validatorClass = HeaderValidator.class, validationFunction = "validateBillPaymentRequest")
    public <T> ResponseEntity<T> billPayments(String tenantId, String correlationId, String callbackURL, String payerFspId,
            BillPaymentsReqDTO body) throws ExecutionException, JsonProcessingException {
        BillInquiryResponseDTO billInquiryResponseDTO = new BillInquiryResponseDTO();

        // validate for request body
        PhErrorDTO phErrorDTO = billPayValidator.validateBillPayments(body);
        if (phErrorDTO != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = objectMapper.writeValueAsString(phErrorDTO);

            log.info("Error DTO is : {}", jsonResponse);

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
