package it.serravalle.cameras.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class MediaClient {
	
	
	@Autowired
	private MediaServer mediaServer;
	
	private WebClient client;


	public WebClient getClient() {
		return client;
	}


	@Autowired
	public void setClient() {
		this.client = WebClient.builder()
				.baseUrl(mediaServer.getHostname())
				.defaultHeaders(header -> header.setBasicAuth(mediaServer.getUser(), mediaServer.getPassword()))
				.build();
	}

}