package it.serravalle.cameras_api.service;

import it.serravalle.cameras_api.data.model.Role;
import it.serravalle.cameras_api.data.model.User;
import it.serravalle.cameras_api.data.repository.RoleRepository;
import it.serravalle.cameras_api.data.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {
	private static final String USER_NOT_FOUND_MESSAGE = "User with username %s not found";

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Transactional(readOnly = true)
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username);
		if (user == null) {
			String message = String.format(USER_NOT_FOUND_MESSAGE, username);
			log.error(message);
			throw new UsernameNotFoundException(message);
		} else {
			log.info("User found in the database: {}", username);
			List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
					.map(role -> new SimpleGrantedAuthority(role.getName()))
					.collect(Collectors.toList());
			return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
					authorities);
		}
	}

	@Override
	public User save(User user) {
		log.info("Saving user {} to the database", user.getUsername());
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return userRepository.save(user);
	}

	@Override
	public User addRoleToUser(String username, String roleName) {
		log.info("Adding role {} to user {}", roleName, username);
		User user = userRepository.findByUsername(username);
		Role role = roleRepository.findByName(roleName);
		user.getRoles().add(role);
		return user;
	}

	@Transactional(readOnly = true)
	@Override
	public User findByUsername(String username) {
		log.info("Retrieving user {}", username);
		return userRepository.findByUsername(username);
	}

	@Transactional(readOnly = true)
	@Override
	public List<User> findAll() {
		log.info("Retrieving all users");
		return userRepository.findAll();
	}

//	@Transactional(readOnly = true)
//	@Override
//	public Map<String, String> refreshToken(String authorizationHeader, String issuer)
//			throws BadJOSEException, ParseException, JOSEException {
//
//		String refreshToken = authorizationHeader.substring("Bearer ".length());
//		UsernamePasswordAuthenticationToken authenticationToken = JwtUtil.parseToken(refreshToken);
//		String username = authenticationToken.getName();
//		User userEntity = findByUsername(username);
//		List<String> roles = userEntity.getRoles().stream().map(Role::getName).collect(Collectors.toList());
//		String accessToken = JwtUtil.createAccessToken(username, issuer, roles);
//		return Map.of("access_token", accessToken, "refresh_token", refreshToken);
//	}

}
