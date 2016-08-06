package io.yawp.driver.appengine.pipes.flow;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

public class Mutex {

    private String key;

    private long maxWait;

    private String ns;


    public Mutex(String key, long maxWait, String ns) {
        this.key = key;
        this.maxWait = maxWait;
        this.ns = ns;
    }

    public boolean aquire() {
        MemcacheService memcache = MemcacheServiceFactory.getMemcacheService(ns);
        long start = System.currentTimeMillis();
        while (true) {
            if (memcache.increment(key, 1L, 0L) == 1L) {
                return true;
            }
            if (System.currentTimeMillis() - start > maxWait) {
                return false;
            }
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
            }
        }
    }

    public void release() {
        MemcacheService memcache = MemcacheServiceFactory.getMemcacheService(ns);
        memcache.delete(key);
    }

}
