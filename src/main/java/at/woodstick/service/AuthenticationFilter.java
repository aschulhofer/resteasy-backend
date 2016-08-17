package at.woodstick.service;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

@WebFilter
public class AuthenticationFilter implements Filter {

	@Inject
	private TokenStoreSingleton tokenStore;
	
	@Inject
	private RestResponseProcessor restResponseProcessor;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest httpRequest = (HttpServletRequest)request;
		HttpServletResponse httpResponse = (HttpServletResponse)response;
		
		String requestUrl = httpRequest.getRequestURL().toString();
		
		System.out.println("url: " + requestUrl);
		
		boolean isOptionsRequest = "OPTIONS".equalsIgnoreCase(httpRequest.getMethod());
		boolean isLogin = requestUrl.endsWith("/login");
		
		String authorizationValue = httpRequest.getHeader("Authorization");
		
		System.out.println("Authorization header: " + authorizationValue);
		
		if(!isOptionsRequest && !isLogin) {
			String jwtToken = null;
			if(authorizationValue != null) {
				jwtToken = authorizationValue.replaceAll("Bearer ", "");
			}
			
			if(jwtToken == null || !tokenStore.hasKey(jwtToken)) {
				System.out.println("UNAUTHORIZED");
				
				restResponseProcessor.addCorsHeaders(httpResponse)
									 .setStatus(Status.UNAUTHORIZED.getStatusCode());
				
				return;
			}
		}
		
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		
	}
}
