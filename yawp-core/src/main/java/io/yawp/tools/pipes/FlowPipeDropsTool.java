package io.yawp.tools.pipes;

import io.yawp.tools.Tool;

public class FlowPipeDropsTool extends Tool {

    @Override
    public void execute() {
        drops();
        pw.println("ok");
    }

    public void drops() {
        yawp.driver().pipes().flowDrops();
    }


}
