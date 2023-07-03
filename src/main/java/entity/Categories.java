package entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "categories")
public class Categories {

	@Id
	@Column(name = "category_code", nullable = false)
	private String categoryCode;

	@Column(name = "description", nullable = false)
	private String description;

}
