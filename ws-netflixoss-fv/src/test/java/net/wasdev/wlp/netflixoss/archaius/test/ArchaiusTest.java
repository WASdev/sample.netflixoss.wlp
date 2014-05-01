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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class ArchaiusTest extends TestCase {

    public ArchaiusTest(String testName) {
	super( testName );
    }

    private static Map<String, String> properties = new HashMap<String, String>();
    {
        properties.put("StockListingAPP.listProperties.tableProperties.name", "LIST_ONE");
        properties.put("StockListingAPP.listProperties.tableProperties.height", "800px");
        properties.put("StockListingAPP.chartProperties.chartName", "CHART_STOCK");
        properties.put("StockListingAPP.chartProperties.chartStyle.color", "WHITISH_GREY");
        properties.put("PhaniWebApp.myJDBCCredentials.myUserId", "PHANI");
        properties.put("PhaniWebApp.myJDBCServer", "DB_SERVER");
        properties.put("PhaniWebApp.myJDBCCredentials.myPassword", "dummy_password");
        properties.put("StockListingAPP.listProperties.tableProperties.width", "300px");
        properties.put("StockListingAPP.chartProperties.chartStyle.shade", "WHITISH-DARK");
        properties.put("PhaniEjbApp.myJMSCredentials.myJMSPassword", "JMS-PASSWORD-3");
        properties.put("PhaniWebApp.myJDBCPort", "1200");
        properties.put("PhaniEjbApp.myJMSCredentials.myJMSUser", "MADGULA");
        properties.put("PhaniEjbApp.myJMSServer", "JMS_SERVER");
        properties.put("StockListingAPP.chartProperties.fontStyle", "italic,bold");

    }

    public void testArchaius() throws Exception {

	Thread.currentThread().sleep(3000);

	for (Map.Entry<String, String> entry: properties.entrySet()) {
	    URL url = new URL("http://localhost:9080/ws-netflixoss-fv/NestedPropertiesTestServlet?name=" + entry.getKey());
	    URLConnection urlConnection = url.openConnection();
	    BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
	    String result = in.readLine();
	    in.close();
	    assertEquals(entry.getValue(), result);
	}
    }
	
    public static Test suite() {
        return new TestSuite( ArchaiusTest.class );
    }

}
