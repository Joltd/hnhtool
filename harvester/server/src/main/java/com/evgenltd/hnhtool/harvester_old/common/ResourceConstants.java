package com.evgenltd.hnhtool.harvester_old.common;

import com.evgenltd.hnhtool.harvester_old.common.entity.ServerResultCode;
import com.evgenltd.hnhtools.common.ApplicationException;
import com.evgenltd.hnhtools.common.Assert;
import com.evgenltd.hnhtools.common.Resources;
import com.evgenltd.hnhtools.common.Result;
import com.evgenltd.hnhtools.entity.IntPoint;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.IntNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 04-04-2019 01:54</p>
 */
public class ResourceConstants {

    private static final Logger log = LogManager.getLogger(ResourceConstants.class);

    public static final String TIMBER_HOUSE = "gfx/terobjs/arch/timberhouse";

    private static final Map<String,Resource> resources = new HashMap<>();

    static {
        readDescriptor();
    }

    public static boolean isWaste(final String resourceName) {
        return getBoolean(resourceName, Resource::getWaste);
    }

    public static boolean isPlayer(final String resourceName) {
        return Assert.isEmpty(resourceName);
    }

    public static boolean isDoorway(final String resourceName) {
        return getBoolean(resourceName, Resource::getDoorway);
    }

    public static boolean isDroppedItem(final String resourceName) {
        return getBoolean(resourceName, Resource::getDroppedItem);
    }

    public static boolean isContainer(final String resourceName) {
        return getBoolean(resourceName, Resource::getContainer);
    }

    public static boolean isStack(final String resourceName) {
        return getBoolean(resourceName, Resource::getStack);
    }

    public static boolean isDoorwayToBuilding(final String resourceName) {
        return false;
    }

    public static boolean isDoorwayToHole(final String resourceName) {
        return false;
    }

    public static boolean isDoorwayToMine(final String resourceName) {
        return false;
    }

    @NotNull
    public static Result<String> getMatch(final String resourceName) {
        final String match = getProperty(resourceName, Resource::getMatch);
        return !Assert.isEmpty(match)
                ? Result.ok(match)
                : Result.fail(ServerResultCode.RESOURCE_NOT_KNOWN);
    }

    @Nullable
    public static String getMatchedResource(final String resourceName) {
        return getProperty(resourceName, Resource::getMatch);
    }

    @NotNull
    public static Result<IntPoint> getSize(final String resourceName) {
        final IntPoint size = getProperty(resourceName, Resource::getSize);
        if (size != null) {
            return Result.ok(size);
        } else {
            log.info("Resource [{}] not known", resourceName);
            return Result.fail(ServerResultCode.RESOURCE_NOT_KNOWN);
        }
    }

    private static boolean getBoolean(final String resourceName, final Function<Resource, Boolean> getter) {
        return Optional.ofNullable(resources.get(resourceName))
                .map(getter)
                .orElse(false);
    }

    private static <T> T getProperty(final String resourceName, final Function<Resource, T> getter) {
        return Optional.ofNullable(resources.get(resourceName))
                .map(getter)
                .orElse(null);
    }

    //

    private static void readDescriptor() {
        try {
            final SimpleModule module = new SimpleModule();
            module.addDeserializer(IntPoint.class, pointDeserializer());
            final ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(module);

            final String resourcesContent = Resources.load(ResourceConstants.class, "resources.json");
            final List<Resource> resourceList = objectMapper.readValue(resourcesContent, new TypeReference<List<Resource>>() {});
            for (final Resource resource : resourceList) {
                resources.put(resource.getName(), resource);
            }
        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }

    @NotNull
    private static JsonDeserializer<IntPoint> pointDeserializer() {
        return new JsonDeserializer<IntPoint>() {
            @Override
            public IntPoint deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) throws IOException {
                final TreeNode node = jsonParser.getCodec().readTree(jsonParser);
                return new IntPoint(
                        ((IntNode) node.get("x")).intValue(),
                        ((IntNode) node.get("y")).intValue()
                );
            }
        };
    }

    private static final class Resource {
        private String name;
        private Boolean waste;
        private Boolean doorway;
        private Boolean droppedItem;
        private Boolean container;
        private Boolean stack;
        private IntPoint size;
        private String match;

        public String getName() {
            return name;
        }
        public void setName(final String name) {
            this.name = name;
        }

        public Boolean getWaste() {
            return waste != null && waste;
        }
        public void setWaste(final Boolean waste) {
            this.waste = waste;
        }

        public Boolean getDoorway() {
            return doorway != null && doorway;
        }
        public void setDoorway(final Boolean doorway) {
            this.doorway = doorway;
        }

        public Boolean getDroppedItem() {
            return droppedItem;
        }
        public void setDroppedItem(final Boolean droppedItem) {
            this.droppedItem = droppedItem;
        }

        public Boolean getContainer() {
            return container != null && container;
        }
        public void setContainer(final Boolean container) {
            this.container = container;
        }

        public Boolean getStack() {
            return stack != null && stack;
        }
        public void setStack(final Boolean stack) {
            this.stack = stack;
        }

        public IntPoint getSize() {
            return size;
        }
        public void setSize(final IntPoint size) {
            this.size = size;
        }

        public String getMatch() {
            return match;
        }
        public void setMatch(final String match) {
            this.match = match;
        }
    }

}
