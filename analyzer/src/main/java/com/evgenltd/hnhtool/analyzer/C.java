package com.evgenltd.hnhtool.analyzer;

import com.evgenltd.hnhtool.analyzer.common.Lifecycle;
import com.evgenltd.hnhtool.analyzer.service.GateImpl;
import com.evgenltd.hnhtool.analyzer.service.MainModel;
import com.evgenltd.hnhtool.analyzer.stand.Stand;
import com.evgenltd.hnhtool.analyzer.stand.StandHandler;
import com.evgenltd.hnhtools.common.ApplicationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.magenta.hnhtool.gate.Gate;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 24-02-2019 16:29</p>
 */
public class C implements Lifecycle {

    private static C instance = new C();

    private ObjectMapper mapper;
    private Registry registry;

    private GateImpl gate;
    private Stand stand;
    private StandHandler standHandler;

    private MainModel mainModel;


    public static C get() {
        return instance;
    }

    @Override
    public void init() {
        try {
            mapper = new ObjectMapper();
            registry = LocateRegistry.createRegistry(7777);

            gate = new GateImpl();
            gate.init();
            registry.bind(Gate.class.getSimpleName(), gate);

            stand = new Stand();
            standHandler = new StandHandler(stand);

            mainModel = new MainModel();
            mainModel.init();

            debugThread();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    private void debugThread() {
        new Thread(() -> {
            while (true) {
                System.out.println("start");

                System.out.println("debug");

                System.out.println("end");

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    return;
                }

            }
        }).start();
    }

    //

    public static GateImpl getGate() {
        return get().gate;
    }

    public static Stand getStand() {
        return get().stand;
    }

    public static StandHandler getStandHandler() {
        return get().standHandler;
    }

    public static MainModel getMainModel() {
        return get().mainModel;
    }

    public static ObjectMapper getMapper() {
        return get().mapper;
    }
}
