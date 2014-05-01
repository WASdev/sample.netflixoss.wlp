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
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;

/**
 * Servlet implementation class Archaius
 */
@WebServlet("/archaius")
public class Archaius extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final DynamicStringProperty stringProperty = DynamicPropertyFactory
			.getInstance()
			.getStringProperty("archaiusServlet.string", "Default Value"); //

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Archaius() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {		
		SimpleDateFormat sdf = new SimpleDateFormat();
		response.getOutputStream().println("value=\""+stringProperty.getValue() + "\" lastChanged="
				+ sdf.format(new Date(stringProperty.getChangedTimestamp())));
	}

}
