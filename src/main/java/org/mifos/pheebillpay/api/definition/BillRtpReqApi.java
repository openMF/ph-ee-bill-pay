package org.mifos.pheebillpay.api.definition;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.concurrent.ExecutionException;
import org.mifos.pheebillpay.data.BillRTPReqDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "GOV")
public interface BillRtpReqApi {

    @Operation(summary = "Bill RTP Req API from Bill Agg to PBB")
    @PostMapping("/billTransferRequests")
    <T> ResponseEntity<T> billRTPReq(@RequestHeader(value = "X-Platform-TenantId") String tenantId,
            @RequestHeader(value = "X-Client-Correlation-ID") String correlationId,
            @RequestHeader(value = "X-Callback-URL") String callbackUrl, @RequestHeader(value = "X-Biller-Id") String billerId,
            @RequestBody BillRTPReqDTO billRTPReqDTO) throws ExecutionException, InterruptedException;
}
