package store.cookshoong.www.cookshoongbackend.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.cookshoong.www.cookshoongbackend.account.entity.Rank;

/**
 * 회원 등급 CRUD 를 위한 레포지토리.
 *
 * @author koesnam
 * @since 2023.07.04
 */
public interface RankRepository extends JpaRepository<Rank, String> {
}
