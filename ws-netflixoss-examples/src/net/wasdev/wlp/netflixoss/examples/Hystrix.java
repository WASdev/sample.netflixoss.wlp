/**
 * (C) Copyright IBM Corporation 2014.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.wasdev.wlp.netflixoss.examples;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.websphere.security.WSSecurityException;
import com.ibm.websphere.security.auth.WSSubject;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import com.netflix.servo.DefaultMonitorRegistry;
import com.netflix.servo.monitor.BasicCounter;
import com.netflix.servo.monitor.Counter;
import com.netflix.servo.monitor.MonitorConfig;

/**
 * Servlet implementation class Hystrix
 */
@WebServlet("/hystrix")
public class Hystrix extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final Counter getRequestCounter = new BasicCounter(
			MonitorConfig.builder("requestCounter").build());

	static {
		DefaultMonitorRegistry.getInstance().register(getRequestCounter);
	}

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Hystrix() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		getRequestCounter.increment();

		HystrixRequestContext context = HystrixRequestContext
				.initializeContext();
		
		checkThreadContextPropagation(context, request, response);
		checkCommandTimeout(context, request, response);
		checkCommandError(context, request, response);
		context.shutdown();
	}
	
	private void checkCommandError(HystrixRequestContext context,
			HttpServletRequest request, HttpServletResponse response) throws IOException{

		HystrixCommand<String> hystrixCommand = new HystrixCommand<String>(
				HystrixCommandGroupKey.Factory.asKey("Error")) {

			@Override
			protected String run() throws Exception {
				if(true)
					throw new RuntimeException();
				return "Shouldn't get here";
			}

			@Override
			protected String getFallback() {
				return "Error Fallback";
			}

		};
	
		ServletOutputStream sos = response.getOutputStream();			
		sos.println("TimeoutTest:" + hystrixCommand.execute());
		
	}
	
	private void checkCommandTimeout(HystrixRequestContext context,
			HttpServletRequest request, HttpServletResponse response) throws IOException{

		HystrixCommand<String> hystrixCommand = new HystrixCommand<String>(
				HystrixCommandGroupKey.Factory.asKey("Timeout")) {

			@Override
			protected String run() throws Exception {
				Thread.sleep(60000);
				return "Shouldn't get here";
			}

			@Override
			protected String getFallback() {
				return "Timeout Fallback";
			}

		};
	
		ServletOutputStream sos = response.getOutputStream();			
		sos.println("TimeoutTest:" + hystrixCommand.execute());
		
	}

	private void checkThreadContextPropagation(HystrixRequestContext context,
			HttpServletRequest request, HttpServletResponse response) throws IOException{

		HystrixCommand<String> hystrixCommand = new HystrixCommand<String>(
				HystrixCommandGroupKey.Factory.asKey("GetPrincipal")) {

			@Override
			protected String run() throws Exception {
				return WSSubject.getRunAsSubject().getPrincipals().toString();
			}

			@Override
			protected String getFallback() {
				return "Error retrieving principal";
			}

		};

		try {
			ServletOutputStream sos = response.getOutputStream();
			sos.println("Servlet:" + request.getUserPrincipal().toString());
			sos.println("ServletThread:"
					+ WSSubject.getRunAsSubject().getPrincipals().toString());
			sos.println("HystrixThread:" + hystrixCommand.execute());
		} catch (WSSecurityException e) {
			e.printStackTrace();
		}
	}

}
