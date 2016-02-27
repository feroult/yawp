package io.yawp.repository.pipes.pump;

import io.yawp.commons.utils.EndpointTestCase;
import io.yawp.repository.models.basic.BasicObject;

import java.io.*;
import java.util.List;

import static org.junit.Assert.*;

public abstract class PumpTestCase<T> extends EndpointTestCase {

    protected void pumpTestBasicList(Pump<T> pump, List<T> list) {
        pump.add(list.get(0));
        pump.addAll(list.subList(1, 3));

        assertTrue(pump.hasMore());

        assertList(pump.more(), 1, 2);
        assertList(pump.more(), 3);

        assertFalse(pump.hasMore());
    }

    protected void pumpTestBasicQuery(Pump<T> pump) {
        pump.addQuery(yawp(BasicObject.class));

        assertTrue(pump.hasMore());

        assertList(pump.more(), 1, 2);
        assertList(pump.more(), 3);

        assertFalse(pump.hasMore());
    }

    protected void pumpTestMultipleQueries(Pump<T> pump) {
        addMultipleQueries(pump);

        assertTrue(pump.hasMore());
        assertList(pump.more(), 1, 2);
        assertList(pump.more(), 3, 4);
        assertList(pump.more(), 5, 6);

        assertTrue(pump.hasMore());
        assertList(pump.more(), 7, 8);
        assertList(pump.more(), 9, 10);
        assertList(pump.more(), 11);

        assertFalse(pump.hasMore());
    }

    protected void pumpTestListAndMultipleQueries(Pump<T> pump, List<T> list) {
        addMultipleQueries(pump);
        pump.addAll(list);

        pump = serializeAndDeserialize(pump);
        addMultipleQueries(pump);

        assertTrue(pump.hasMore());
        assertList(pump.more(), 12, 13);
        assertList(pump.more(), 14, 15);
        assertList(pump.more(), 16, 1);

        assertTrue(pump.hasMore());
        assertList(pump.more(), 2, 3);
        assertList(pump.more(), 4, 5);
        assertList(pump.more(), 6, 7);

        pump = serializeAndDeserialize(pump);
        addMultipleQueries(pump);

        assertTrue(pump.hasMore());
        assertList(pump.more(), 8, 9);
        assertList(pump.more(), 10, 11);

        assertTrue(pump.hasMore());
        assertEquals(0, pump.more().size());
        assertFalse(pump.hasMore());
    }

    private void addMultipleQueries(Pump<T> pump) {
        pump.addQuery(yawp(BasicObject.class).where("intValue", "<=", 3));
        pump.addQuery(yawp(BasicObject.class).where("intValue", ">", 3).and("intValue", "<=", 6));
        pump.addQuery(yawp(BasicObject.class).where("intValue", ">", 6).and("intValue", "<=", 11));
    }

    private Pump<T> serializeAndDeserialize(Pump<T> pump) {
        return deserialize(serialize(pump));
    }

    private byte[] serialize(Pump<T> pump) {
        ByteArrayOutputStream bos = null;
        ObjectOutputStream out = null;
        try {
            bos = new ByteArrayOutputStream();
            out = new ObjectOutputStream(bos);
            out.writeObject(pump);
            return bos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
            }
            try {
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
            }
        }
    }

    private Pump<T> deserialize(byte[] pumpBytes) {
        ByteArrayInputStream bis = null;
        ObjectInputStream in = null;
        try {
            bis = new ByteArrayInputStream(pumpBytes);
            in = new ObjectInputStream(bis);
            return (Pump<T>) in.readObject();

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
            }
            try {
                if (bis != null) {
                    bis.close();
                }
            } catch (IOException e) {
            }
        }
    }

    protected abstract void assertList(List<T> list, int... values);
}
