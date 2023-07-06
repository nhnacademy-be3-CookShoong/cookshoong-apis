package store.cookshoong.www.cookshoongbackend.store.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import store.cookshoong.www.cookshoongbackend.store.entity.Merchant;

/**
 * 가맹점 레포지토리.
 *
 * @author seungyeon
 * @since 2023.07.04
 */
public interface MerchantRepository extends JpaRepository<Merchant, Long>, MerchantRepositoryCustom {
    /**
     * Find merchant by name optional.
     *
     * @param name the name
     * @return the optional
     */
    Optional<Merchant> findMerchantByName(String name);
}
