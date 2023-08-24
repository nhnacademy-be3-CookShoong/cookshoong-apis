package store.cookshoong.www.cookshoongbackend.delivery.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import store.cookshoong.www.cookshoongbackend.delivery.model.request.SendDeliveryRequestDto;

/**
 * 배송 요청 어댑터.
 *
 * @author eora21 (김주호)
 * @since 2023.08.23
 */
@Component
@RequiredArgsConstructor
public class DeliveryAdapter {
    private final RestTemplate restTemplate;

    @Value("${cookshoong.delivery.to}")
    private String deliveryTo;

    /**
     * 배송 요청.
     *
     * @param request the request
     */
    public void sendDeliveryRequest(SendDeliveryRequestDto request) {
        restTemplate.exchange(
            UriComponentsBuilder
                .fromUriString(deliveryTo)
                .build()
                .toUri(),
            HttpMethod.POST,
            new HttpEntity<>(request),
            new ParameterizedTypeReference<>() {
            });
    }
}
