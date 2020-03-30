package my.oauth.client.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class UserEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;
	private String password;
	private String role;

	public UserEntity() {
	}

	public UserEntity(Long i, String n, String passwd, String r) {
		id = i;
		name = n;
		password = passwd;
		role = r;
	}

	public UserEntity(UserEntity us) {
		id = us.id;
		name = us.name;
		password = us.password;
		role = us.role;
	}

	public void setId(Long i) {
		id = i;
	}

	public Long getId() {
		return id;
	}

	public void setName(String s) {
		name = s;
	}

	public String getName() {
		return name;
	}

	public void setPassword(String s) {
		password = s;
	}

	public String getPassword() {
		return password;
	}

	public void setRole(String s) {
		role = s;
	}

	public String getRole() {
		return role;
	}

	@Override
	public String toString() {
		return "Name: [" + name + "] password: [" + password + "]";
	}

}
