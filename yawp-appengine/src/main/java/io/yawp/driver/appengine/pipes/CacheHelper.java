package io.yawp.driver.appengine.pipes;

import com.google.appengine.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.appengine.repackaged.org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

public class CacheHelper {

    private static final String INDEX_PREFIX = "__yawp-pipe-index";

    private static final String LOCK_PREFIX = "__yawp-pipe-lock";

    public static final long POW_2_16 = (long) Math.pow(2, 16);

    public static final long POW_2_15 = (long) Math.pow(2, 15);

    public static String createIndexCacheKey(String sinkUri) {
        return String.format("%s-%s", INDEX_PREFIX, sinkUri);
    }

    public static String createIndexHash(String sinkUri, Integer index) {
        return String.format("%s-%s", hash("" + index), sinkUri);
    }

    public static String createLockCacheKey(String sinkUri, Integer index) {
        return String.format("%s-%s-%d", LOCK_PREFIX, sinkUri, index);
    }

    private static String hash(String string) {
        byte[] shaArray = DigestUtils.sha(string);
        byte[] encodedArray = new Base64().encode(shaArray);
        String returnValue = new String(encodedArray);
        returnValue = StringUtils.removeEnd(returnValue, "\r\n");
        return returnValue.replaceAll("=", "").replaceAll("/", "-").replaceAll("\\+", "\\_");
    }


}
