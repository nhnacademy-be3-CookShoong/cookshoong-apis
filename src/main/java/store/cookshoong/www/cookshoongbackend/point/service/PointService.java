package store.cookshoong.www.cookshoongbackend.point.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.account.exception.UserNotFoundException;
import store.cookshoong.www.cookshoongbackend.account.repository.AccountRepository;
import store.cookshoong.www.cookshoongbackend.point.exception.LowPointException;
import store.cookshoong.www.cookshoongbackend.point.model.response.PointLogResponseDto;
import store.cookshoong.www.cookshoongbackend.point.model.response.PointResponseDto;
import store.cookshoong.www.cookshoongbackend.point.repository.PointLogRepository;

/**
 * 포인트 서비스.
 *
 * @author eora21 (김주호)
 * @since 2023.08.08
 */
@Service
@Transactional
@RequiredArgsConstructor
public class PointService {
    private final AccountRepository accountRepository;
    private final PointLogRepository pointLogRepository;

    /**
     * 포인트 합계.
     *
     * @param accountId the account id
     * @return the int
     */
    @Transactional(readOnly = true)
    public PointResponseDto selectSumPoint(Long accountId) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(UserNotFoundException::new);

        return pointLogRepository.lookupMyPoint(account);
    }

    /**
     * 포인트 로그 페이지.
     *
     * @param accountId the account id
     * @param pageable  the pageable
     * @return the page
     */
    @Transactional(readOnly = true)
    public Page<PointLogResponseDto> selectAllPointLog(Long accountId, Pageable pageable) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(UserNotFoundException::new);

        return pointLogRepository.lookupMyPointLog(account, pageable);
    }

    /**
     * 포인트 검증.
     *
     * @param accountId the account id
     * @param usePoint  the use point
     */
    @Transactional(readOnly = true)
    public int getValidPoint(Long accountId, int usePoint, int beforePointDiscountPrice) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(UserNotFoundException::new);

        int nowPoint = pointLogRepository.lookupMyPoint(account)
            .getPoint();

        if (nowPoint < usePoint) {
            throw new LowPointException();
        }

        return Math.min(usePoint, beforePointDiscountPrice);
    }
}
