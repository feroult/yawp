package io.yawp.repository.models.basic;

import static org.junit.Assert.assertEquals;
import io.yawp.commons.utils.DateUtils;
import io.yawp.repository.IdRef;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.annotations.Id;
import io.yawp.repository.annotations.Index;
import io.yawp.repository.annotations.Json;
import io.yawp.repository.annotations.Text;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Endpoint(path = "/basic_objects")
public class BasicObject {

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

}