package org.mifos.pheebillpay.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Component
public class ResponseDTO {

    private String responseCode;
    private String responseDescription;
    private String requestID;
}
