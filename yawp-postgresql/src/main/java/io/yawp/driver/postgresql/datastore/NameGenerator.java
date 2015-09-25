package io.yawp.driver.postgresql.datastore;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

public class NameGenerator {
	private static Base64 BASE64 = new Base64(true);

	public static String generate() {
		UUID uuid = UUID.randomUUID();
		byte[] uuidArray = toByteArray(uuid);
		byte[] encodedArray = BASE64.encode(uuidArray);
		String returnValue = new String(encodedArray);
		returnValue = StringUtils.removeEnd(returnValue, "\r\n");
		return returnValue.replaceAll("=", "").replaceAll("/", "-").replaceAll("\\+", "\\_");
	}

	public static UUID convert(String name) {
		UUID returnValue = null;
		if (StringUtils.isNotBlank(name)) {
			// Convert base64 string to a byte array
			byte[] decodedArray = BASE64.decode(name);
			returnValue = fromByteArray(decodedArray);
		}
		return returnValue;
	}

	private static byte[] toByteArray(UUID uuid) {
		byte[] byteArray = new byte[(Long.SIZE / Byte.SIZE) * 2];
		ByteBuffer buffer = ByteBuffer.wrap(byteArray);
		LongBuffer longBuffer = buffer.asLongBuffer();
		longBuffer.put(new long[] { uuid.getMostSignificantBits(), uuid.getLeastSignificantBits() });
		return byteArray;
	}

	private static UUID fromByteArray(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		LongBuffer longBuffer = buffer.asLongBuffer();
		return new UUID(longBuffer.get(0), longBuffer.get(1));
	}
}
