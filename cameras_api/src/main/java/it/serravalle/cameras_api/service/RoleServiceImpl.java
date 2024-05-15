package it.serravalle.cameras_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import it.serravalle.cameras_api.data.model.Role;
import it.serravalle.cameras_api.data.repository.RoleRepository;



@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class RoleServiceImpl implements RoleService {

    @Autowired
	private RoleRepository roleRepository;

    @Override
    public Role save(Role role) {
        log.info("Saving role {} to the database", role.getName());
        return roleRepository.save(role);
    }


}