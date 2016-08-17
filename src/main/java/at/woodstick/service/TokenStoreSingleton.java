package at.woodstick.service;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton
@Startup
public class TokenStoreSingleton {

	private Map<String, String> jwtTokens = new HashMap<String, String>();
	
	public String getToken(String key) {
		return jwtTokens.get(key);
	}
	
	public boolean hasKey(String key) {
		return jwtTokens.containsKey(key);
	}
	
	public void addToken(String token) {
		jwtTokens.put(token, token);
	}
	
	public void addToken(String key, String token) {
		jwtTokens.put(key, token);
	}
	
	public void removeToken(String key) {
		jwtTokens.remove(key);
	}
	
}
