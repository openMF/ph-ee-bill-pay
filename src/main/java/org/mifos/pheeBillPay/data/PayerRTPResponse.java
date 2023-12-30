package org.mifos.pheeBillPay.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PayerRTPResponse {

    private String txnId;
    private String billId;
    private String rtpStatus;
    private String rejectReason;
}
