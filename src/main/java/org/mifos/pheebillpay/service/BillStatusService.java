package org.mifos.pheebillpay.service;

import static org.mifos.pheebillpay.zeebe.ZeebeVariables.PLATFORM_TENANT;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.mifos.pheebillpay.data.BillStatusReqDTO;
import org.mifos.pheebillpay.data.TrasactionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class BillStatusService {

    private Logger logger = LoggerFactory.getLogger(BillStatusService.class);

    @Value("${operations.url}")
    private String baseUrl;

    @Value("${operations.endpoint.transactionReq}")
    private String transactionReqEndpoint;

    private ObjectMapper objectMapper = new ObjectMapper();

    String transactionId;

    public TrasactionDTO billStatus(String tenantId, String correlationId, String billerId, String billId, String transferRequestId,
            BillStatusReqDTO body) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, JsonProcessingException {
        TrasactionDTO trasactionDTO = new TrasactionDTO();
        logger.info("Bill Status Implementation");
        String url = baseUrl + transactionReqEndpoint + "&clientCorrelationId={clientid}";
        logger.info("Url {}", url);
        RestTemplate restTemplate = new RestTemplate();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLContext(new SSLContextBuilder().loadTrustMaterial(null, (certificate, authType) -> true).build())
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));

        HttpHeaders headers = new HttpHeaders();
        headers.set(PLATFORM_TENANT, tenantId);
        headers.setContentType(MediaType.APPLICATION_JSON);
        url = UriComponentsBuilder.fromUriString(url).buildAndExpand(transferRequestId).toUriString();

        HttpEntity<String> entity = new HttpEntity<>(headers);
        TrasactionDTO txnResponseDTO = new TrasactionDTO();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        logger.info("Response from operations: " + response.getBody());
        JsonNode contentNode = objectMapper.readTree(response.getBody()).get("content");
        if (contentNode.isEmpty()) {
            return txnResponseDTO;
        } else {
            txnResponseDTO = objectMapper.treeToValue(contentNode.get(0), TrasactionDTO.class);
        }
        return txnResponseDTO;
    }

}
