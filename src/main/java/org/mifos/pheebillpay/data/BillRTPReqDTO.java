package org.mifos.pheebillpay.data;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BillRTPReqDTO implements Serializable {

    private String clientCorrelationId;
    private String billID;
    private String requestType;
    private PayerFSPDetail payerFspDetails;
    private Alias alias;
    private Bill billDetails;
    private Map<String, Object> additionalProperties = new HashMap<>();

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        additionalProperties.put(name, value);
    }

}
