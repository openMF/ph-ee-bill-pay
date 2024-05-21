package org.mifos.pheebillpay.camel.routes;

import static org.mifos.pheebillpay.utils.BillPayEnum.SUCCESS_RESPONSE_CODE;
import static org.mifos.pheebillpay.utils.BillPayEnum.SUCCESS_RESPONSE_MESSAGE;

import org.apache.camel.Exchange;
import org.json.JSONObject;
import org.mifos.connector.common.camel.ErrorHandlerRouteBuilder;
import org.mifos.pheebillpay.data.ResponseDTO;
import org.mifos.pheebillpay.zeebe.ZeebeVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BillRTPRespRouteBuilder extends ErrorHandlerRouteBuilder {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ResponseDTO responseDTO;

    @Override
    public void configure() {

        from("direct:bill-rtp-resp").routeId("bill-rtp-resp").log("Sending response for bill rtp request")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200)).setBody(exchange -> {
                    JSONObject response = setResponseBody(exchange.getProperty(ZeebeVariables.CLIENTCORRELATIONID).toString());
                    exchange.setProperty("response", response);
                    return response;
                });

        from("direct:aync-response").routeId("aync-response").log("Setting response for request for bill inquiry")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(202)).setBody(constant("Request Processing"));
    }

    private JSONObject setResponseBody(String clientCorrelationId) {

        JSONObject response = new JSONObject();
        responseDTO.setResponseCode(SUCCESS_RESPONSE_CODE.toString());
        responseDTO.setResponseDescription(SUCCESS_RESPONSE_MESSAGE.toString());
        responseDTO.setRequestID(clientCorrelationId);
        response.put("BillPaymentsResponse", responseDTO);
        return response;
    }

}
