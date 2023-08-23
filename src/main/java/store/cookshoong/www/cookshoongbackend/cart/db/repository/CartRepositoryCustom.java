package store.cookshoong.www.cookshoongbackend.cart.db.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.NoRepositoryBean;
import store.cookshoong.www.cookshoongbackend.cart.db.model.response.CartResponseDto;

/**
 * DB 에 저장되어 있는 장바구니 내역을 조회하는 메서드.
 *
 * @author jeongjewan
 * @since 2023.07.28
 */
@NoRepositoryBean
public interface CartRepositoryCustom {

    /**
     * 회원이 장바구니를 가지고 있는 확인하는 메서드.
     *
     * @param accountId     회원 아이디
     * @return              회원에 대해 장바구니가 존재하면 TRUE, 없으면 False
     */
    Boolean hasCartByAccountId(Long accountId);

    /**
     * 회원 아이디를 통해 DB 장바구니 가져오기.
     *
     * @param accountId     회원 아이디
     * @return              장바구니 UUID 반환
     */
    UUID findCartId(Long accountId);

    /**
     * DB 에 들어 있는 회원 장바구니 정보가져오기.
     *
     * @param accountId     회원 아이디
     * @return              회원 장바구니 정보
     */
    List<CartResponseDto> lookupCartDbList(Long accountId);
}
