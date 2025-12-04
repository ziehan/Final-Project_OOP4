package com.isthereanyone.frontend.tasks;

public class WireTask extends BaseTask{
    public WireTask(float x, float y){
        super(x, y);
    }

    @Override
    protected void executeLogic() {
        System.out.println(">>> Memperbaiki kabel listrik... bzzzt... <<<");
        System.out.println(">>> Listrik menyala! <<<");
    }
}
