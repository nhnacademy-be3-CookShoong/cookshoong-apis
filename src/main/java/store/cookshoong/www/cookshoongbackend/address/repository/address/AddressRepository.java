package store.cookshoong.www.cookshoongbackend.address.repository.address;

import org.springframework.data.jpa.repository.JpaRepository;
import store.cookshoong.www.cookshoongbackend.address.entity.Address;

/**
 * 회원과 매장에서 요청되는 정보에 대한 JPA.
 *
 * @author jeongjewan
 * @since 2023.07.04
 */
public interface AddressRepository extends JpaRepository<Address, Long> {
}
