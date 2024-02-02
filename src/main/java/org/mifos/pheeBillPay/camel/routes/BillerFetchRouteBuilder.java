package org.mifos.pheeBillPay.camel.routes;

import org.mifos.connector.common.camel.ErrorHandlerRouteBuilder;
import org.mifos.pheeBillPay.data.Bill;
import org.mifos.pheeBillPay.data.BillInquiryResponseDTO;
import org.mifos.pheeBillPay.properties.BillerDetails;
import org.mifos.pheeBillPay.properties.BillerDetailsProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static org.mifos.pheeBillPay.zeebe.ZeebeVariables.BILL_ID;
import static org.mifos.pheeBillPay.zeebe.ZeebeVariables.ERROR_INFORMATION;

@Component
public class BillerFetchRouteBuilder extends ErrorHandlerRouteBuilder {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    BillInquiryResponseDTO billInquiryResponseDTO;

    @Autowired
    private Bill billDetails;

    @Value("${billPay.billIdEmpty}")
    private String billIdEmpty;
    @Value("${billPay.billIdEmptyOriginal}")
    private String billIdEmptyOriginal;

    @Autowired
    private BillerDetailsProperties billerDetailsProperties;

    @Override
    public void configure() {

        from("direct:biller-fetch").routeId("biller-fetch").log("Received request for biller fetch").process(exchange -> {
            String billId = exchange.getIn().getHeader("billId").toString();
            logger.debug("Bill Id: {}", billId);
            BillerDetails billerDetails = getBillDetails(billId);
            if (billerDetails != null) {
                if(billerDetails.getId().equals(billId)) {
                    if(billId.equals(billIdEmptyOriginal)){
                        exchange.setProperty(BILL_ID,billIdEmpty);
                    }
                    else {
                        exchange.setProperty(BILL_ID,billId);
                    }
                    exchange.setProperty("billerDetails", billerDetails);
                    exchange.setProperty("billerId", billerDetails.getId());
                    exchange.setProperty("billerName", billerDetails.getBiller());
                    exchange.setProperty("billerType", billerDetails.getBillerCategory());
                    exchange.setProperty("billerAccount", billerDetails.getBillerAccount());
                    exchange.setProperty("billerFetchFailed", false);
                }
                else {
                    logger.debug("Biller details not found for bill id: {}", billId);
                    exchange.setProperty(ERROR_INFORMATION,
                            "Unindentified Biller: Bill Id does not exist in biller table");
                    exchange.setProperty("billerFetchFailed", true);
                }
            } else {
                logger.debug("Biller details not found for bill id: {}", billId);
                exchange.setProperty(ERROR_INFORMATION,
                        "Unindentified Biller: Payer FI prefix does not match");
                exchange.setProperty("billerDetails", null);
                exchange.setProperty("billerFetchFailed", true);;
            }

        });

    }

    private BillerDetails getBillDetails(String billId) {
        boolean flag = false;
        BillerDetails billerDetails1 = new BillerDetails();
        for (BillerDetails billerDetails : billerDetailsProperties.getDetails()) {
            logger.info("Biller Details: {}", billerDetails.toString());
            String prefix = billId.substring(0, 2);
            if (billerDetails.getId().equals(billId)) {
                return billerDetails;
            }
            else if(billerDetails.getId().contains(prefix)){
              flag = true;
              billerDetails1 = billerDetails;

            }
        }
        if(flag) {
            return billerDetails1;
        }
        return null;
    }
}
