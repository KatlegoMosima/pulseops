
package za.co.pulseops.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class AnalyticsClientService {

    private final RestClient restClient;

    public AnalyticsClientService(
            RestClient.Builder builder,
            @Value("${analytics.service.url}") String analyticsServiceUrl) {

        this.restClient = builder
                .baseUrl(analyticsServiceUrl)
                .build();
    }

    public Map<String, Object> analyse(Object requestBody) {

        return restClient.post()
                .uri("/analyse")
                .body(requestBody)
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});
    }
}
