package io.yawp.repository.query;

import io.yawp.repository.query.condition.BaseCondition;
import io.yawp.repository.query.condition.Condition;
import io.yawp.servlet.HttpException;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class DatastoreQueryOptions {

	private BaseCondition condition;

	private List<DatastoreQueryOrder> preOrders;

	private List<DatastoreQueryOrder> postOrders;

	private Integer limit;

	public static DatastoreQueryOptions parse(String json) {
		return new DatastoreQueryOptions(json);
	}

	public DatastoreQueryOptions(String json) {
		JsonObject jsonObject = (JsonObject) new JsonParser().parse(json);

		this.condition = parseCondition(jsonObject.get("where"));
		this.preOrders = parseOrders(jsonObject.getAsJsonArray("order"));
		this.postOrders = parseOrders(jsonObject.getAsJsonArray("sort"));
		this.limit = parseLimit(jsonObject.get("limit"));
	}

	private Integer parseLimit(JsonElement jsonElement) {
		if (jsonElement == null) {
			return null;
		}

		return jsonElement.getAsInt();
	}

	private List<DatastoreQueryOrder> parseOrders(JsonArray jsonArray) {
		if (jsonArray == null) {
			return null;
		}

		List<DatastoreQueryOrder> orders = new ArrayList<DatastoreQueryOrder>();

		for (JsonElement jsonElement : jsonArray) {
			String entity = getJsonStringValue(jsonElement, "e");
			String property = getJsonStringValue(jsonElement, "p");
			String direction = getJsonStringValue(jsonElement, "d");
			orders.add(new DatastoreQueryOrder(entity, property, direction));
		}

		return orders;
	}

	private String getJsonStringValue(JsonElement jsonElement, String key) {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		if (!jsonObject.has(key)) {
			return null;
		}
		return jsonObject.get(key).getAsString();
	}

	private Object getJsonObjectValue(JsonElement jsonElement) {
		if (jsonElement.isJsonArray()) {
			return getJsonObjectValueForArrays(jsonElement);
		}
		if (jsonElement.isJsonNull()) {
			return null;
		}

		JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();

		if (jsonPrimitive.isNumber()) {
			if (jsonPrimitive.getAsString().indexOf(".") != -1) {
				return jsonPrimitive.getAsDouble();
			}
			return jsonPrimitive.getAsLong();
		}

		if (jsonPrimitive.isString()) {
			return jsonPrimitive.getAsString();
		}

		// TODO timestamp
		throw new RuntimeException("Invalid json value: " + jsonPrimitive.getAsString());
	}

	private Object getJsonObjectValueForArrays(JsonElement jsonElement) {
		JsonArray array = jsonElement.getAsJsonArray();
		List<String> els = new ArrayList<>();
		for (JsonElement e : array) {
			els.add(e.getAsJsonPrimitive().getAsString());
		}
		return els;
	}

	private BaseCondition parseCondition(JsonElement json) {
		if (json == null) {
			return null;
		}
		if (json.isJsonArray()) {
			return parseSimpleCondition(json.getAsJsonArray());
		}
		return parseConditionObject(json.getAsJsonObject());
	}

	private BaseCondition parseSimpleCondition(JsonArray jsonArray) {
		if (jsonArray.size() % 3 != 0) {
			throw new HttpException(422,
			        "Array condition in where must have size multiple of three ([<field1>, <op1>, <value1>, <field2>, <op2>, <value2>, ...])");
		}
		BaseCondition[] conditions = new BaseCondition[jsonArray.size() / 3];
		for (int i = 0; i < jsonArray.size(); i += 3) {
			String field = jsonArray.get(i).getAsString();
			String op = jsonArray.get(i + 1).getAsString();
			Object value = getJsonObjectValue(jsonArray.get(i + 2));
			conditions[i / 3] = Condition.c(field, op, value);
		}
		return Condition.and(conditions);
	}

	private BaseCondition parseConditionObject(JsonObject json) {
		if (json.has("c")) {
			return parseJoinedCondition(json);
		}

		return parseSimpleCondition(json);
	}

	private BaseCondition parseJoinedCondition(JsonObject json) {
		String op = json.get("op").getAsString();

		List<BaseCondition> conditions = new ArrayList<BaseCondition>();

		for (JsonElement jsonElement : json.getAsJsonArray("c")) {
			conditions.add(parseConditionObject(jsonElement.getAsJsonObject()));
		}

		if (op.equalsIgnoreCase("and")) {
			return Condition.and(conditions.toArray(new BaseCondition[] {}));
		}
		if (op.equalsIgnoreCase("or")) {
			return Condition.or(conditions.toArray(new BaseCondition[] {}));
		}

		throw new RuntimeException("Invalid joined condition operator");
	}

	private BaseCondition parseSimpleCondition(JsonObject json) {
		String field = json.get("p").getAsString();
		String op = json.get("op").getAsString();
		Object value = getJsonObjectValue(json.get("v"));
		return Condition.c(field, op, value);
	}

	public BaseCondition getCondition() {
		return condition;
	}

	public List<DatastoreQueryOrder> getPreOrders() {
		return preOrders;
	}

	public List<DatastoreQueryOrder> getPostOrders() {
		return postOrders;
	}

	public Integer getLimit() {
		return this.limit;
	}
}
