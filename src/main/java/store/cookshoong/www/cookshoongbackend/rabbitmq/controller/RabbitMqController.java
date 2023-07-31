package store.cookshoong.www.cookshoongbackend.rabbitmq.controller;

import static store.cookshoong.www.cookshoongbackend.config.RabbitMqConfig.DEAD_LETTER_QUEUE_NAME;
import static store.cookshoong.www.cookshoongbackend.config.RabbitMqConfig.QUEUE_NAME;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import store.cookshoong.www.cookshoongbackend.coupon.model.request.UpdateProvideCouponRequestDto;
import store.cookshoong.www.cookshoongbackend.coupon.service.ProvideCouponService;

/**
 * rabbitMq 통해 메시지를 수신하는 컴포넌트.
 *
 * @author eora21 (김주호)
 * @since 2023.07.28
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMqController {
    private final ProvideCouponService provideCouponService;

    /**
     * 큐에서 메시지를 수신하여 서비스를 수행.
     *
     * @param updateProvideCouponRequestDto the update provide coupon request
     */
    @RabbitListener(queues = QUEUE_NAME, containerFactory = "simpleRabbitListenerContainerFactory")
    public void receiveMessageFromQueue(UpdateProvideCouponRequestDto updateProvideCouponRequestDto) {
        provideCouponService.provideCouponToAccountByEvent(updateProvideCouponRequestDto);
    }

    /**
     * 처리하지 못 한 메시지를 수신하여 로그를 작성.
     *
     * @param message the message
     */
    @RabbitListener(queues = DEAD_LETTER_QUEUE_NAME)
    public void processFailedMessages(Message message) {
        log.error("dead letter message: {}", message.toString());
    }
}
