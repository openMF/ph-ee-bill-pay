package org.mifos.pheebillpay.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BillStatusResponseDTO {

    private String responseCode;
    private String responseDescription;
    private String rtpId;
    private String requestStatus;
    private String paymentReferenceID;
    private String voucherNumber;
    private long lastUpdateDate;

}
