package org.mifos.pheebillpay.api.definition;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.concurrent.ExecutionException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "GOV")
public interface BillInquiryApi {

    @Operation(summary = "Bill Inquiry API from Payer FSP to PBB")
    @GetMapping("/bills/{billId}")
    <T> ResponseEntity<T> billInquiry(@RequestHeader(value = "Platform-TenantId") String tenantId,
            @RequestHeader(value = "X-CorrelationID") String correlationId, @RequestHeader(value = "X-CallbackURL") String callbackURL,
            @RequestHeader(value = "Payer-FSP-Id") String payerFspId, @PathVariable(value = "billId") String billId,
            @RequestParam(value = "fields", defaultValue = "inquiry") String field) throws ExecutionException, InterruptedException;
}
