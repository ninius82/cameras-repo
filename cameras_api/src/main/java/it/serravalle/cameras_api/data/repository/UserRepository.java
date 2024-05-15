package it.serravalle.cameras_api.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.serravalle.cameras_api.data.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

	User findByUsername(String username);
}
