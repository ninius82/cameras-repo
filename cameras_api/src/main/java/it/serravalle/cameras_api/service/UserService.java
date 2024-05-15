package it.serravalle.cameras_api.service;

import it.serravalle.cameras_api.data.model.User;

import java.util.List;


public interface UserService {

    User save(User user);
    User addRoleToUser(String username, String roleName);
    User findByUsername(String username);
    List<User> findAll();
    //Map<String,String> refreshToken(String authorizationHeader, String issuer) throws BadJOSEException, ParseException, JOSEException;
}
