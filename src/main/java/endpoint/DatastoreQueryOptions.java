package endpoint;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class DatastoreQueryOptions {

	private Object[] where;
	private List<DatastoreQueryOrder> preOrders;
	private List<DatastoreQueryOrder> postOrders;
	private Integer limit;

	public static DatastoreQueryOptions parse(String json) {
		return new DatastoreQueryOptions(json);
	}

	public DatastoreQueryOptions(String json) {
		JsonObject jsonObject = (JsonObject) new JsonParser().parse(json);
		this.where = parseWhere(jsonObject.getAsJsonArray("where"));
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
			String property = jsonElement.getAsJsonObject().get("p").getAsString();
			String direction = jsonElement.getAsJsonObject().get("d").getAsString();
			orders.add(new DatastoreQueryOrder(property, direction));
		}

		return orders;
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
