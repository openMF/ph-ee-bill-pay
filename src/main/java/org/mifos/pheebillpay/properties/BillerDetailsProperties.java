package org.mifos.pheebillpay.properties;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "billers")
public class BillerDetailsProperties {

    public List<BillerDetails> getDetails() {
        return details;
    }

    public void setDetails(List<BillerDetails> details) {
        this.details = details;
    }

    List<BillerDetails> details;

}
