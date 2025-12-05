package com.isthereanyone.frontend.entities.tasks;

import com.badlogic.gdx.graphics.Color;

public class RitualTask extends BaseTask {

    public RitualTask(float x, float y) {
        super(x, y);
        this.debugColor = Color.PURPLE;
    }

    @Override
    protected void executeLogic() {
        System.out.println(">>> Meletakkan sesajen di altar... <<<");
        System.out.println(">>> Hantu berteriak di kejauhan! <<<");
    }
}
