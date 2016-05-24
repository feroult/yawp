package io.yawp.repository.models.basic;

import io.yawp.commons.utils.DateUtils;
import io.yawp.repository.IdRef;
import io.yawp.repository.LazyJson;
import io.yawp.repository.annotations.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static io.yawp.repository.Yawp.yawp;
import static org.junit.Assert.assertEquals;

@Endpoint(path = "/basic_objects")
public class BasicObject implements Serializable {

    private static final long serialVersionUID = -2799031983422266407L;

    @Id
    private IdRef<BasicObject> id;

    @Index
    private String stringValue;

    @Text
    private String textValue;

    @Index
    private int intValue;

    private long longValue;

    private double doubleValue;

    private boolean booleanValue;

    private Date dateValue;

    @Json
    private Pojo jsonValue;

    @Json
    private List<Pojo> jsonList;

    @Json
    private Map<Long, Pojo> jsonMap;

    private IdRef<BasicObject> objectId;

    @Index
    private Status status;

    @Index
    private List<String> stringList;

    @Index
    private List<IdRef<BasicObject>> idList;

    private LazyJson<Pojo> lazyPojo = new LazyJson<>();

    private LazyJson<List<Pojo>> lazyListPojo = new LazyJson<>();

    private LazyJson<Map<Long, Pojo>> lazyMapPojo = new LazyJson<>();

    public BasicObject() {

    }

    public BasicObject(String stringValue) {
        this.stringValue = stringValue;
    }

    public BasicObject(Long longValue) {
        this.longValue = longValue;
    }

    public BasicObject(String stringValue, long longValue) {
        this.stringValue = stringValue;
        this.longValue = longValue;
    }

    public BasicObject(String stringValue, IdRef<BasicObject> objectId) {
        this.stringValue = stringValue;
        this.objectId = objectId;
    }

    public IdRef<BasicObject> getId() {
        return id;
    }

    public void setId(IdRef<BasicObject> id) {
        this.id = id;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public String getTextValue() {
        return textValue;
    }

    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }

    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    public long getLongValue() {
        return longValue;
    }

    public void setLongValue(long longValue) {
        this.longValue = longValue;
    }

    public double getDoubleValue() {
        return doubleValue;
    }

    public void setDoubleValue(double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public boolean isBooleanValue() {
        return booleanValue;
    }

    public void setBooleanValue(boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    public Date getDateValue() {
        return dateValue;
    }

    public void setDateValue(Date dateValue) {
        this.dateValue = dateValue;
    }

    public Pojo getJsonValue() {
        return jsonValue;
    }

    public void setJsonValue(Pojo jsonValue) {
        this.jsonValue = jsonValue;
    }

    public List<Pojo> getJsonList() {
        return jsonList;
    }

    public void setJsonList(List<Pojo> jsonList) {
        this.jsonList = jsonList;
    }

    public Map<Long, Pojo> getJsonMap() {
        return jsonMap;
    }

    public void setJsonMap(Map<Long, Pojo> jsonMap) {
        this.jsonMap = jsonMap;
    }

    public IdRef<BasicObject> getObjectId() {
        return objectId;
    }

    public void setObjectId(IdRef<BasicObject> objectId) {
        this.objectId = objectId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<String> getStringList() {
        return stringList;
    }

    public void setStringList(List<String> stringList) {
        this.stringList = stringList;
    }

    public List<IdRef<BasicObject>> getIdList() {
        return idList;
    }

    public void setIdList(List<IdRef<BasicObject>> idList) {
        this.idList = idList;
    }

    public Pojo getLazyPojo() {
        return lazyPojo.get();
    }

    public void setLazyPojo(Pojo lazyPojo) {
        this.lazyPojo.set(lazyPojo);
    }

    public List<Pojo> getLazyListPojo() {
        return lazyListPojo.get();
    }

    public void setLazyListPojo(List<Pojo> lazyListPojo) {
        this.lazyListPojo.set(lazyListPojo);
    }

    public Map<Long, Pojo> getLazyMapPojo() {
        return lazyMapPojo.get();
    }

    public void setLazyMapPojo(Map<Long, Pojo> lazyMapPojo) {
        this.lazyMapPojo.set(lazyMapPojo);
    }

    public void assertObject(String stringValue, String textValue, int intValue, long longValue, double doubleValue, boolean booleanValue,
                             String timestamp) {
        assertEquals(intValue, getIntValue());
        assertEquals(longValue, getLongValue());
        assertEquals(doubleValue, getDoubleValue(), 0);
        assertEquals(booleanValue, isBooleanValue());
        assertEquals(DateUtils.toTimestamp(timestamp), getDateValue());
        assertEquals(textValue, getTextValue());
        assertEquals(stringValue, getStringValue());
    }

    public static List<BasicObject> saveManyBasicObjects(int n, int start, String stringValue) {
        List<BasicObject> objects = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            objects.add(saveOneObject(stringValue, i + start));
        }
        return objects;
    }

    public static List<BasicObject> saveManyBasicObjects(int n, String stringValue) {
        return saveManyBasicObjects(n, 0, stringValue);
    }

    public static List<BasicObject> saveManyBasicObjects(int n, int start) {
        return saveManyBasicObjects(n, start, "xpto");
    }

    public static List<BasicObject> saveManyBasicObjects(int n) {
        return saveManyBasicObjects(n, "xpto");
    }

    public static BasicObject saveOneObject(String stringValue, int i) {
        BasicObject object = new BasicObject();
        object.setStringValue(stringValue);
        object.setIntValue(i + 1);
        yawp.save(object);
        return object;
    }

}