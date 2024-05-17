package org.mifos.pheebillpay.camel.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.mifos.connector.common.camel.ErrorHandlerRouteBuilder;
import org.mifos.pheebillpay.data.BillPaymentsResponseDTO;
import org.mifos.pheebillpay.zeebe.ZeebeVariables;
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
                    exchange.getIn().setHeader(ZeebeVariables.PLATFORM_TENANT, exchange.getIn().getHeader(ZeebeVariables.PLATFORM_TENANT));
                    exchange.getIn().setHeader(ZeebeVariables.CLIENTCORRELATIONID,
                            exchange.getIn().getHeader(ZeebeVariables.CLIENTCORRELATIONID));
                    exchange.getIn().setHeader(ZeebeVariables.PAYER_FSP, exchange.getIn().getHeader(ZeebeVariables.PAYER_FSP));
                    exchange.getIn().setHeader(ZeebeVariables.CALLBACK_URL, exchange.getProperty(ZeebeVariables.CALLBACK_URL));
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
        billPaymentsResponseDTO.setBillId(exchange.getIn().getHeader(ZeebeVariables.BILL_ID).toString());
        billPaymentsResponseDTO.setRequestID(exchange.getIn().getHeader(ZeebeVariables.CLIENTCORRELATIONID).toString());
        return billPaymentsResponseDTO;

    }

}
