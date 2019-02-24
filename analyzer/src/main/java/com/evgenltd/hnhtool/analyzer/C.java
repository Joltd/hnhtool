package com.evgenltd.hnhtool.analyzer;

import com.evgenltd.hnhtool.analyzer.common.Lifecycle;
import com.evgenltd.hnhtool.analyzer.service.Gate;
import com.evgenltd.hnhtool.analyzer.service.MainModel;
import com.evgenltd.hnhtools.common.ApplicationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 24-02-2019 16:29</p>
 */
public class C implements Lifecycle {

    private static C instance = new C();

    private Gate gate;

    private MainModel mainModel;

    private ObjectMapper mapper;

    public static C get() {
        return instance;
    }

    @Override
    public void init() {
        mapper = new ObjectMapper();

        gate = new Gate();
        gate.init();

        mainModel = new MainModel();
        mainModel.init();
    }

    @Override
    public void stop() {
        gate.stop();
        mainModel.stop();
    }

    //

    public static Parent load(final Class<?> controllerClass) {
        try {
            return FXMLLoader.load(controllerClass.getResource(controllerClass.getSimpleName() + ".fxml"));
        } catch (final IOException e) {
            throw new ApplicationException(e);
        }
    }

    //

    public static Gate getGate() {
        return get().gate;
    }

    public static MainModel getMainModel() {
        return get().mainModel;
    }

    public static ObjectMapper getMapper() {
        return get().mapper;
    }
}
