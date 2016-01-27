package io.yawp.repository.pipes;

public class SinkTag<S> {
    private final String tag;
    private final S sink;

    public SinkTag(S sink, String tag) {
        this.tag = tag;
        this.sink = sink;
    }

    public S as(String s) {
        return sink;
    }
}
