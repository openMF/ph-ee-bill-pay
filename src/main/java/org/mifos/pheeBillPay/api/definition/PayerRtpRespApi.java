package org.mifos.pheeBillPay.api.definition;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.mifos.pheeBillPay.data.PayerRTPResponse;
import org.mifos.pheeBillPay.data.ResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.concurrent.ExecutionException;

@Tag(name = "GOV")
public interface PayerRtpRespApi {

    @Operation(
            summary = "Bill RTP Resp API from PFI to PBB")
    @PutMapping("/billTransferRequests")
    ResponseEntity<ResponseDTO> billRTPResp(@RequestHeader(value="X-Platform-TenantId") String tenantId,
                                            @RequestHeader(value="X-Client-Correlation-ID") String correlationId,
                                            @RequestHeader(value = "X-Biller-Id") String billerId,
                                            @RequestBody PayerRTPResponse payerRTPResponse)
            throws ExecutionException, InterruptedException;
}