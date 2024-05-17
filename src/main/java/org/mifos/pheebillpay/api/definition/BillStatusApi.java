package org.mifos.pheebillpay.api.definition;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.concurrent.ExecutionException;
import org.mifos.pheebillpay.data.BillStatusReqDTO;
import org.mifos.pheebillpay.data.BillStatusResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "GOV")
public interface BillStatusApi {

    @Operation(summary = "Bill Status API ")
    @GetMapping("/transferRequests/{correlationId}")
    ResponseEntity<BillStatusResponseDTO> billStatus(@RequestHeader(value = "Platform-TenantId") String tenantId,
            @RequestHeader(value = "X-CorrelationID") String correlationId, @RequestHeader(value = "X-Biller-Id") String billerId,
            @RequestHeader(value = "billId") String billId, @PathVariable(value = "correlationId") String transferRequestId,
            @RequestBody BillStatusReqDTO billStatusReqDTO) throws ExecutionException, InterruptedException;
}
