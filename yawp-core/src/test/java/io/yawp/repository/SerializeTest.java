package io.yawp.repository;

import io.yawp.commons.utils.EndpointTestCase;
import io.yawp.repository.models.basic.BasicObject;
import io.yawp.repository.models.basic.Pojo;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertEquals;

public class SerializeTest extends EndpointTestCase {

    @Test
    public void testSerializeDeserialize() throws IOException, ClassNotFoundException {
        BasicObject object = new BasicObject();

        object.setId(id(BasicObject.class, 100l));
        object.setJsonValue(new Pojo("x"));
        object.setLazyPojo(new Pojo("y"));

        byte[] bytes = serialize(object);
        BasicObject deserializedObject = deserialize(bytes);

        assertEquals((Long) 100l, deserializedObject.getId().asLong());
        assertEquals("x", deserializedObject.getJsonValue().getStringValue());
        assertEquals("y", deserializedObject.getLazyPojo().getStringValue());
    }

    private byte[] serialize(BasicObject object) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(bos);
        os.writeObject(object);
        return bos.toByteArray();
    }

    private BasicObject deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput is = new ObjectInputStream(bis);
        return (BasicObject) is.readObject();
    }
}
