package org.mifos.pheeBillPay.camel.routes;

import org.mifos.connector.common.camel.ErrorHandlerRouteBuilder;
import org.mifos.pheeBillPay.data.Bill;
import org.mifos.pheeBillPay.data.BillInquiryResponseDTO;
import org.mifos.pheeBillPay.properties.BillerDetails;
import org.mifos.pheeBillPay.properties.BillerDetailsProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BillerFetchRouteBuilder extends ErrorHandlerRouteBuilder {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    BillInquiryResponseDTO billInquiryResponseDTO;

    @Autowired
    private Bill billDetails;

    @Autowired
    private BillerDetailsProperties billerDetailsProperties;

    @Override
    public void configure() {

        from("direct:biller-fetch").routeId("biller-fetch").log("Received request for biller fetch").process(exchange -> {
            String billId = exchange.getIn().getHeader("billId").toString();
            logger.debug("Bill Id: " + billId);
            BillerDetails billerDetails = getBillDetails(billId);
            if (billerDetails != null) {
                exchange.setProperty("billerDetails", billerDetails);
                exchange.setProperty("billerId", billerDetails.getId());
                exchange.setProperty("billerName", billerDetails.getBiller());
                exchange.setProperty("billerType", billerDetails.getBillerCategory());
                exchange.setProperty("billerAccount", billerDetails.getBillerAccount());
                exchange.setProperty("billerFetchFailed", false);
            } else {
                logger.debug("Biller details not found for bill id: " + billId);
                exchange.setProperty("billerDetails", null);
                exchange.setProperty("billerFetchFailed", true);
            }

        });

    }

    private BillerDetails getBillDetails(String billId) {
        for (BillerDetails billerDetails : billerDetailsProperties.getDetails()) {
            logger.info("Biller Details: " + billerDetails.toString());
            if (billerDetails.getId().equals(billId)) {
                return billerDetails;
            }
        }
        return null;
    }
}
