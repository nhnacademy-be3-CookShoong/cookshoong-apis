package store.cookshoong.www.cookshoongbackend.shop.repository.stauts;

import java.util.List;
import org.springframework.data.repository.NoRepositoryBean;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectAllStatusResponseDto;

/**
 * 매장 상태 custom repository interface.
 *
 * @author seungyeon
 * @since 2023.07.16
 */
@NoRepositoryBean
public interface StoreStatusRepositoryCustom {

    List<SelectAllStatusResponseDto> lookupStatusForUser();
}
