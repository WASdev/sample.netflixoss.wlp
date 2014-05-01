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
package net.wasdev.wlp.netflixoss.archaius;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.apache.commons.configuration.AbstractConfiguration;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

import com.netflix.config.ConcurrentCompositeConfiguration;
import com.netflix.config.ConcurrentMapConfiguration;
import com.netflix.config.ConfigurationBackedDynamicPropertySupportImpl;
import com.netflix.config.ConfigurationManager;
import com.netflix.config.DynamicConfiguration;
import com.netflix.config.DynamicPropertySupport;
import com.netflix.config.FixedDelayPollingScheduler;
import com.netflix.config.PollResult;
import com.netflix.config.PolledConfigurationSource;

public class Activator implements BundleActivator, ManagedService {

	private BundleContext bContext;
	private ConcurrentMapConfiguration configuration = new ConcurrentMapConfiguration();
	private static String CLASSNAME = Activator.class.getName();
	private static Logger LOGGER = Logger.getLogger(CLASSNAME);

	@Override
	public void start(BundleContext context) throws Exception {
		LOGGER.entering(CLASSNAME, "start", context);
		this.bContext = context;
		context.registerService(ManagedService.class, this, getDefaults())
				.getReference();
		ConcurrentCompositeConfiguration finalConfig = new ConcurrentCompositeConfiguration();
		finalConfig.addConfiguration(configuration);
		ConfigurationManager.install(finalConfig);
		LOGGER.exiting(CLASSNAME, "start");
	}

	private static Hashtable<String, ?> getDefaults() {
		Hashtable<String, String> defaults = new Hashtable<String, String>();
		defaults.put(org.osgi.framework.Constants.SERVICE_PID, "archaius");
		return defaults;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		LOGGER.entering(CLASSNAME, "stop", context);
		LOGGER.exiting(CLASSNAME, "stop");
	}

	// From ManagedService
	@Override
	public void updated(Dictionary<String, ?> properties)
			throws ConfigurationException {
		LOGGER.entering(CLASSNAME, "updated", properties);

		try {

			if (properties != null) {

				ServiceReference configurationAdminReference = bContext
						.getServiceReference(ConfigurationAdmin.class.getName());

				if (configurationAdminReference != null) {

					ConfigurationAdmin configAdmin = (ConfigurationAdmin) bContext
							.getService(configurationAdminReference);

					Set<String> nestedKeys = new HashSet<String>();
					Enumeration<String> keys = properties.keys();
					while (keys.hasMoreElements()) {
						String key = keys.nextElement();
						Object val = properties.get(key);
						if (val instanceof String[]) {
							nestedKeys.addAll(generateKeyVals(configAdmin, key,
									(String[]) val));

						}
					}

					for (Iterator<String> it =  configuration.getKeys(); it.hasNext();) {
					    String key = it.next();
					    if (!nestedKeys.contains(key)) {
						LOGGER.fine("Removing key \'" + key + "\'");
						configuration.clearProperty(key);
					    }
					}
				}
			} else {
				LOGGER.fine("Removing all keys");
				configuration.clear();
			}

		} catch (Exception e) {
			RuntimeException re = new RuntimeException(
					"Failed to retrieve properties", e);
			LOGGER.throwing(CLASSNAME, "updated", re);
			throw re;
		}

		LOGGER.exiting(CLASSNAME, "updated");
	}

	private Set<String> generateKeyVals(ConfigurationAdmin configAdmin,
			String key, String[] values) throws IOException {

		Set<String> keys = new HashSet<String>();

		for (String value : values) {

			Configuration config = configAdmin.getConfiguration(value);
			Dictionary valueProps = config.getProperties();

			if (valueProps == null) {
				if (configuration.containsKey(key)) {
					if (!configuration.getProperty(key).equals(value)) {
						LOGGER.fine("Updating key \'" + key + "\' to value \'"
								+ value + "\'");
						configuration.setProperty(key, value);
					}
				} else {
					LOGGER.fine("Adding key \'" + key + "\' with value \'"
							+ value + "\'");
					configuration.addProperty(key, value);
				}
				keys.add(key);
			} else {
				Enumeration<String> valuePropKeys = valueProps.keys();
				while (valuePropKeys.hasMoreElements()) {
					String valuePropKey = valuePropKeys.nextElement();
					Object valuePropValue = valueProps.get(valuePropKey);
					if (valuePropValue instanceof String[]) {
						keys.addAll(generateKeyVals(configAdmin, key + "."
								+ valuePropKey, (String[]) valuePropValue));
					}
				}
			}

		}

		return keys;

	}

}
