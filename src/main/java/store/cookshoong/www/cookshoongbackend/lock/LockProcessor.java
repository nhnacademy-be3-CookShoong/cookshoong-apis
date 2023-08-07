package store.cookshoong.www.cookshoongbackend.lock;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import store.cookshoong.www.cookshoongbackend.rabbitmq.exception.LockInterruptedException;
import store.cookshoong.www.cookshoongbackend.rabbitmq.exception.LockOverWaitTimeException;

/**
 * 분산락을 담당하는 bean.
 *
 * @author eora21 (김주호)
 * @since 2023.08.07
 */
@Component
@RequiredArgsConstructor
public class LockProcessor {
    private static final int WAIT_TIME = 60;
    private static final int LEASE_TIME = 5;

    private final RedissonClient redissonClient;

    /**
     * 분산락 활성화.
     *
     * @param key      the key
     * @param consumer the consumer
     */
    public void lock(String key, Consumer<String> consumer) {
        RLock lock = redissonClient.getLock(key);

        try {
            boolean lockSuccess = lock.tryLock(WAIT_TIME, LEASE_TIME, TimeUnit.SECONDS);
            if (!lockSuccess) {
                throw new LockOverWaitTimeException();
            }

            consumer.accept(key);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new LockInterruptedException();
        } finally {
            lock.unlock();
        }
    }
}
