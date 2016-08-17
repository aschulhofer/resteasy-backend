package at.woodstick.service;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@Interceptor
public class LoggingInterceptor {

	@AroundInvoke
	public Object intercept(InvocationContext context) throws Exception {
		
		Object[] parameters = context.getParameters();
		
		for(Object parameter : parameters) {
			System.out.println("PARAM: " + parameter);
		}
		
		return context.proceed();
	}
	
}
