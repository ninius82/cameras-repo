package it.serravalle.cameras.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class MediaClient {

	private final MediaServer mediaServer;

	public MediaClient(MediaServer mediaServer) {
		this.mediaServer = mediaServer;
	}

	@Bean(name = "mediaWebClient")
	public WebClient mediaWebClient() {
		return WebClient.builder()
				.baseUrl(mediaServer.getHostname())
				.defaultHeaders(header -> header.setBasicAuth(mediaServer.getUser(), mediaServer.getPassword()))
				.build();
	}

}
