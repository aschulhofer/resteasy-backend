package at.woodstick.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;

import at.woodstick.data.request.LoginData;
import at.woodstick.model.HistoryLog;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;

@Stateless
@Path("auth")
public class AuthenticationEndpoint {

	private static Map<String, User> userMap  = new HashMap<>();
	
	static {
		userMap.put("admin", new User("admin", "admin", Arrays.asList("admin")));
		userMap.put("user", new User("user", "user", Arrays.asList("user")));
	}
	
	@PersistenceContext
	private EntityManager em;
	
	@Inject
	private TokenStoreSingleton tokenStore;
	
	@Inject
	private RestResponseProcessor restResponseProcessor;
	
	private String getSecretKey() {
		return "secretkey";
	}
	
	@POST
	@Path("test")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Interceptors(LoggingInterceptor.class)
	public Response test(@HeaderParam("Authorization") String bearerToken) {
		
		System.out.println("Authorization = '" + bearerToken + "'");
		
		Response response = null;
		
		if(bearerToken != null) {
			Map<String, Object> respData = new HashMap<>();
			respData.put("success", Boolean.TRUE);
			
			String jwtToken = bearerToken.replaceAll("Bearer ", "");
			
			System.out.println("jwtToken = '" + jwtToken + "'");
			
			String key = TextCodec.BASE64.encode(getSecretKey());
			
			System.out.println("Decoded key: " + TextCodec.BASE64.decodeToString(key));
			
			try {
				
				Jws<Claims> jwt = Jwts.parser().setSigningKey(key).parseClaimsJws(jwtToken);
				
				List<String> roles = (List<String>)jwt.getBody().get("roles");
				
				System.out.println("Roles: " + roles);
				
				respData.put("roles", roles);
				response = restResponseProcessor.addCorsHeaders(Response.ok(respData)).build();
			}
			catch(Exception e) {
				System.out.println("error parsing jwt token: " + e.getMessage());
				e.printStackTrace();
				response = restResponseProcessor.addCorsHeaders(Response.status(Status.UNAUTHORIZED)).build();
			}
		}
		else {
			response = restResponseProcessor.addCorsHeaders(Response.status(Status.UNAUTHORIZED)).build();
		}
		
		return response;
	}
	
	@POST
	@Path("login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response authenticate(@HeaderParam("Authorization") String bearerToken, LoginData loginData) {
		
		LocalDateTime createdAt = LocalDateTime.now();
		Date createAtDate = Date.from(createdAt.atZone(ZoneId.systemDefault()).toInstant());
		HistoryLog entry = HistoryLog.newEntry("API call auth", createAtDate);
		em.persist(entry);
		
		String username = loginData.getUsername();
		String password = loginData.getPassword();
		
		System.out.println("Authenticate with " + username + " and " + password);
		System.out.println("Authorization = '" + bearerToken + "'");
		
		User user = userMap.get(username);
		
		System.out.println("Found user: " + user);
		
		if(user == null || !user.isUser(username, password)) {
			return restResponseProcessor.addCorsHeaders(Response.status(Status.UNAUTHORIZED)).build();
		}
		
//		Key key = MacProvider.generateKey();
		
		List<String> roles = user.getRoles();
		
		Claims claims = Jwts.claims();
		claims.put("roles", roles);
		
		claims
			.setSubject(username)
			.setIssuer("Rest API")
			.setIssuedAt(createAtDate);
		
		String key = TextCodec.BASE64.encode(getSecretKey());
		
		String compactJws = Jwts.builder()
				  .setHeaderParam(Header.TYPE, "JWT")
				  .setClaims(claims)
				  .signWith(SignatureAlgorithm.HS256, key)
				  .compact();
		
		Map<String, Object> responseData = new HashMap<>();
		responseData.put("success", Boolean.TRUE);
		responseData.put("jwt", compactJws);
		responseData.put("key", key);
		responseData.put("username", username);
		responseData.put("roles", roles);

		String authorizationValue = "Bearer " + compactJws;
		
		tokenStore.addToken(compactJws);
		
		return restResponseProcessor.addCorsHeaders(
				Response.ok(responseData).header("Authorization", authorizationValue)
			   ).build();
	}
	
	@POST
	@Path("logout")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response logout(@HeaderParam("Authorization") String bearerToken, LoginData loginData) {
		
		LocalDateTime createdAt = LocalDateTime.now();
		Date createAtDate = Date.from(createdAt.atZone(ZoneId.systemDefault()).toInstant());
		HistoryLog entry = HistoryLog.newEntry("API call auth", createAtDate);
		em.persist(entry);
		
		if(bearerToken != null) {
			String jwtToken = bearerToken.replaceAll("Bearer ", "");
			
			if(tokenStore.hasKey(jwtToken)) {
				tokenStore.removeToken(jwtToken);
				return restResponseProcessor.addCorsHeaders(Response.ok()).build();
			}
			else {
				System.out.println("Token not found");
			}
		}
		else {
			System.out.println("No authorization header set");
		}
		
		return restResponseProcessor.addCorsHeaders(Response.status(Status.BAD_REQUEST)).build();
	}

	
	static class User {
		private final String username;
		private final String password;
		private final List<String> roles;
		
		public User(String username, String password, List<String> roles) {
			this.username = username;
			this.password = password;
			this.roles = roles;
		}

		public String getUsername() {
			return username;
		}

		public String getPassword() {
			return password;
		}

		public List<String> getRoles() {
			return roles;
		}

		@Override
		public String toString() {
			return "User [username=" + username + ", password=" + password + ", roles=" + roles + "]";
		}

		public boolean isUser(String username, String password) {
			return StringUtils.isNoneEmpty(username, password) && username.equals(this.username) && password.equals(this.password);
		}
	}
}
