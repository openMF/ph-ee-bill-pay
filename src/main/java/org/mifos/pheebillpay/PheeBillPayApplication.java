package org.mifos.pheebillpay;

import org.mifos.pheebillpay.utils.AbstractApplicationConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

@EnableCaching
@SpringBootApplication
@ComponentScan("org.mifos.pheebillpay")
public class PheeBillPayApplication extends AbstractApplicationConfiguration {

    public static void main(String[] args) {
        SpringApplication.run(PheeBillPayApplication.class, args);
    }

}
