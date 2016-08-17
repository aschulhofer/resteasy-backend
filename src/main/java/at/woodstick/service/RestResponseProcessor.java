package at.woodstick.service;

import javax.ejb.Stateless;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.ResponseBuilder;

@Stateless
public class RestResponseProcessor {

	/**
	 * Adds CORS control headers to the given jax-rs {@link ResponseBuilder response}.
	 * <ul>
	 * 	<li>Access-Control-Allow-Origin</li>
	 *  <li>Access-Control-Allow-Headers</li>
	 *  <li>Access-Control-Allow-Credentials</li>
	 *  <li>Access-Control-Allow-Methods</li>
	 *  <li>Access-Control-Max-Age</li>
	 * </ul>
	 * @param response
	 * @return response to allow method chaining
	 */
	public ResponseBuilder addCorsHeaders(ResponseBuilder response) {
		return response
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600");
	}
	
	/**
	 * Adds CORS control headers to the given servlet {@link HttpServletResponse response}.
	 * <ul>
	 * 	<li>Access-Control-Allow-Origin</li>
	 *  <li>Access-Control-Allow-Headers</li>
	 *  <li>Access-Control-Allow-Credentials</li>
	 *  <li>Access-Control-Allow-Methods</li>
	 *  <li>Access-Control-Max-Age</li>
	 * </ul>
	 * @param httpResponse
	 * @return httpResponse to allow method chaining
	 */
	public HttpServletResponse addCorsHeaders(HttpServletResponse httpResponse) {
		httpResponse.setHeader("Access-Control-Allow-Origin", "*");
		httpResponse.setHeader("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
		httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
		httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
		httpResponse.setHeader("Access-Control-Max-Age", "1209600");
		
		return httpResponse;
	}
	
}
