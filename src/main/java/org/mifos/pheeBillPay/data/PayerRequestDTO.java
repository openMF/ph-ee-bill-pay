package org.mifos.pheeBillPay.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PayerRequestDTO {
    private String requestId;
    private String transactionId;
    private Integer rtpId;
    private BillDetails billDetails;

}
