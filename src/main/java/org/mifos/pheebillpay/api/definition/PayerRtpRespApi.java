package org.mifos.pheebillpay.api.definition;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.concurrent.ExecutionException;
import org.mifos.pheebillpay.data.PayerRTPResponse;
import org.mifos.pheebillpay.data.ResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "GOV")
public interface PayerRtpRespApi {

    @Operation(summary = "Bill RTP Resp API from PFI to PBB")
    @PutMapping("/billTransferRequests")
    ResponseEntity<ResponseDTO> billRTPResp(@RequestHeader(value = "X-Platform-TenantId") String tenantId,
            @RequestHeader(value = "X-Client-Correlation-ID") String correlationId, @RequestHeader(value = "X-Biller-Id") String billerId,
            @RequestBody PayerRTPResponse payerRTPResponse) throws ExecutionException, InterruptedException;
}
