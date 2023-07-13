package store.cookshoong.www.cookshoongbackend.shop.repository.store;

import org.springframework.data.jpa.repository.JpaRepository;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;

/**
 * 매장 레포지토리.
 *
 * @author seungyeon
 * @since 2023.07.04
 */
public interface StoreRepository extends JpaRepository<Store, Long>, StoreRepositoryCustom {
    /**
     * 사업자 등록번호 이미 존재하는지 확인.
     *
     * @param businessLicenseNumber 사업자등록번호
     * @return true : 이미 등록이 되어있음. false : 등록되지 않은 사업자 번호
     */
    boolean existsStoreByBusinessLicenseNumber(String businessLicenseNumber);
}
