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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.properties.HystrixProperty;


public class WsHystrixConcurrencyStrategy extends HystrixConcurrencyStrategy {
    private static final String CLASSNAME = WsHystrixConcurrencyStrategy.class.getName();
    private static final Logger LOGGER = Logger.getLogger(CLASSNAME);

    private ThreadPoolExecutorFactory executorFactory;
    private BlockingQueueFactory blockingQueueFactory;
    private final ConcurrentMap<HystrixThreadPoolKey, ThreadPoolExecutor> poolMap = new ConcurrentHashMap<HystrixThreadPoolKey, ThreadPoolExecutor>();

    void activate() {
        LOGGER.entering(CLASSNAME, "activate");
        LOGGER.exiting(CLASSNAME, "activate");
    }

    void deactivate() {
        LOGGER.entering(CLASSNAME, "deactivate");
        LOGGER.exiting(CLASSNAME, "deactivate");
    }

    void setThreadPoolExecutorFactory(ThreadPoolExecutorFactory threadPoolExecutorFactory) {
        LOGGER.entering(CLASSNAME, "setThreadPoolExecutorFactory", threadPoolExecutorFactory);
        executorFactory = threadPoolExecutorFactory;
        LOGGER.exiting(CLASSNAME, "setThreadPoolExecutorFactory");
    }

    void unsetThreadPoolExecutorFactory(ThreadPoolExecutorFactory threadPoolExecutorFactory) {
        LOGGER.entering(CLASSNAME, "unsetThreadPoolExecutorFactory");
        LOGGER.exiting(CLASSNAME, "unsetThreadPoolExecutorFactory");
    }

    void setBlockingQueueFactory(BlockingQueueFactory blockingQueueFactory) {
        LOGGER.entering(CLASSNAME, "setBlockingQueueFactory", blockingQueueFactory);
        this.blockingQueueFactory = blockingQueueFactory;
        LOGGER.exiting(CLASSNAME, "setBlockingQueueFactory");
    }

    void unsetBlockingQueueFactory(BlockingQueueFactory blockingQueueFactory) {
        LOGGER.entering(CLASSNAME, "unsetBlockingQueueFactory");
        LOGGER.exiting(CLASSNAME, "unsetBlockingQueueFactory");
    }

    @Override
    public ThreadPoolExecutor getThreadPool(HystrixThreadPoolKey threadPoolKey,
            HystrixProperty<Integer> corePoolSize,
            HystrixProperty<Integer> maximumPoolSize,
            HystrixProperty<Integer> keepAliveTime, TimeUnit unit,
            BlockingQueue<Runnable> workQueue) {
        ThreadPoolExecutor pool = poolMap.get(threadPoolKey);
        if (pool == null) {
            ThreadPoolExecutor newPool = 
                executorFactory.createThreadPoolExecutor(
                        corePoolSize.get(), maximumPoolSize.get(),
                        keepAliveTime.get(), unit, workQueue);
            pool = poolMap.putIfAbsent(threadPoolKey, newPool);
            pool = (pool == null) ? newPool : pool;
        }

        return pool;
    }

    @Override
    public BlockingQueue<Runnable> getBlockingQueue(int maxQueueSize) {
        return blockingQueueFactory.createBlockingQueue(maxQueueSize);
    }
}
