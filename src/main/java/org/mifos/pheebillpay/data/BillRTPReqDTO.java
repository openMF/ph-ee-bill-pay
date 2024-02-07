package org.mifos.pheebillpay.data;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BillRTPReqDTO implements Serializable {

    private String clientCorrelationId;
    private String billId;
    private String requestType;
    private PayerFSPDetail payerFspDetail;
    private Alias alias;
    private Bill bill;

}
