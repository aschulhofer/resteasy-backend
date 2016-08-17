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

@WebFilter
public class CORSOptionsFilter implements Filter {

	@Inject
	private RestResponseProcessor restResponseProcessor;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest httpRequest = (HttpServletRequest)request;
		HttpServletResponse httpResponse = (HttpServletResponse)response;
		
		if("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
			restResponseProcessor.addCorsHeaders(httpResponse);
		}
		
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		
	}
}
