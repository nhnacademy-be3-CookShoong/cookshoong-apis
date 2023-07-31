package store.cookshoong.www.cookshoongbackend.cart.db.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import store.cookshoong.www.cookshoongbackend.cart.db.entity.Cart;

/**
 * Redis 장바구니에 담겨져 있는 정보를 Db 저장 조회해 주는 Repository.
 *
 * @author jeongjewan
 * @since 2023.07.27
 */
@Repository
public interface CartRepository extends JpaRepository<Cart, UUID>, CartRepositoryCustom {
}
