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
package net.wasdev.wlp.netflixoss.archaius.test;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException; 
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;

/**
 * Servlet implementation class NestedPropertiesServlet
 */
@WebServlet("/NestedPropertiesServlet")
public class NestedPropertiesServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	DynamicStringProperty prop1 = null;
	DynamicStringProperty prop2 = null;
	DynamicStringProperty prop3 = null;
	DynamicStringProperty prop4 = null;
	DynamicStringProperty prop5 = null;
	DynamicStringProperty prop6 = null;
	DynamicStringProperty prop7 = null;
	DynamicStringProperty prop8 = null;
	DynamicStringProperty prop9 = null;
	DynamicStringProperty prop10 = null;
	DynamicStringProperty prop11 = null;
	DynamicStringProperty prop12 = null;
	DynamicStringProperty prop13 = null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public NestedPropertiesServlet() {
        super();

        prop1 = DynamicPropertyFactory.getInstance().getStringProperty("StockListingAPP.listProperties.tableProperties.name", "DEFAULT");
        prop2 = DynamicPropertyFactory.getInstance().getStringProperty("StockListingAPP.listProperties.tableProperties.height", "DEFAULT");
        prop3 = DynamicPropertyFactory.getInstance().getStringProperty("StockListingAPP.chartProperties.chartName", "DEFAULT");
        prop4 = DynamicPropertyFactory.getInstance().getStringProperty("StockListingAPP.chartProperties.chartStyle.color", "DEFAULT");
        prop5 = DynamicPropertyFactory.getInstance().getStringProperty("PhaniWebApp.myJDBCCredentials.myUserId", "DEFAULT");
        prop6 = DynamicPropertyFactory.getInstance().getStringProperty("PhaniWebApp.myJDBCServer", "DEFAULT");
        prop7 = DynamicPropertyFactory.getInstance().getStringProperty("PhaniWebApp.myJDBCCredentials.myPassword", "DEFAULT");
        prop8 = DynamicPropertyFactory.getInstance().getStringProperty("StockListingAPP.listProperties.tableProperties.width", "DEFAULT");
        prop9 = DynamicPropertyFactory.getInstance().getStringProperty("StockListingAPP.chartProperties.chartStyle.shade", "DEFAULT");
        prop10 = DynamicPropertyFactory.getInstance().getStringProperty("PhaniEjbApp.myJMSCredentials.myJMSPassword", "DEFAULT");
        prop11 = DynamicPropertyFactory.getInstance().getStringProperty("PhaniWebApp.myJDBCPort", "DEFAULT");
        prop12 = DynamicPropertyFactory.getInstance().getStringProperty("PhaniEjbApp.myJMSCredentials.myJMSUser", "DEFAULT");
        prop13 = DynamicPropertyFactory.getInstance().getStringProperty("PhaniEjbApp.myJMSServer", "DEFAULT");

    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		PrintWriter out  = response.getWriter();
		
		out.println("<table border=\"1\">");
		
		out.println("<tr>");
			out.println("<th>");
				out.println("Property");
			out.println("</th>");
			out.println("<th>");
				out.println("Value");		
			out.println("</th>");
		out.println("</tr>");
		
		printData(out,"PhaniWebApp.myJDBCServer",prop6.get());
		printData(out,"PhaniWebApp.myJDBCPort",prop11.get());
		printData(out, "PhaniWebApp.myJDBCCredentials.myUserId", prop5.get());
		printData(out, "PhaniWebApp.myJDBCCredentials.myPassword", prop7.get());
		printData(out, "PhaniEjbApp.myJMSServer", prop13.get());
		printData(out, "PhaniEjbApp.myJMSCredentials.myJMSUser", prop12.get());
		printData(out, "PhaniEjbApp.myJMSCredentials.myJMSPassword", prop10.get());
		printData(out, "StockListingAPP.listProperties.tableProperties.name", prop1.get());
		printData(out, "StockListingAPP.listProperties.tableProperties.width", prop8.get());
		printData(out, "StockListingAPP.listProperties.tableProperties.height", prop2.get());
		printData(out, "StockListingAPP.chartProperties.chartName", prop3.get());
		printData(out, "StockListingAPP.chartProperties.chartStyle.color", prop4.get());
		printData(out, "StockListingAPP.chartProperties.chartStyle.shade", prop9.get());		
		
		out.println("</table>");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}
	
	private void printData(PrintWriter out, String propName, String propValue){
		out.println("<tr>");
		out.println("<td>");
		out.println(propName);
		out.println("</td>");
		out.println("<td>");
		out.println(propValue);
		out.println("</td>");
		out.println("</tr>");
	}

}
