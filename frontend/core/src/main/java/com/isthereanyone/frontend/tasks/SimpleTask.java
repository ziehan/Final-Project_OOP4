package com.isthereanyone.frontend.tasks;

public class SimpleTask extends BaseTask {
    public SimpleTask(float x, float y) {
        super(x, y);
    }

    @Override
    protected void performLogic() {
        System.out.println("Connecting wires... (Proses Task)");
    }
}
