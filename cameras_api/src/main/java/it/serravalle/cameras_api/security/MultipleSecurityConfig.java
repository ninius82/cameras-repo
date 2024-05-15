package it.serravalle.cameras_api.security;

//import org.springframework.security.config.Customizer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;


//import com.azure.spring.cloud.autoconfigure.implementation.aad.security.AadResourceServerHttpSecurityConfigurer;

@Configuration
@EnableWebSecurity
public class MultipleSecurityConfig {

	// My enpdoints start from / so this pattern is ok for me
	// private static final String API_URL_PATTERN = "/**";

	@Autowired
	private MyBasicAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

	@Bean
	MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
		return new MvcRequestMatcher.Builder(introspector);
	}

	@Bean
	@Order(1)
	SecurityFilterChain basicAuthFilterChain(HttpSecurity http) throws Exception {

		http.securityMatcher("/cameras/**")
				// .securityMatcher("/v1/info/**")
				.csrf(csrf -> csrf.disable())
				.headers(headersConfigurer -> headersConfigurer
						.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(authorize -> authorize.requestMatchers("/cameras/private/**")
						.hasRole("Camera.Admin").requestMatchers("/cameras/**").hasRole("Camera.User")
						// .requestMatchers(mvc.pattern(API_URL_PATTERN)).hasAnyRole()
						.anyRequest().authenticated())
				.httpBasic((httpSecurityHttpBasicConfigurer) -> httpSecurityHttpBasicConfigurer
						.authenticationEntryPoint(authenticationEntryPoint));
		return http.build();
	}

    @Bean
    @Order(2)
    SecurityFilterChain oauth2FilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable())
				.headers(headersConfigurer -> headersConfigurer
						.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				// .with(AadWebApplicationHttpSecurityConfigurer.aadWebApplication(),
				// Customizer.withDefaults())
				.authorizeHttpRequests(authorize -> authorize.requestMatchers("/oauth2/cameras/private/**")
						.hasAuthority("SCOPE_Camera.Admin").requestMatchers("/oauth2/cameras/**")
						.hasAuthority("SCOPE_Camera.User").requestMatchers("/**").permitAll()
						// .requestMatchers(mvc.pattern(API_URL_PATTERN)).hasAnyRole()
						.anyRequest().authenticated())
				.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt
	                    .jwtAuthenticationConverter(jwtAuthenticationConverter())));
		// .oauth2ResourceServer(oauth2 -> oauth2
		// .jwt(jwt -> jwt
		// .jwkSetUri("https://sts.windows.net/<Tenant-ID>/.well-known/openid-configuration")));
		return http.build();

	}

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }
}