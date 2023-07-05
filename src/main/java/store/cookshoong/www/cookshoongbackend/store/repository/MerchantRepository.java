package store.cookshoong.www.cookshoongbackend.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.cookshoong.www.cookshoongbackend.store.entity.Merchant;

/**
 * 가맹점 레포지토리.
 *
 * @author seungyeon
 * @since 2023.07.04
 */
public interface MerchantRepository extends JpaRepository<Merchant, Long> {
}
