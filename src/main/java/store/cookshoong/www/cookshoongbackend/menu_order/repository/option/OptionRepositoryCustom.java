package store.cookshoong.www.cookshoongbackend.menu.repository.option;

import java.util.List;
import org.springframework.data.repository.NoRepositoryBean;
import store.cookshoong.www.cookshoongbackend.menu.model.response.SelectOptionResponseDto;

/**
 * 옵션 Custom 레포지토리 인터페이스.
 *
 * @author papel
 * @since 2023.07.17
 */
@NoRepositoryBean
public interface OptionRepositoryCustom {

    /**
     * 일반 회원의 메뉴 옵션 조회 구현.
     *
     * @param storeId 매장 아이디
     * @return 매장의 메뉴 리스트
     */
    List<SelectOptionResponseDto> lookupOptions(Long storeId);
}
