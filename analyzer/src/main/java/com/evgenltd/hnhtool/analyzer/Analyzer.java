package com.evgenltd.hnhtool.analyzer;

import com.evgenltd.hnhtool.analyzer.ui.MainScreen;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
        primaryStage.setAlwaysOnTop(true);
        primaryStage.show();
    }

}
