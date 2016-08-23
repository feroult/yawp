package io.yawp.commons.utils.json.genson;

import com.owlike.genson.*;
import com.owlike.genson.stream.JsonType;
import com.owlike.genson.stream.ObjectReader;
import com.owlike.genson.stream.ObjectWriter;
import io.yawp.repository.LazyJson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static com.owlike.genson.stream.ValueType.*;

public class LazyJsonConverters {

    public static final LazyJsonConverterFactory lazyJsonfConverterFactory = new LazyJsonConverterFactory();

    public static void configure(GensonBuilder builder) {
        builder.withConverterFactory(lazyJsonfConverterFactory);
    }

    public static Converter get(String name, Type parametrizedType) {
        ParameterizedType rootType = (ParameterizedType) parametrizedType;
        Type type = rootType.getActualTypeArguments()[0];
        return new LazyJsonConverter(name, type);
    }

    public static class LazyJsonConverter implements Converter<LazyJson> {

        private String name;

        private Type type;

        private LazyJsonConverter() {
        }

        public LazyJsonConverter(String name, Type type) {
            this.name = name;
            this.type = type;
        }

        @Override
        public void serialize(LazyJson value, ObjectWriter writer, Context ctx) throws Exception {
            if (value == null || value.getJson() == null) {
                writer.writeNull();
                return;
            }

            begin(value, writer);
            write((RawJsonWriter) writer, value);
            end(value, writer);
        }

        private void write(RawJsonWriter writer, LazyJson value) {
            String json = value.getJson();
            json = json.substring(1, json.length() - 1);
            RawJsonWriter customWriter = writer;
            customWriter.writeRawString(json);
        }

        private void end(LazyJson value, ObjectWriter writer) {
            if (value.isJsonArray()) {
                writer.endArray();
            } else {
                writer.endObject();
            }
        }

        private void begin(LazyJson value, ObjectWriter writer) {
            if (value.isJsonArray()) {
                writer.beginArray();
            } else {
                writer.beginObject();
            }
        }

        @Override
        public LazyJson deserialize(ObjectReader reader, Context ctx) throws Exception {
            String json = getObjectJson(reader, ctx);
            return LazyJson.$create(type, json);
        }

        private String getObjectJson(ObjectReader reader, Context ctx) {

            StringBuilder sb = new StringBuilder();
            int balance = 0;

            do {
                if (ARRAY == reader.getValueType()) {
                    reader.beginArray();
                    sb.append("[");
                    balance++;
                } else if (OBJECT == reader.getValueType()) {
                    reader.beginObject();
                    sb.append("{");
                    balance++;
                }

                if (reader.hasNext()) {
                    reader.next();

                    if (OBJECT == reader.getValueType()) {
                        if (reader.enclosingType() == JsonType.OBJECT) {
                            sb.append("\"" + reader.name() + "\":");
                        }
                        continue;
                    }

                    boolean first = true;
                    do {
                        if (!first) {
                            reader.next();
                            sb.append(",");
                        } else {
                            first = false;
                        }

                        sb.append("\"" + reader.name() + "\":");

                        if (reader.getValueType() == STRING) {
                            sb.append("\"" + reader.valueAsString() + "\"");
                        } else {
                            sb.append(reader.valueAsString());
                        }
                        reader.skipValue();
                    } while (reader.hasNext());
                }

                JsonType type = reader.enclosingType();
                if (JsonType.ARRAY == type) {
                    reader.endArray();
                    sb.append("]");
                    balance--;

                    if (balance != 0 && reader.hasNext()) {
                        sb.append(",");
                        reader.next();
                    }
                } else if (JsonType.OBJECT == type) {
                    reader.endObject();
                    sb.append("}");
                    balance--;

                    if (balance != 0 && reader.hasNext()) {
                        sb.append(",");
                        reader.next();
                    }
                }

            } while (balance > 0);

            return sb.toString();
        }
    }

    public static class LazyJsonConverterFactory implements Factory<Converter<LazyJson>> {
        @Override
        public Converter<LazyJson> create(Type type, Genson genson) {
            return new LazyJsonConverter();
        }
    }

}
