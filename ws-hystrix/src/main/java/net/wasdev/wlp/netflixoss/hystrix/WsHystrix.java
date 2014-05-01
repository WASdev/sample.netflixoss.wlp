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
package net.wasdev.wlp.netflixoss.hystrix;

import java.util.logging.Logger;

import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;

public class WsHystrix {
    private static final String CLASSNAME = WsHystrix.class.getName();
    private static final Logger LOGGER = Logger.getLogger(CLASSNAME);

    void activate() {
        LOGGER.entering(CLASSNAME, "activate");
        LOGGER.exiting(CLASSNAME, "activate");
    }

    void deactivate() {
        LOGGER.entering(CLASSNAME, "deactivate");
        LOGGER.exiting(CLASSNAME, "deactivate");
    }

    void setHystrixConcurrencyStrategy(HystrixConcurrencyStrategy concurrencyStrategy) {
        LOGGER.entering(CLASSNAME, "setHystrixConcurrencyStrategy", concurrencyStrategy);
        HystrixPlugins.getInstance().registerConcurrencyStrategy(concurrencyStrategy);
        LOGGER.exiting(CLASSNAME, "setHystrixConcurrencyStrategy");
    }

    void unsetHystrixConcurrencyStrategy(HystrixConcurrencyStrategy concurrencyStrategy) {
        LOGGER.entering(CLASSNAME, "unsetHystrixConcurrencyStrategy", concurrencyStrategy);
        //The concurrencyStrategy can only be set, and then only once
        LOGGER.exiting(CLASSNAME, "unsetHystrixConcurrencyStrategy");
    }
}
