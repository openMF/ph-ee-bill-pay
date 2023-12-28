package org.mifos.pheeBillPay.camel.routes;

import static org.mifos.pheeBillPay.zeebe.ZeebeVariables.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.mifos.connector.common.camel.ErrorHandlerRouteBuilder;
import org.mifos.pheeBillPay.data.Bill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BillInquiryRouteBuilder extends ErrorHandlerRouteBuilder {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private Bill billDetails;

    @Override
    public void configure() {

        from("direct:bill-inquiry-response").routeId("bill-inquiry-response").log("Triggering callback for bill inquiry response")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200)).process(exchange -> {
                    Object obj = exchange.getProperty(BILL_INQUIRY_RESPONSE);
                    ObjectMapper objectMapper = new ObjectMapper();
                    String jsonString = objectMapper.writeValueAsString(obj);
                    exchange.getIn().setBody(jsonString);
                    logger.info("Bill Inquiry Responsejsostring: " + jsonString);
                }).log(LoggingLevel.DEBUG, "Sending bill inquiry response to callback URL: ${exchangeProperty.X-CallbackURL}")
                .toD("${exchangeProperty.X-CallbackURL}" + "?bridgeEndpoint=true&throwExceptionOnFailure=false")
                .process(exchange -> {
                    // Access the HTTP response code
                    int responseCode = exchange.getMessage().
                            getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class);
                    logger.info(String.valueOf(responseCode));
                    String responseBody = exchange.getIn().getBody(String.class);
                    logger.info(responseBody);

                });
    }

}
