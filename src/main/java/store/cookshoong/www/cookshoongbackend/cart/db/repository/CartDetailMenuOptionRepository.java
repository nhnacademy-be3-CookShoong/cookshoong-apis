package store.cookshoong.www.cookshoongbackend.cart.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.cookshoong.www.cookshoongbackend.cart.db.entity.CartDetailMenuOption;

/**
 * {설명을 작성해주세요}.
 *
 * @author jeongjewan
 * @since 2023.07.27
 */
public interface CartDetailMenuOptionRepository extends JpaRepository<CartDetailMenuOption, CartDetailMenuOption.Pk> {
}
