package org.mifos.pheebillpay.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrasactionDTO {

    private int id;
    private String workflowInstanceKey;
    private String transactionId;
    private long startedAt;
    private long completedAt;
    private String state;
    private String payeeDfspId;
    private String payeePartyId;
    private String payeePartyIdType;
    private Double payeeFee;
    private String payeeQuoteCode;
    private String payerDfspId;
    private String payerPartyId;
    private String payerPartyIdType;
    private Double payerFee;
    private String payerQuoteCode;
    private Double amount;
    private String currency;
    private String direction;
    private String authType;
    private String initiatorType;
    private String scenario;
    private String externalId;
    private String clientCorrelationId;
    private String errorInformation;

}
