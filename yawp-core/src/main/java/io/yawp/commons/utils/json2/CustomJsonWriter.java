package io.yawp.commons.utils.json2;

import com.owlike.genson.stream.JsonWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class CustomJsonWriter extends JsonWriter {

    private Writer writer;

    public CustomJsonWriter(StringWriter writer, boolean skipNull, boolean htmlSafe, boolean indent) {
        super(writer, skipNull, htmlSafe, indent);
        this.writer = writer;
    }

    public JsonWriter writeRawString(String s) {
        flush();
        try {
            writer.write(s);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

}
