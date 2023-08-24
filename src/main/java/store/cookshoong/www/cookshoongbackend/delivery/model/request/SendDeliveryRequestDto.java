package store.cookshoong.www.cookshoongbackend.delivery.model.request;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;

/**
 * 배송 요청 시 사용되는 dto.
 *
 * @author eora21 (김주호)
 * @since 2023.08.23
 */
@Getter
public class SendDeliveryRequestDto {
    private static final String COOKSHOONG = "cookshoong_";

    private final LocalDateTime prepDatetime = LocalDateTime.now();

    private final String deliveryAddress;
    private final int price;
    private final String reqStore;
    private final String reqStoreAddress;
    private final String callbackId;
    private final String callbackUrl;

    private final int acceptMinSeconds;
    private final int acceptMaxSeconds;
    private final int deliveryMinSeconds;
    private final int deliveryMaxSeconds;
    private final int finishMinSeconds;
    private final int finishMaxSeconds;
    private final int acceptPercent;
    private final boolean setTime;

    /**
     * Instantiates a new Send delivery request dto.
     *
     * @param deliveryAddress the delivery address
     * @param price           the price
     * @param reqStore        the req store
     * @param reqStoreAddress the req store address
     * @param orderCode       the order code
     * @param callbackUrl     the callback url
     */
    public SendDeliveryRequestDto(String deliveryAddress, int price, String reqStore, String reqStoreAddress,
                                  UUID orderCode, String callbackUrl) {
        this.deliveryAddress = deliveryAddress;
        this.price = price;
        this.reqStore = reqStore;
        this.reqStoreAddress = reqStoreAddress;
        this.callbackId = COOKSHOONG + orderCode;
        this.callbackUrl = callbackUrl;

        this.acceptMinSeconds = 0;
        this.acceptMaxSeconds = 5;
        this.deliveryMinSeconds = 0;
        this.deliveryMaxSeconds = 5;
        this.finishMinSeconds = 0;
        this.finishMaxSeconds = 5;
        this.acceptPercent = 100;
        this.setTime = true;
    }
}
