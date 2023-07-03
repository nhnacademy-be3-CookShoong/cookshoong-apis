package repository;

import store.cookshoong.www.cookshoongbackend.accounts.entity.Authorities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AuthoritiesRepository extends JpaRepository<Authorities, String>, JpaSpecificationExecutor<Authorities> {

}