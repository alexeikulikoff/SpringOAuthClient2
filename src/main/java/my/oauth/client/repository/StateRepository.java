package my.oauth.client.repository;

import org.springframework.data.repository.CrudRepository;

import my.oauth.client.entity.StateEntity;

public interface StateRepository extends CrudRepository<StateEntity, Long> {

	StateEntity findByState(String state);

}
