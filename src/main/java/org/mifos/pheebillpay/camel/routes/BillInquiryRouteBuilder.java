package org.mifos.pheebillpay.camel.routes;

import static org.mifos.pheebillpay.utils.BillPayEnum.FAILED_RESPONSE_CODE;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.mifos.connector.common.camel.ErrorHandlerRouteBuilder;
import org.mifos.pheebillpay.data.Bill;
import org.mifos.pheebillpay.data.ResponseDTO;
import org.mifos.pheebillpay.zeebe.ZeebeVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BillInquiryRouteBuilder extends ErrorHandlerRouteBuilder {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private Bill billDetails;

    @Autowired
    ResponseDTO responseDTO;

    @Override
    public void configure() {

        from("direct:bill-inquiry-response").routeId("bill-inquiry-response").log("Triggering callback for bill inquiry response")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200)).process(exchange -> {
                    if (exchange.getProperty(ZeebeVariables.BILL_INQUIRY_RESPONSE) != null) {
                        Object obj = exchange.getProperty(ZeebeVariables.BILL_INQUIRY_RESPONSE);
                        ObjectMapper objectMapper = new ObjectMapper();
                        String jsonString = objectMapper.writeValueAsString(obj);
                        exchange.getIn().setBody(jsonString);
                        logger.debug("Bill Inquiry Response: {} ", jsonString);
                    } else {
                        exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400));
                        responseDTO = setFailureMessage(exchange);
                        ObjectMapper objectMapper = new ObjectMapper();
                        String jsonString = objectMapper.writeValueAsString(responseDTO);
                        exchange.getIn().setBody(jsonString);
                        logger.debug("Bill Inquiry Failure Response: {}", jsonString);
                    }
                }).log(LoggingLevel.DEBUG, "Sending bill inquiry response to callback URL: ${exchangeProperty.X-CallbackURL}")
                .toD("${exchangeProperty.X-CallbackURL}" + "?bridgeEndpoint=true&throwExceptionOnFailure=false").process(exchange -> {
                    // Access the HTTP response code
                    int responseCode = exchange.getMessage().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class);
                    logger.info(String.valueOf(responseCode));
                    String responseBody = exchange.getIn().getBody(String.class);
                    logger.info(responseBody);

                });
    }

    private ResponseDTO setFailureMessage(Exchange exchange) {
        ResponseDTO responseDTO1 = new ResponseDTO();
        responseDTO1.setResponseCode(FAILED_RESPONSE_CODE.getValue());
        responseDTO1.setResponseDescription(exchange.getProperty(ZeebeVariables.ERROR_INFORMATION).toString());
        responseDTO1.setRequestID(exchange.getProperty(ZeebeVariables.CLIENTCORRELATIONID).toString());
        return responseDTO1;
    }

}
