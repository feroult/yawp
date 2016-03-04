package io.yawp.commons.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.UUID;

public class NameGenerator {
    private static Base64 BASE64 = new Base64(true);

    public static String generateFromUUID() {
        UUID uuid = UUID.randomUUID();
        byte[] byteArray = toByteArray(uuid);
        return generate(byteArray);
    }

    public static String generateFromString(String s) {
        return generate(s.getBytes());
    }

    public static UUID convertToUUID(String name) {
        UUID returnValue = null;
        if (StringUtils.isNotBlank(name)) {
            byte[] decodedArray = BASE64.decode(name);
            returnValue = fromByteArray(decodedArray);
        }
        return returnValue;
    }

    public static String convertToString(String name) {
        byte[] decodedArray = BASE64.decode(name);
        return new String(decodedArray);
    }

    private static String generate(byte[] byteArray) {
        byte[] encodedArray = BASE64.encode(byteArray);
        String returnValue = new String(encodedArray);
        returnValue = StringUtils.removeEnd(returnValue, "\r\n");
        return returnValue.replaceAll("=", "").replaceAll("/", "-").replaceAll("\\+", "\\_");
    }

    private static byte[] toByteArray(UUID uuid) {
        byte[] byteArray = new byte[(Long.SIZE / Byte.SIZE) * 2];
        ByteBuffer buffer = ByteBuffer.wrap(byteArray);
        LongBuffer longBuffer = buffer.asLongBuffer();
        longBuffer.put(new long[]{uuid.getMostSignificantBits(), uuid.getLeastSignificantBits()});
        return byteArray;
    }

    private static UUID fromByteArray(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        LongBuffer longBuffer = buffer.asLongBuffer();
        return new UUID(longBuffer.get(0), longBuffer.get(1));
    }
}
