package io.yawp.commons.utils.json1;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public abstract class CustomTypeAdapterFactory<T> implements TypeAdapterFactory {

    private final Class<T> clazz;

    public CustomTypeAdapterFactory(Class<T> clazz) {
        this.clazz = clazz;
    }

    @SuppressWarnings("unchecked") // we use a runtime check to guarantee that 'T' and 'TT' are equal
    public final <TT> TypeAdapter<TT> create(Gson gson, TypeToken<TT> type) {
        return type.getRawType() == clazz
                ? (TypeAdapter<TT>) createAdapter(gson, (TypeToken<T>) type)
                : null;
    }

    private TypeAdapter<T> createAdapter(Gson gson, TypeToken<T> _type) {
        final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, _type);
        final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
        return new TypeAdapter<T>() {
            @Override
            public void write(JsonWriter out, T value) throws IOException {
                CustomTypeAdapterFactory.this.write(out, value, elementAdapter, delegate);
            }

            @Override
            public T read(JsonReader in) throws IOException {
                return CustomTypeAdapterFactory.this.read(in, elementAdapter, delegate);
            }
        };
    }

    /**
     * Override this to define how this is serialized in {@code toSerialize} to
     * the outgoing JSON stream.
     */
    protected void write(JsonWriter out, T value, TypeAdapter<JsonElement> elementAdapter, TypeAdapter<T> delegate) throws IOException {
        JsonElement tree = delegate.toJsonTree(value);
        beforeWrite(value, tree);
        elementAdapter.write(out, tree);
    }

    /**
     * Override this to define how this is deserialized in {@code deserialize} to
     * its type.
     */
    protected T read(JsonReader in, TypeAdapter<JsonElement> elementAdapter, TypeAdapter<T> delegate) throws IOException {
        JsonElement tree = elementAdapter.read(in);
        afterRead(tree);
        return delegate.fromJsonTree(tree);
    }

    /**
     * Override this to muck with {@code toSerialize} before it is written to
     * the outgoing JSON stream.
     */
    protected void beforeWrite(T source, JsonElement toSerialize) {
    }

    /**
     * Override this to muck with {@code deserialized} before it parsed into
     * the application type.
     */
    protected void afterRead(JsonElement deserialized) {
    }
}