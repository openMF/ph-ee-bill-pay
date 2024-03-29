package org.mifos.pheebillpay.camel.routes;

import static org.mifos.pheebillpay.zeebe.ZeebeVariables.BILL_ID;
import static org.mifos.pheebillpay.zeebe.ZeebeVariables.CALLBACK_URL;
import static org.mifos.pheebillpay.zeebe.ZeebeVariables.CLIENTCORRELATIONID;
import static org.mifos.pheebillpay.zeebe.ZeebeVariables.PAYER_FSP;
import static org.mifos.pheebillpay.zeebe.ZeebeVariables.PLATFORM_TENANT;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.mifos.connector.common.camel.ErrorHandlerRouteBuilder;
import org.mifos.pheebillpay.data.BillPaymentsResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BillPaymentNotificationRouteBuilder extends ErrorHandlerRouteBuilder {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    BillPaymentsResponseDTO billPaymentsResponseDTO;

    @Override
    public void configure() {

        from("direct:paymentNotification-response").routeId("paymentNotification-response").log("Bill Inquiry over, moving to bill payment")
                .setHeader("Content-Type", constant("application/json")).process(exchange -> {
                    BillPaymentsResponseDTO responseDTO = setResponseBody(exchange);
                    exchange.getIn().setHeader(PLATFORM_TENANT, exchange.getIn().getHeader(PLATFORM_TENANT));
                    exchange.getIn().setHeader(CLIENTCORRELATIONID, exchange.getIn().getHeader(CLIENTCORRELATIONID));
                    exchange.getIn().setHeader(PAYER_FSP, exchange.getIn().getHeader(PAYER_FSP));
                    exchange.getIn().setHeader(CALLBACK_URL, exchange.getProperty(CALLBACK_URL));
                    ObjectMapper objectMapper = new ObjectMapper();
                    String jsonString = objectMapper.writeValueAsString(responseDTO);
                    exchange.getIn().setBody(jsonString);
                }).log("Payment Notification Body: ${body}").log("Payment Notification Headers: ${headers}")
                .toD("${exchangeProperty.X-CallbackURL}" + "?bridgeEndpoint=true&throwExceptionOnFailure=false");

    }

    private BillPaymentsResponseDTO setResponseBody(Exchange exchange) {

        billPaymentsResponseDTO.setCode(exchange.getProperty("code").toString());
        billPaymentsResponseDTO.setStatus(exchange.getProperty("status").toString());
        billPaymentsResponseDTO.setReason(exchange.getProperty("reason").toString());
        billPaymentsResponseDTO.setBillId(exchange.getIn().getHeader(BILL_ID).toString());
        billPaymentsResponseDTO.setRequestID(exchange.getIn().getHeader(CLIENTCORRELATIONID).toString());
        return billPaymentsResponseDTO;

    }

}
