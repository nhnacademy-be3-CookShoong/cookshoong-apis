package store.cookshoong.www.cookshoongbackend.shop.repository.store;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectAllStoresNotOutedResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectAllStoresResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectStoreForUserResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectStoreResponseDto;

/**
 * 매장 Custom 레포지토리 인터페이스.
 *
 * @author seungyeon
 * @since 2023.07.05
 */
@NoRepositoryBean
public interface StoreRepositoryCustom {

    /**
     * 사업자 회원의 매장 조회시 pagination 구현.
     *
     * @param accountId 회원 아이디
     * @param pageable  페이지 정보.
     * @return 페이지 별 매장 리스트
     */
    Page<SelectAllStoresResponseDto> lookupStoresPage(Long accountId, Pageable pageable);

    /**
     * 사업자 회원의 storeId에 해당하는 매장 조회 구현.
     *
     * @param accountId 회원 아이디
     * @param storeId   매장 아이디
     * @return 매장에 대한 정보
     */
    Optional<SelectStoreResponseDto> lookupStore(Long accountId, Long storeId);

    /**
     * 일반 회원이 선택한 매장의 정보 조회.
     *
     * @param storeId 해당 매장 id
     * @return 해당 매장 정보
     */
    Optional<SelectStoreForUserResponseDto> lookupStoreForUser(Long storeId);

    /**
     * 회원 위치에서 3km 이내에 있는 매장들을 조회하기 전, 모든 매장을 가져오는 메서드.
     *
     * @return  모든 매장 정보 페이지로 반환.
     */
    Page<SelectAllStoresNotOutedResponseDto> lookupStoreLatLanPage(Pageable pageable);
}
