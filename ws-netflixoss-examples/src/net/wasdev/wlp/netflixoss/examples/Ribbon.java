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
import java.net.URI;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.netflix.client.ClientFactory;
import com.netflix.client.http.HttpRequest;
import com.netflix.client.http.HttpResponse;
import com.netflix.config.ConfigurationManager;
import com.netflix.loadbalancer.ZoneAwareLoadBalancer;
import com.netflix.niws.client.http.RestClient;

/**
 * Servlet implementation class Ribbon
 */
@WebServlet("/ribbon")
public class Ribbon extends HttpServlet {

	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Ribbon() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest servletRequest,
			HttpServletResponse servletResponse) throws ServletException,
			IOException {
		try {
						
			System.out.println(ConfigurationManager.getConfigInstance()
					.getProperty("sample-client.ribbon.listOfServers"));
			RestClient client = (RestClient) ClientFactory
					.getNamedClient("sample-client");
			HttpRequest request = HttpRequest.newBuilder().uri(new URI("/"))
					.build();
			for (int i = 0; i < 1; i++) {
				HttpResponse response = client.executeWithLoadBalancer(request);
				System.out.println("Status code for "
						+ response.getRequestedURI() + "  :"
						+ response.getStatus());
			}
			@SuppressWarnings("rawtypes")
			ZoneAwareLoadBalancer lb = (ZoneAwareLoadBalancer) client
					.getLoadBalancer();
			servletResponse.getOutputStream().println(lb.getLoadBalancerStats().toString());
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	}

}
