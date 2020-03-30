package my.oauth.client.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import my.oauth.client.entity.UserEntity;

@Transactional
public interface UserRepository extends CrudRepository<UserEntity, Long> {

	UserEntity findByName(String name);

}
