package store.cookshoong.www.cookshoongbackend.common.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.time.Duration;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import store.cookshoong.www.cookshoongbackend.common.property.RabbitMqProperties;
import store.cookshoong.www.cookshoongbackend.common.service.SKMService;

/**
 * rabbitmq 통신 설정.
 *
 * @author eora21 (김주호)
 * @since 2023.07.28
 */
@Configuration
public class RabbitMqConfig {
    public static final String EXCHANGE_NAME = "coupon";
    public static final String QUEUE_NAME = "event";
    private static final String DEAD_LETTER = "dead.";
    private static final String DEAD_LETTER_EXCHANGE_NAME = DEAD_LETTER + EXCHANGE_NAME;
    public static final String DEAD_LETTER_QUEUE_NAME = DEAD_LETTER + QUEUE_NAME;
    private static final int MAX_ATTEMPTS = 3;
    private static final long INITIAL_INTERVAL = 3L;
    private static final long MULTIPLIER = 2L;
    private static final long MAX_INTERVAL = 10L;


    /**
     * skm 정보에서 rabbitMq 설정값을 Bean 적용.
     *
     * @param rabbitMqKeyId the rabbit mq key id
     * @param skmService    the skm service
     * @return the rabbit mq properties
     * @throws JsonProcessingException the json processing exception
     */
    @Bean
    public RabbitMqProperties rabbitMqProperties(@Value("${cookshoong.skm.keyid.rabbitmq}") String rabbitMqKeyId,
                                                 SKMService skmService) throws JsonProcessingException {
        return skmService.fetchSecrets(rabbitMqKeyId, RabbitMqProperties.class);
    }

    /**
     * rabbitMq 연결을 위한 팩토리 bean 선언.
     *
     * @param rabbitMqProperties the rabbit mq properties
     * @return the connection factory
     */
    @Bean
    public ConnectionFactory connectionFactory(RabbitMqProperties rabbitMqProperties) {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(rabbitMqProperties.getHostname());
        connectionFactory.setUsername(rabbitMqProperties.getUsername());
        connectionFactory.setPassword(rabbitMqProperties.getPassword());
        connectionFactory.setVirtualHost(rabbitMqProperties.getVirtualHost());
        connectionFactory.setPort(rabbitMqProperties.getPort());

        return connectionFactory;
    }

    /**
     * 쿠폰 이벤트 담당 큐.
     *
     * @return the queue
     */
    @Bean
    public Queue couponEventQueue() {
        return QueueBuilder
            .durable(QUEUE_NAME)
            .deadLetterExchange(DEAD_LETTER_EXCHANGE_NAME)
            .build();
    }

    /**
     * 쿠폰 이벤트 메시지 처리 중 Dead letter 발생 시 사용될 큐.
     *
     * @return the queue
     */
    @Bean
    public Queue deadLetterCouponQueue() {
        return QueueBuilder
            .durable(DEAD_LETTER_QUEUE_NAME)
            .build();
    }

    /**
     * 쿠폰 이벤트 큐와 1:1로 매핑될 익스체인지.
     *
     * @return the exchange
     */
    @Bean
    public Exchange couponExchange() {
        return new DirectExchange(EXCHANGE_NAME, true, false);
    }

    /**
     * Dead letter 담당 익스체인지.
     *
     * @return the exchange
     */
    @Bean
    public Exchange deadLetterCouponExchange() {
        return new FanoutExchange(DEAD_LETTER_EXCHANGE_NAME, true, false);
    }

    /**
     * 쿠폰 익스체인지와 쿠폰 큐 연결.
     *
     * @return the binding
     */
    @Bean
    public Binding couponQueueBinding() {
        return BindingBuilder
            .bind(couponEventQueue())
            .to(couponExchange())
            .with(QUEUE_NAME)
            .noargs();
    }

    /**
     * Dead letter 익스체인지, 큐 바인딩.
     *
     * @return the binding
     */
    @Bean
    public Binding deadLetterCouponQueueBinding() {
        return BindingBuilder
            .bind(deadLetterCouponQueue())
            .to(deadLetterCouponExchange())
            .with(DEAD_LETTER_QUEUE_NAME)
            .noargs();
    }

    /**
     * rabbitTemplate object - json 변경을 위한 컨버터.
     *
     * @return the message converter
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 메시지 발행 시 사용될 template.
     *
     * @param connectionFactory the connection factory
     * @return the rabbit template
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        rabbitTemplate.setExchange(EXCHANGE_NAME);
        rabbitTemplate.setRoutingKey(QUEUE_NAME);
        return rabbitTemplate;
    }

    /**
     * 메시지 수신 시 규약을 담당할 팩토리.
     *
     * @param connFactory the conn factory
     * @return the simple rabbit listener container factory
     */
    @Bean
    public SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory(ConnectionFactory connFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connFactory);
        factory.setDefaultRequeueRejected(false);
        factory.setChannelTransacted(true);
        factory.setMessageConverter(jsonMessageConverter());
        factory.setAdviceChain(
            RetryInterceptorBuilder.stateless()
                .maxAttempts(MAX_ATTEMPTS)
                .backOffOptions(
                    Duration.ofSeconds(INITIAL_INTERVAL).toMillis(),
                    MULTIPLIER,
                    Duration.ofSeconds(MAX_INTERVAL).toMillis()
                )
                .recoverer(new RejectAndDontRequeueRecoverer())
                .build());

        return factory;
    }

}
