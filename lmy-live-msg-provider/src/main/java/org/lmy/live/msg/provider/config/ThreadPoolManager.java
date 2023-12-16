package org.lmy.live.msg.provider.config;

import java.util.concurrent.*;

public class ThreadPoolManager {
    public static ThreadPoolExecutor commonAsyncPool=new ThreadPoolExecutor(2, 8, 3, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000),
            new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {

                    Thread thread=new Thread(r);
                    thread.setName("commonAsyncPool - "+ ThreadLocalRandom.current().nextInt(10000));
                    return thread;

                }
            });
}
