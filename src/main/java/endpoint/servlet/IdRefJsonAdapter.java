package endpoint.servlet;

import java.lang.reflect.ParameterizedType;
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
import endpoint.repository.annotations.Endpoint;

// TODO move to servlet package
// TODO evaluate if whole tree should be returned or just current id as is
public class IdRefJsonAdapter implements JsonSerializer<IdRef<?>>, JsonDeserializer<IdRef<?>> {

	private Repository r;

	public IdRefJsonAdapter(Repository r) {
		this.r = r;
	}

	@Override
	public JsonElement serialize(IdRef<?> idRef, Type type, JsonSerializationContext ctx) {
		Class<?> clazz = idRef.getClazz();
		Endpoint endpointAnnotation = clazz.getAnnotation(Endpoint.class);
		return new JsonPrimitive(endpointAnnotation.path() + '/' + idRef.asLong());
	}

	@Override
	public IdRef<?> deserialize(JsonElement json, Type type, JsonDeserializationContext ctx) throws JsonParseException {
		String id = json.getAsJsonPrimitive().getAsString();
		Long asLong = Long.valueOf(id.split("/")[2]);
		return IdRef.create(r, getIdRefClazz(type), asLong);
	}

	private Class<?> getIdRefClazz(Type type) {
		return (Class<?>) ((ParameterizedType) type).getActualTypeArguments()[0];
	}

}
