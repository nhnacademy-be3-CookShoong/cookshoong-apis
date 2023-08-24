package store.cookshoong.www.cookshoongbackend.point.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.order.entity.Order;
import store.cookshoong.www.cookshoongbackend.point.entity.PointLog;
import store.cookshoong.www.cookshoongbackend.point.model.response.PointLogResponseDto;
import store.cookshoong.www.cookshoongbackend.point.model.response.PointResponseDto;

/**
 * 포인트 로그에서 QueryDSL 사용을 위한 interface.
 *
 * @author eora21 (김주호)
 * @since 2023.08.08
 */
@NoRepositoryBean
public interface PointLogRepositoryCustom {
    PointResponseDto lookupMyPoint(Account account);

    Page<PointLogResponseDto> lookupMyPointLog(Account account, Pageable pageable);

    PointLog lookupUsePoint(Order order);
}
