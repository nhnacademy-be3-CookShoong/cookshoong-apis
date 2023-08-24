package store.cookshoong.www.cookshoongbackend.delivery.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.cookshoong.www.cookshoongbackend.delivery.adapter.DeliveryAdapter;
import store.cookshoong.www.cookshoongbackend.delivery.model.request.SendDeliveryRequestDto;
import store.cookshoong.www.cookshoongbackend.order.entity.Order;
import store.cookshoong.www.cookshoongbackend.order.exception.OrderNotFoundException;
import store.cookshoong.www.cookshoongbackend.order.repository.OrderRepository;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;

/**
 * 배송 서비스.
 *
 * @author eora21 (김주호)
 * @since 2023.08.23
 */
@Service
@RequiredArgsConstructor
public class DeliveryService {
    private final OrderRepository orderRepository;
    private final DeliveryAdapter deliveryAdapter;

    @Value("${cookshoong.delivery.callback}")
    private String callbackUrl;

    /**
     * Send delivery.
     *
     * @param orderCode the order code
     */
    @Transactional(readOnly = true)
    public void sendDelivery(UUID orderCode) {
        Order order = orderRepository.findById(orderCode)
            .orElseThrow(OrderNotFoundException::new);

        Store store = order.getStore();
        SendDeliveryRequestDto sendDeliveryRequestDto = new SendDeliveryRequestDto(order.getDeliveryAddress(),
                order.getDeliveryCost(), store.getName(), store.getAddress().getMainPlace(), orderCode, callbackUrl);

        deliveryAdapter.sendDeliveryRequest(sendDeliveryRequestDto);
    }
}
