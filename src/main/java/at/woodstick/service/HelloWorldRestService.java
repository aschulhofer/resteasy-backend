package at.woodstick.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import at.woodstick.data.RestMessage;
import at.woodstick.model.HistoryLog;

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
		HistoryLog entry = HistoryLog.newEntry("API call", Date.from(createdAt.atZone(ZoneId.systemDefault()).toInstant()));
		em.persist(entry);
		
		return Response
				.ok(new RestMessage("Hello World - cross origin")).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").build();
	}

}
