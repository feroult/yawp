package io.yawp.driver.appengine.pipes.utils;

import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Value;

import java.util.List;

public class WaiterJob extends Job1<Void, List<Void>> {
    @Override
    public Value<Void> run(List<Void> voids) throws Exception {
        return null;
    }
}
