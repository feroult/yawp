package io.yawp.driver.appengine.pipes;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.appengine.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.appengine.repackaged.org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

public abstract class PipeBaseTask implements DeferredTask {

    protected static final String INDEX_PREFIX = "__yawp-pipe-index-";

    protected static final String LOCK_PREFIX = "__yawp-pipe-lock-";

    protected static final long POW_2_16 = (long) Math.pow(2, 16);

    protected static final long POW_2_15 = (long) Math.pow(2, 15);

    protected transient MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();

    protected Payload payload;

    protected transient String sinkId;

    public PipeBaseTask(Payload payload) {
        this.payload = payload;
        this.sinkId = payload.getSinkUri();
    }

    protected String createIndexHash(Integer index) {
        return String.format("%s-%s", hash("" + index), sinkId);
    }

    private String hash(String string) {
        byte[] shaArray = DigestUtils.sha(string);
        byte[] encodedArray = new Base64().encode(shaArray);
        String returnValue = new String(encodedArray);
        returnValue = StringUtils.removeEnd(returnValue, "\r\n");
        return returnValue.replaceAll("=", "").replaceAll("/", "-").replaceAll("\\+", "\\_");
    }

}
