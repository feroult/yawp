package io.yawp.repository.models.basic;

public enum Status {

    STOPPED {
        @Override
        public String getText() {
            return "stopped";
        }
    }, RUNNING {
        @Override
        public String getText() {
            return "running";
        }
    };

    public abstract String getText();
    
}
