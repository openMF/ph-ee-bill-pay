package org.mifos.pheebillpay.api.definition;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.concurrent.ExecutionException;
import org.mifos.pheebillpay.data.BillPaymentsReqDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "GOV")
public interface BillPaymentsApi {

    @Operation(summary = "Bill Payments API from Payer FSP to PBB")
    @PostMapping("/paymentNotifications")
    <T> ResponseEntity<T> billPayments(@RequestHeader(value = "X-Platform-TenantId") String tenantId,
            @RequestHeader(value = "X-CorrelationID") String correlationId, @RequestHeader(value = "X-CallbackURL") String callbackURL,
            @RequestHeader(value = "X-PayerFSP-Id") String payerFspId, @RequestBody BillPaymentsReqDTO body)
            throws ExecutionException, InterruptedException;
}
