package endpoint.repository.models.basic;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;
import java.util.Map;

import endpoint.repository.IdRef;
import endpoint.repository.annotations.Endpoint;
import endpoint.repository.annotations.Id;
import endpoint.repository.annotations.Json;
import endpoint.utils.DateUtils;

@Endpoint(path = "/data_objects")
public class DataObject {

	@Id
	private IdRef<DataObject> id;

	private int intValue;

	private long longValue;

	private double doubleValue;

	private boolean booleanValue;

	private Date dateValue;

	private String stringValue;

	@Json
	private JsonPojo pojoValue;

	@Json
	private List<JsonPojo> pojoList;

	@Json
	private Map<Long, JsonPojo> pojoMap;

	public DataObject() {

	}

	public DataObject(String stringValue) {
		this.stringValue = stringValue;
	}

	public IdRef<DataObject> getId() {
		return id;
	}

	public void setId(IdRef<DataObject> id) {
		this.id = id;
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

	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	public JsonPojo getPojoValue() {
		return pojoValue;
	}

	public void setPojoValue(JsonPojo pojoValue) {
		this.pojoValue = pojoValue;
	}

	public List<JsonPojo> getPojoList() {
		return pojoList;
	}

	public void setPojoList(List<JsonPojo> pojoList) {
		this.pojoList = pojoList;
	}

	public Map<Long, JsonPojo> getPojoMap() {
		return pojoMap;
	}

	public void setPojoMap(Map<Long, JsonPojo> pojoMap) {
		this.pojoMap = pojoMap;
	}

	public void assertObject(int intValue, long longValue, double doubleValue, boolean booleanValue, String timestamp, String stringValue) {
		assertFields(intValue, longValue, doubleValue, booleanValue, timestamp, stringValue);
	}

	public void assertObjectWithoutKey(int intValue, long longValue, double doubleValue, boolean booleanValue, String timestamp,
			String stringValue) {
		assertFields(intValue, longValue, doubleValue, booleanValue, timestamp, stringValue);
	}

	private void assertFields(int intValue, long longValue, double doubleValue, boolean booleanValue, String timestamp, String stringValue) {
		assertEquals(intValue, getIntValue());
		assertEquals(longValue, getLongValue());
		assertEquals(doubleValue, getDoubleValue(), 0);
		assertEquals(booleanValue, isBooleanValue());
		assertEquals(DateUtils.toTimestamp(timestamp), getDateValue());
		assertEquals(stringValue, getStringValue());
	}

}