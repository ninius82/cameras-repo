package it.serravalle.cameras.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "app.mediaserver")
@Data
public class MediaServer {

    /**
     * Hostname of MediaServer.
     */
    private String hostname;
    private String user;
    private String password;
    

//	public String getUser() {
//		return user;
//	}
//
//	public void setUser(String user) {
//		this.user = user;
//	}
//
//	public String getPassword() {
//		return password;
//	}
//
//	public void setPassword(String password) {
//		this.password = password;
//	}
//
//	public String getHostname() {
//		return hostname;
//	}
//
//	public void setHostname(String hostname) {
//		this.hostname = hostname;
//	}

    
}
