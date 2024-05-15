package it.serravalle.cameras.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class MetadataClient {

	@Bean
	ReactiveClientRegistrationRepository clientRegistrations(
			@Value("${spring.security.oauth2.client.provider.azure.token-uri}") String token_uri,
			@Value("${spring.security.oauth2.client.registration.azure.client-id}") String client_id,
			@Value("${spring.security.oauth2.client.registration.azure.client-secret}") String client_secret,
			@Value("${spring.security.oauth2.client.registration.azure.authorization-grant-type}") String authorizationGrantType,
			@Value("${spring.security.oauth2.client.registration.azure.scope}") String scope

	) {
		ClientRegistration registration = ClientRegistration.withRegistrationId("azure").tokenUri(token_uri)
				.clientId(client_id).clientSecret(client_secret)
				.authorizationGrantType(new AuthorizationGrantType(authorizationGrantType)).scope(scope).build();
		return new InMemoryReactiveClientRegistrationRepository(registration);
	}
	
    @Bean
    AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager authorizedClientManager(
        ReactiveClientRegistrationRepository clientRegistrationRepository) {
        InMemoryReactiveOAuth2AuthorizedClientService clientService =
            new InMemoryReactiveOAuth2AuthorizedClientService(clientRegistrationRepository);
        ReactiveOAuth2AuthorizedClientProvider authorizedClientProvider =
            ReactiveOAuth2AuthorizedClientProviderBuilder.builder().clientCredentials().build();
        AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager authorizedClientManager =
            new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(
                clientRegistrationRepository, clientService);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);
        return authorizedClientManager;
    }

    @Bean
    WebClient webClient(AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager auth2AuthorizedClientManager) {
        ServerOAuth2AuthorizedClientExchangeFilterFunction oauth2Client =
            new ServerOAuth2AuthorizedClientExchangeFilterFunction(auth2AuthorizedClientManager);
        oauth2Client.setDefaultClientRegistrationId("azure");
        return WebClient.builder()
            .filter(oauth2Client)
            .build();
    }
}
