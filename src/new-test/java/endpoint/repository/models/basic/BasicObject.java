package endpoint.repository.models.basic;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;
import java.util.Map;

import endpoint.repository.IdRef;
import endpoint.repository.annotations.Endpoint;
import endpoint.repository.annotations.Id;
import endpoint.repository.annotations.Index;
import endpoint.repository.annotations.Json;
import endpoint.utils.DateUtils;

@Endpoint(path = "/basic_objects")
public class BasicObject {

	@Id
	private IdRef<BasicObject> id;

	@Index
	private String stringValue;

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

	public BasicObject() {

	}

	public BasicObject(String stringValue) {
		this.stringValue = stringValue;
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

	public void assertObject(String stringValue, int intValue, long longValue, double doubleValue, boolean booleanValue, String timestamp) {
		assertEquals(intValue, getIntValue());
		assertEquals(longValue, getLongValue());
		assertEquals(doubleValue, getDoubleValue(), 0);
		assertEquals(booleanValue, isBooleanValue());
		assertEquals(DateUtils.toTimestamp(timestamp), getDateValue());
		assertEquals(stringValue, getStringValue());
	}

}