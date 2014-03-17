package endpoint.models;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class DatastoreQueryOptions {

	private Object[] where;
	private String[] order;
	private Integer limit;

	public static DatastoreQueryOptions parse(String json) {
		return new DatastoreQueryOptions(json);
	}

	public DatastoreQueryOptions(String json) {
		JsonObject jsonObject = (JsonObject) new JsonParser().parse(json);
		this.where = parseWhere(jsonObject.getAsJsonArray("where"));
		this.order = parseOrder(jsonObject.getAsJsonArray("order"));
		this.limit = parseLimit(jsonObject.get("limit"));
	}

	private Integer parseLimit(JsonElement jsonElement) {
		if (jsonElement == null) {
			return null;
		}

		return jsonElement.getAsInt();
	}

	private String[] parseOrder(JsonArray jsonArray) {
		if (jsonArray == null) {
			return null;
		}

		List<String> order = new ArrayList<String>();

		for (JsonElement jsonElement : jsonArray) {
			order.add(jsonElement.getAsString());
		}

		return order.toArray(new String[order.size()]);
	}

	private Object[] parseWhere(JsonArray jsonArray) {
		if (jsonArray == null) {
			return null;
		}

		List<Object> where = new ArrayList<Object>();

		for (JsonElement jsonElement : jsonArray) {
			JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();

			if (jsonPrimitive.isNumber()) {
				if (jsonPrimitive.getAsString().indexOf(".") != -1) {
					where.add(jsonPrimitive.getAsDouble());
					continue;
				} else {
					where.add(jsonPrimitive.getAsLong());
					continue;
				}
			}

			if (jsonPrimitive.isString()) {
				where.add(jsonPrimitive.getAsString());
				continue;
			}

			// TODO timestamp?
		}

		return where.toArray(new Object[where.size()]);
	}

	public Object[] getWhere() {
		return this.where;
	}

	public String[] getOrder() {
		return this.order;
	}

	public Integer getLimit() {
		return this.limit;
	}
}
