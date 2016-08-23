package io.yawp.commons.utils.json1;

import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.Writer;

public class CustomJsonWriter extends JsonWriter {

    private final Writer out;

    public CustomJsonWriter(Writer out) {
        super(out);
        this.out = out;
    }

    public void write(String s) throws IOException {
        out.write(s);
    }

}
