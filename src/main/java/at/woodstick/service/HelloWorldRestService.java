package at.woodstick.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import at.woodstick.data.RestMessage;
import at.woodstick.model.HistoryLog;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Stateless
@Path("message")
public class HelloWorldRestService {

	@PersistenceContext
	private EntityManager em;
	
	@GET
	@Path("helloworld")
	@Produces("application/json")
	public Response getHelloWorld() {
		
		LocalDateTime createdAt = LocalDateTime.now();
		HistoryLog entry = HistoryLog.newEntry("API hello world call", Date.from(createdAt.atZone(ZoneId.systemDefault()).toInstant()));
		em.persist(entry);
		
		return Response
				.ok(new RestMessage("Hello World - cross origin")).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").build();
	}
	
	@GET
	@Path("testdata")
	@Produces("application/json")
	public Response getTestData() {
		
		LocalDateTime createdAt = LocalDateTime.now();
		HistoryLog entry = HistoryLog.newEntry("API testdata call", Date.from(createdAt.atZone(ZoneId.systemDefault()).toInstant()));
		em.persist(entry);
		
		List<Object> testList = new ArrayList<>();
		testList.add("abc");
		testList.add(new RestMessage("Hello world!"));
		
		Map<String, Object> testValues = new HashMap<>();
		testValues.put("boolean", Boolean.TRUE);
		testValues.put("str", "Value String");
		testValues.put("number", 100);
		testValues.put("number2", 2.5f);
		testValues.put("list", testList);
		testValues.put("obj", new RestMessage("Hello world II!"));
		
		return Response
				.ok(testValues).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").build();
	}
	
	
	@POST
	@Path("test/jwttoken")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response authenticate(Map<String, Object> requestBody) {
		
		LocalDateTime createdAt = LocalDateTime.now();
		Date createAtDate = Date.from(createdAt.atZone(ZoneId.systemDefault()).toInstant());
		HistoryLog entry = HistoryLog.newEntry("API call auth", createAtDate);
		em.persist(entry);
		
		String username = (String)requestBody.get("username");
		
		// Key key = MacProvider.generateKey();
		List<String> roles = Arrays.asList("admin", "user");
		
		Claims claims = Jwts.claims();
		claims.put("roles", roles);
		
		claims
			.setSubject(username)
			.setIssuer("Rest API")
			.setIssuedAt(createAtDate);
		
		String key = "secretkey";
		String compactJws = Jwts.builder()
				  .setHeaderParam("typ", "JWT")
				  .setClaims(claims)
				  .signWith(SignatureAlgorithm.HS256, key)
				  .compact();
		
		Map<String, Object> responseData = new HashMap<>();
		responseData.put("success", Boolean.TRUE);
		responseData.put("jwt", compactJws);
		responseData.put("key", key);
		responseData.put("username", username);
		responseData.put("req_body", requestBody);

		return Response
				.ok(responseData).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").build();
	}

}
