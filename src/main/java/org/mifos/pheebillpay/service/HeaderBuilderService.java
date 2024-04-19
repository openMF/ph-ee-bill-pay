package org.mifos.pheebillpay.service;

import java.util.Set;
import org.mifos.pheebillpay.utils.HeaderConstants;
import org.mifos.pheebillpay.utils.Headers;
import org.springframework.stereotype.Component;

@Component
public class HeaderBuilderService {

    public Set<String> buildHeadersForBillInquiryAPI() {
        Headers.HeaderBuilder headerBuilder = new Headers.HeaderBuilder();

        headerBuilder.addHeader(HeaderConstants.PLATFORM_TENANT_ID, "").addHeader(HeaderConstants.X_CORRELATION_ID, "")
                .addHeader(HeaderConstants.X_CALLBACK_URL, "").addHeader(HeaderConstants.PAYER_FSP_ID, "");

        return headerBuilder.build().getHeadersKey();
    }

    public Set<String> buildHeadersForBillPaymentsAPI() {
        Headers.HeaderBuilder headerBuilder = new Headers.HeaderBuilder();

        headerBuilder.addHeader(HeaderConstants.X_PLATFORM_TENANT_ID, "").addHeader(HeaderConstants.X_CORRELATION_ID, "")
                .addHeader(HeaderConstants.X_CALLBACK_URL, "").addHeader(HeaderConstants.X_PAYER_FSP_ID, "");

        return headerBuilder.build().getHeadersKey();
    }

    public Set<String> buildHeadersForBillRtpReqAPI() {
        Headers.HeaderBuilder headerBuilder = new Headers.HeaderBuilder();

        headerBuilder.addHeader(HeaderConstants.X_PLATFORM_TENANT_ID, "").addHeader(HeaderConstants.X_CLIENT_CORRELATION_ID, "")
                .addHeader(HeaderConstants.X_CALLBACK_URL, "").addHeader(HeaderConstants.X_REGISTERING_INSTITUTION_ID, "")
                .addHeader(HeaderConstants.X_BILLER_ID, "");

        return headerBuilder.build().getHeadersKey();
    }
}
