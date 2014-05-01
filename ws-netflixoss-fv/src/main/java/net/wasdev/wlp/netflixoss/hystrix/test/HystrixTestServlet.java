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
package net.wasdev.wlp.netflixoss.hystrix.test;

import java.io.IOException;
import java.io.PrintWriter;
 
import javax.servlet.ServletException;  
import javax.servlet.annotation.WebServlet; 
import javax.servlet.http.HttpServlet; 
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;
import com.netflix.config.DynamicLongProperty;
import com.netflix.hystrix.HystrixCommand;  
import com.netflix.hystrix.HystrixCommandGroupKey; 
import com.netflix.hystrix.HystrixCommandProperties;

/**
 * Servlet implementation class HystrixTestServlet
 */
public class HystrixTestServlet extends HttpServlet { 

	private static final long serialVersionUID = 1L;

	DynamicLongProperty threadTimeout = null;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public HystrixTestServlet() {
		super();

		threadTimeout = DynamicPropertyFactory.getInstance().getLongProperty("hystrix.command.CommandHelloWorld.execution.isolation.thread.timeoutInMilliseconds", 500); 
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			PrintWriter out  = response.getWriter();
			boolean flag = (testAsynchronous().equals("Hello Phani!"));
			System.out.println("flag="+flag);
			out.println(flag);
		} catch (Exception e) {
			throw new ServletException (e);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}

	private String testAsynchronous() throws Exception {

		String s = new CommandHelloWorld("Phani").queue().get();
		System.out.println("s="+s);
		return s;
	}


}

class CommandHelloWorld extends HystrixCommand<String> {

	private final String name;

	public CommandHelloWorld(String name) {
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionIsolationThreadTimeoutInMilliseconds(2000)));
		
		this.name = name;
	}

	@Override
	protected String run() {
		try {
			Thread.currentThread().sleep(9000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// a real example would do work like a network call here
		return "Hello " + name + "!";
	}

}
