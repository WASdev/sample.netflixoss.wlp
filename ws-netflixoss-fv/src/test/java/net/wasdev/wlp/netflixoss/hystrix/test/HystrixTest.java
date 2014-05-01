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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class HystrixTest extends TestCase {

    public HystrixTest(String testName) {
	super( testName );
    }
	
    public void testHystrix() throws Exception {

        URL url = new URL("http://localhost:9080/ws-netflixoss-fv/HystrixTestServlet");
        URLConnection urlConnection = url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        String inputLine;
	boolean result = false;
        while ((inputLine = in.readLine()) != null) {
	    System.out.println("OUTPUT START **********");
	    System.out.println(inputLine);
	    System.out.println("OUTPUT END **********");
	    result = Boolean.parseBoolean(inputLine);
	}
        in.close();
		
	assertEquals(result,true);
    }
	
    public static Test suite() {
        return new TestSuite( HystrixTest.class );
    }

}
