package org.mifos.pheeBillPay.api.definition;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.concurrent.ExecutionException;
import org.mifos.pheeBillPay.data.BillRTPReqDTO;
import org.mifos.pheeBillPay.data.ResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "GOV")
public interface BillRtpReqApi {

    @Operation(summary = "Bill RTP Req API from Bill Agg to PBB")
    @PostMapping("/billTransferRequests")
    ResponseEntity<ResponseDTO> billRTPReq(@RequestHeader(value = "X-Platform-TenantId") String tenantId,
            @RequestHeader(value = "X-Client-Correlation-ID") String correlationId,
            @RequestHeader(value = "X-Callback-URL") String callbackUrl, @RequestParam(value = "X-Biller-Id") String billerId,
            @RequestBody BillRTPReqDTO billRTPReqDTO) throws ExecutionException, InterruptedException;
}
