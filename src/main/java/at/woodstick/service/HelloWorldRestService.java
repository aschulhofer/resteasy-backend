package at.woodstick.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import at.woodstick.data.RestMessage;

@Path("message")
public class HelloWorldRestService {

	@GET
	@Path("helloworld")
	@Produces("application/json")
	public Response getHelloWorld() {
		return Response
				.ok(new RestMessage("Hello World - cross origin"))
				.header("Access-Control-Allow-Origin", "*")
	            .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
	            .header("Access-Control-Allow-Credentials", "true")
	            .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
	            .header("Access-Control-Max-Age", "1209600")
				.build();
	}
	
}
