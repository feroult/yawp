package endpoint.servlet;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import endpoint.repository.IdRef;
import endpoint.repository.Repository;

// TODO move to servlet package
public class IdRefJsonAdapter implements JsonSerializer<IdRef<?>>, JsonDeserializer<IdRef<?>> {

	private Repository r;

	public IdRefJsonAdapter(Repository r) {
		this.r = r;
	}

	@Override
	public JsonElement serialize(IdRef<?> idRef, Type type, JsonSerializationContext ctx) {
		return new JsonPrimitive(idRef.toString());
	}

	@Override
	public IdRef<?> deserialize(JsonElement json, Type type, JsonDeserializationContext ctx) throws JsonParseException {
		String path = json.getAsJsonPrimitive().getAsString();
		return IdRef.parse(r, path);
	}

}
