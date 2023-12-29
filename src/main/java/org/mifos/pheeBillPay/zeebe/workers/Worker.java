package org.mifos.pheeBillPay.zeebe.workers;

public enum Worker {

    PAYER_RTP("payerRtpRequest"),
    BILLER_RTP_RESPONSE("billerRtpResponse");

    private final String value;

    private Worker(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
