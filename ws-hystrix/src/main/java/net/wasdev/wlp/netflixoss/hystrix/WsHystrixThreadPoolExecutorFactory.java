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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;


public class WsHystrixThreadPoolExecutorFactory implements ThreadPoolExecutorFactory, BlockingQueueFactory {
    private static final String CLASSNAME = WsHystrixThreadPoolExecutorFactory.class.getName();
    private static final Logger LOGGER = Logger.getLogger(CLASSNAME);

    void activate() {
        LOGGER.entering(CLASSNAME, "activate");
        LOGGER.exiting(CLASSNAME, "activate");
    }

    void deactivate() {
        LOGGER.entering(CLASSNAME, "deactivate");
        LOGGER.exiting(CLASSNAME, "deactivate");
    }

    ExecutorService executorService;

    void setExecutorService(ExecutorService executorService) {
        LOGGER.entering(CLASSNAME, "setExecutorService", executorService);
        this.executorService = executorService;
        LOGGER.exiting(CLASSNAME, "setExecutorService");
    }

    void unsetExecutorService(ExecutorService executorService) {
        LOGGER.entering(CLASSNAME, "unsetExecutorService", executorService);
        this.executorService = null;
        LOGGER.exiting(CLASSNAME, "unsetExecutorService");
    }

    @Override
    public BlockingQueue<Runnable> createBlockingQueue(int maxQueueSize) {
        return new SynchronousQueue<Runnable>();
    }

    @Override
    public ThreadPoolExecutor createThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
            int keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        LOGGER.entering(CLASSNAME, "createThreadPoolExecutor");
        try {
            return new WsHystrixThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, executorService);
        } finally {
            LOGGER.exiting(CLASSNAME, "createThreadPoolExecutor");
        }
    }

    private static final class WsHystrixThreadPoolExecutor extends ThreadPoolExecutor {
    private static final String CLASSNAME = WsHystrixThreadPoolExecutor.class.getName();
    private static final Logger LOGGER = Logger.getLogger(CLASSNAME);

        final AtomicInteger corePoolSize;
        final AtomicInteger maximumPoolSize;
        final AtomicReference<Timeout> keepAlive;
        final ExecutorService executorService;
        final AtomicInteger activeCallCount = new AtomicInteger();
        final AtomicInteger maxActiveCallCount = new AtomicInteger();
        final AtomicLong completedTaskCount = new AtomicLong();
        final AtomicLong taskCount = new AtomicLong();

        public WsHystrixThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
                long keepAliveTime, TimeUnit unit,
                BlockingQueue<Runnable> workQueue, ExecutorService executorService) {
            super(0, 1, 0, TimeUnit.SECONDS, workQueue);
            this.corePoolSize = new AtomicInteger(corePoolSize);
            this.maximumPoolSize = new AtomicInteger(maximumPoolSize);
            this.keepAlive = new AtomicReference<Timeout>(new Timeout(keepAliveTime, unit));
            this.executorService = executorService;
        }

        @Override
        public <T> Future<T> submit(Callable<T> task) {
            LOGGER.entering(CLASSNAME, "submit");
            final int maxCalls = maximumPoolSize.get();
            int activeCalls;
            do {
                activeCalls = activeCallCount.get();
                if (activeCalls >= maxCalls) {
                    throw new RejectedExecutionException("active = " + activeCalls + ", max = " + maxCalls);
                }
            } while (!!!activeCallCount.compareAndSet(activeCalls, (activeCalls + 1)));
            activeCalls++;
            int maxActiveCalls;
            do {
                maxActiveCalls = maxActiveCallCount.get();
                if (maxActiveCalls > activeCalls) {
                    break;
                }
            } while (!!!maxActiveCallCount.compareAndSet(maxActiveCalls, activeCalls));
            try {
                taskCount.incrementAndGet();
                Future<T> f = executorService.submit(task);
                try {
                    // this is currently necessary to bump the task from
                    // a thread local queue to the global task queue.
                    f.get(1, TimeUnit.NANOSECONDS);
                } catch (Exception e) {
                }
                return f;
            } finally {
                completedTaskCount.incrementAndGet();
                activeCallCount.decrementAndGet();
                LOGGER.exiting(CLASSNAME, "submit");
            }
        }

        /*=================*/

        @Override
        public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
            return executorService.awaitTermination(timeout, unit);
        }

        @Override
        public void execute(Runnable command) {
            throw new RejectedExecutionException("Not supported.");
        }

        @Override
        public int getActiveCount() {
            return activeCallCount.get();
        }

        @Override
        public long getCompletedTaskCount() {
            return completedTaskCount.get();
        }

        @Override
        public long getTaskCount() {
            return taskCount.get();
        }

        @Override
        public int getLargestPoolSize() {
            return maxActiveCallCount.get();
        }

        @Override
        public int getPoolSize() {
            return activeCallCount.get();
        }

        @Override
        public int prestartAllCoreThreads() {
            return 0;
        }

        @Override
        public boolean prestartCoreThread() {
            return false;
        }

        @Override
        public void allowCoreThreadTimeOut(boolean value) {
        }

        @Override
        public boolean allowsCoreThreadTimeOut() {
            return true;
        }

        @Override
        public ThreadFactory getThreadFactory() {
            return null;
        }

        /*=================*/

        @Override
        public int getCorePoolSize() {
            return corePoolSize.get();
        }

        @Override
        public void setCorePoolSize(int newSize) {
            int oldSize;
            do {
                oldSize = corePoolSize.get();
            } while (!!!corePoolSize.compareAndSet(oldSize, newSize));
        }

        @Override
        public int getMaximumPoolSize() {
            return maximumPoolSize.get();
        }

        @Override
        public void setMaximumPoolSize(int newSize) {
            int oldSize;
            do {
                oldSize = maximumPoolSize.get();
            } while (!!!maximumPoolSize.compareAndSet(oldSize, newSize));
        }

        @Override
        public long getKeepAliveTime(TimeUnit unit) {
            Timeout timeout = keepAlive.get();
            return unit.convert(timeout.time, timeout.unit);
        }

        @Override
        public void setKeepAliveTime(long time, TimeUnit unit) {
            final Timeout newTimeout = new Timeout(time, unit);
            Timeout oldTimeout;
            do {
                oldTimeout = keepAlive.get();
            } while (!!!keepAlive.compareAndSet(oldTimeout, newTimeout));
        }

        /*=================*/

        private static final class Timeout {
            final long time;
            final TimeUnit unit;

            Timeout(long time, TimeUnit unit) {
                this.time = time;
                this.unit = unit;
            }
        }
    }
}
