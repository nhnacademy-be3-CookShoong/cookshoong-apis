package store.cookshoong.www.cookshoongbackend.accounts.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "authorities")
public class Authorities {

	@Id
	@Column(name = "authority_code", nullable = false)
	private String authorityCode;

	@Column(name = "description", nullable = false)
	private String description;

}
