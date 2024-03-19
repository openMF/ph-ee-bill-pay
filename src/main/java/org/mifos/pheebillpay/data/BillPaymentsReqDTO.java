package org.mifos.pheebillpay.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Component
public class BillPaymentsReqDTO implements Serializable {

    private String clientCorrelationId;
    private String billInquiryRequestId;
    private String billId;
    private String paymentReferenceID;

    @Override
    public String toString() {
        return "BillPaymentsReqDTO{" + "clientCorrelationId='" + clientCorrelationId + '\'' + ", billInquiryRequestId='"
                + billInquiryRequestId + '\'' + ", billId='" + billId + '\'' + ", paymentReferenceID='" + paymentReferenceID + '\'' + '}';
    }

    public String getClientCorrelationId() {
        return clientCorrelationId;
    }

    public void setClientCorrelationId(String clientCorrelationId) {
        this.clientCorrelationId = clientCorrelationId;
    }

    public String getBillInquiryRequestId() {
        return billInquiryRequestId;
    }

    public void setBillInquiryRequestId(String billInquiryRequestId) {
        this.billInquiryRequestId = billInquiryRequestId;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public String getPaymentReferenceID() {
        return paymentReferenceID;
    }

    public void setPaymentReferenceID(String paymentReferenceID) {
        this.paymentReferenceID = paymentReferenceID;
    }

}
