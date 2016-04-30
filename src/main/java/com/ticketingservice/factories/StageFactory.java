package com.ticketingservice.factories;

import com.ticketingservice.model.Level;
import com.ticketingservice.model.Stage;

/**
 * Created by lva833 on 4/29/16.
 */
public class StageFactory {

    public static Stage createDefaultStage() {
        Level level1 = new Level(1, "Orchestra", 100, 25, 50);
        Level level2 = new Level(2, "Main", 75, 20, 100);
        Level level3 = new Level(3, "Balcony1", 50, 15, 100);
        Level level4 = new Level(4, "Balcony2", 40, 15, 100);
        return createStage(level1, level2, level3, level4);
    }

    public static Stage createStage(Level... levels) {
        Stage stage = new Stage();
        for (Level level : levels) {
            stage.getLevels().add(level);
        }
        return stage;
    }

}
