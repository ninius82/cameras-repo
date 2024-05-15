package it.serravalle.cameras_api.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.serravalle.cameras_api.data.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

	Role findByName(String name);
}
