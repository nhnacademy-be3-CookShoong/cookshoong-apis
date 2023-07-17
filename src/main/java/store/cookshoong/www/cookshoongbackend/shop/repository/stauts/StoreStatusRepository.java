package store.cookshoong.www.cookshoongbackend.shop.repository.stauts;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import store.cookshoong.www.cookshoongbackend.shop.entity.StoreStatus;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectAllStatusResponseDto;

/**
 * 매장 상태 레포지토리.
 *
 * @author seungyeon
 * @since 2023.07.04
 */
public interface StoreStatusRepository extends JpaRepository<StoreStatus, String>, StoreStatusRepositoryCustom{
}
