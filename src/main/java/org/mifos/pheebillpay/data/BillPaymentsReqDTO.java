package org.mifos.pheebillpay.data;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

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

    private Map<String, Object> additionalProperties = new HashMap<>();

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        additionalProperties.put(name, value);
    }

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

    public BillPaymentsReqDTO(String clientCorrelationId, String billInquiryRequestId, String billId, String paymentReferenceID) {
        this.clientCorrelationId = clientCorrelationId;
        this.billInquiryRequestId = billInquiryRequestId;
        this.billId = billId;
        this.paymentReferenceID = paymentReferenceID;
    }

}
