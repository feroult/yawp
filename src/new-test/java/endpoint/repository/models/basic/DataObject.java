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
	private JsonPojo jsonValue;

	@Json
	private List<JsonPojo> jsonList;

	@Json
	private Map<Long, JsonPojo> jsonMap;

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

	public JsonPojo getJsonValue() {
		return jsonValue;
	}

	public void setJsonValue(JsonPojo jsonValue) {
		this.jsonValue = jsonValue;
	}

	public List<JsonPojo> getJsonList() {
		return jsonList;
	}

	public void setJsonList(List<JsonPojo> jsonList) {
		this.jsonList = jsonList;
	}

	public Map<Long, JsonPojo> getJsonMap() {
		return jsonMap;
	}

	public void setJsonMap(Map<Long, JsonPojo> jsonMap) {
		this.jsonMap = jsonMap;
	}

	public void assertObject(String stringValue, int intValue, long longValue, double doubleValue, boolean booleanValue, String timestamp) {
		assertEquals(intValue, getIntValue());
		assertEquals(longValue, getLongValue());
		assertEquals(doubleValue, getDoubleValue(), 0);
		assertEquals(booleanValue, isBooleanValue());
		assertEquals(DateUtils.toTimestamp(timestamp), getDateValue());
		assertEquals(stringValue, getStringValue());
	}

}