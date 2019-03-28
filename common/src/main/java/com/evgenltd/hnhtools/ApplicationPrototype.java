package com.evgenltd.hnhtools;

import com.evgenltd.hnhtools.baseclient.BaseClient;
import com.evgenltd.hnhtools.common.ApplicationException;
import com.evgenltd.hnhtools.entity.IntPoint;
import com.evgenltd.hnhtools.environment.Environment;
import com.evgenltd.hnhtools.message.InboundMessageAccessor;
import com.evgenltd.hnhtools.message.RelType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hnh.auth.Authentication;
import com.hnh.auth.AuthenticationResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 13-03-2019 00:34</p>
 */
public class ApplicationPrototype {

    private static ObjectMapper mapper = new ObjectMapper();
    private static BaseClient baseClient;
    private static Environment environment;

    private static Map<Integer, Widget> relIndex = new ConcurrentHashMap<>();
    private static Map<Long,List<InboundMessageAccessor.ObjectDataAccessor>> objectDataIndex = new ConcurrentHashMap<>();
    private static Map<Integer, String> resourceNameIndex = new ConcurrentHashMap<>();

    public static void main(String[] args) {

        baseClient = new BaseClient(mapper);
        baseClient.withMonitor();
        baseClient.setServer("game.havenandhearth.com", 1870);
        baseClient.setCredentials("Grafbredbery", auth());
//        baseClient.setCredentials("temedrou", auth());
        environment = new Environment(mapper);
        environment.linkBaseClient(baseClient);
//        baseClient.setRelQueue(ApplicationPrototype::handleRel);
//        baseClient.setObjectDataQueue(objectDataAccessor -> {
//            objectDataIndex.putIfAbsent(objectDataAccessor.getId(), new ArrayList<>());
//            objectDataIndex.get(objectDataAccessor.getId()).add(objectDataAccessor);
//        });

        Scanner scanner = new Scanner(System.in);
        while (true) {
            final String command = scanner.nextLine();
            switch (command) {
                case "c":
                    baseClient.connect();
                    break;
                case "d":
                    baseClient.disconnect();
                    environment.store();
//                    storeObjectIndex();
//                    storeResourceNameIndex();
                    return;
                case "i":
                    baseClient.printHealth();
                    break;
                case "debug":
                    System.out.println("debug");
                    break;
                case "widgets":
                    System.out.println(relIndex.values()
                            .stream()
                            .map(Widget::toString)
                            .collect(Collectors.joining("\n")));
                    break;
                case "play":
                    baseClient.pushOutboundRel(3, "play", "Surname");
                    break;
                case "p_tem":
                    baseClient.pushOutboundRel(3, "play", "temedrou");
                    break;
                case "move":
                    final Integer mapViewId = environment.getMapViewId();
                    final IntPoint newPosition = environment.getPlayerPosition().add(5000,5000);
                    baseClient.pushOutboundRel(mapViewId, "click", new IntPoint(), newPosition, 1, 0);
                    break;
                default:
                    handleWidgetCommand(command);
            }
        }

    }

    private static byte[] auth() {

        try (final Authentication auth = Authentication.of()) {
            auth.setHost("game.havenandhearth.com");
            auth.init();
            final AuthenticationResult result = auth.login(
                    "Grafbredbery",
                    Authentication.passwordHash("15051953")
//                    "temedrou",
//                    Authentication.passwordHash("nRJWfd2v")
            );
            return result.getCookie();
        } catch (Exception e) {
            throw new ApplicationException(e);
        }

    }

    private static void handleRel(final InboundMessageAccessor.RelAccessor relAccessor) {
        final RelType relType = relAccessor.getRelType();
        if (relType == null) {
            return;
        }
        switch (relType) {
            case REL_MESSAGE_NEW_WIDGET:
                final Widget newWidget = new Widget(relAccessor.getWidgetId(), relAccessor.getWidgetType(), relAccessor);
                relIndex.put(newWidget.getId(), newWidget);
                break;
            case REL_MESSAGE_WIDGET_MESSAGE:
            case REL_MESSAGE_DESTROY_WIDGET:
            case REL_MESSAGE_ADD_WIDGET:
                final int widgetId = relAccessor.getWidgetId();
                final Widget widget = relIndex.get(widgetId);
                if (widget != null) {
                    widget.getMessages().add(relAccessor);
                }
                break;
            case REL_MESSAGE_RESOURCE_ID:
                resourceNameIndex.put(relAccessor.getResourceId(), relAccessor.getResourceName());
                break;
        }
    }

    private static void handleWidgetCommand(final String command) {
        try {
            final String[] parts = command.split(" ");
            final String[] args = new String[parts.length - 2];
            System.arraycopy(parts, 2, args, 0, parts.length - 2);
            baseClient.pushOutboundRel(Integer.parseInt(parts[0]), parts[1], (Object[]) args);
        } catch (Exception e) {
            System.out.println("Unable to send widget command or unknown [" + command + "]");
            e.printStackTrace();
        }
    }

    private static void storeObjectIndex() {
        final ArrayNode rootNode = mapper.createArrayNode();
        objectDataIndex.forEach((id,deltas) -> {
            final ObjectNode objectNode = rootNode.addObject();
            objectNode.put("id", id);
            final ArrayNode deltasNode = objectNode.putArray("deltas");
            deltas.forEach(delta -> deltasNode.add(delta.getData()));
        });
        try (final FileOutputStream stream = new FileOutputStream("objectIndex.json")) {
            stream.write(rootNode.toString().getBytes());
        } catch (final IOException e) {
            throw new ApplicationException(e);
        }
    }

    private static void storeResourceNameIndex() {
        try {
            mapper.writeValue(new File("resourceNameIndex.json"), resourceNameIndex);
        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }

    private static final class Widget {

        private int id;
        private String type;
        private InboundMessageAccessor.RelAccessor descriptor;
        private List<InboundMessageAccessor.RelAccessor> messages = new ArrayList<>();

        Widget(final int id, final String type, final InboundMessageAccessor.RelAccessor descriptor) {
            this.id = id;
            this.type = type;
            this.descriptor = descriptor;
        }

        public int getId() {
            return id;
        }

        public String getType() {
            return type;
        }

        public InboundMessageAccessor.RelAccessor getDescriptor() {
            return descriptor;
        }

        public List<InboundMessageAccessor.RelAccessor> getMessages() {
            return messages;
        }

        @Override
        public String toString() {
            return String.format("id=[%s], type=[%s], messages=[%s]", id, type, messages.size());
        }
    }

}
