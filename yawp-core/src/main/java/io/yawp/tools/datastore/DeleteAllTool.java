package io.yawp.tools.datastore;

import io.yawp.tools.Tool;

public class DeleteAllTool extends Tool {

    public static final String CONFIRM_PARAM = "confirm";

    @Override
    public void execute() {
        if (!confirm()) {
            pw.println("use: datastore/delete-all?confirm=" + System.currentTimeMillis());
            return;
        }

        deleteAll();
    }

    private void deleteAll() {
        yawp.driver().helpers().deleteAll();
        pw.println("ok");
    }

    private boolean confirm() {
        if (!yawp.driver().environment().isProduction()) {
            return true;
        }

        if (!params.containsKey(CONFIRM_PARAM)) {
            return false;
        }

        String confirmTime = params.get(CONFIRM_PARAM);
        Long confirm = Long.parseLong(confirmTime);
        long time = System.currentTimeMillis();
        if (confirm > time || confirm < time - 60000) {
            return false;
        }

        return true;
    }

}
