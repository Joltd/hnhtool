package com.evgenltd.hnhtool.analyzer;

import com.evgenltd.hnhtool.analyzer.ui.MainScreen;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 24-02-2019 16:28</p>
 */
public class Analyzer extends Application {

    @Override
    public void init() {
        C.get().init();
    }

    @Override
    public void stop() {
        C.get().stop();
    }

    @Override
    public void start(final Stage primaryStage) {
        primaryStage.setTitle("HnH communication analyzer");
        primaryStage.setScene(new Scene(C.load(MainScreen.class), 800, 600));
        primaryStage.show();
    }

}
