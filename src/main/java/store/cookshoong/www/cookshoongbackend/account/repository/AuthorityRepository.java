package store.cookshoong.www.cookshoongbackend.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.cookshoong.www.cookshoongbackend.account.entity.Authority;

/**
 * 회원 권한 CRUD 를 위한 레포지토리.
 *
 * @author koesnam
 * @since 2023.07.04
 */
public interface AuthorityRepository extends JpaRepository<Authority, String> {
}
