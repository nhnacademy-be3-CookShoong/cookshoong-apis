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
     * 이름으로 가맹점 객체 찾기.
     *
     * @param name 가맹점 이름
     * @return 있으면 반환하고, 없으면 null 반환하도록 유도할 것
     */
    Optional<Merchant> findMerchantByName(String name);

    /**
     * 가맹잠 존재 여부.
     *
     * @param name 가맹점 이름
     * @return true : 이미 등록되어있음. false : 등록이 안되어있음.
     */
    boolean existsMerchantByName(String name);
}
