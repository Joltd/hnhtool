package com.evgenltd.hnhtool.analyzer.stand;

import com.evgenltd.hnhtool.analyzer.stand.entity.GameObject;
import com.evgenltd.hnhtool.analyzer.stand.entity.Resource;

import java.util.HashMap;
import java.util.Map;

public class Stand {

    private Map<Long, Resource> resourceIndex = new HashMap<>();
    private Map<Long, GameObject> gameObjectIndex = new HashMap<>();

    public Stand() {}

    public synchronized void addResource(final Long id, final String name, final Integer version) {
        resourceIndex.put(id, new Resource(id, name, version));
    }

    public synchronized GameObject getGameObject(final Long id, final Integer frame) {
        final GameObject gameObject = gameObjectIndex.get(id);
        if (gameObject == null) {
            final GameObject newGameObject = new GameObject(id, frame);
            gameObjectIndex.put(id, newGameObject);
            return newGameObject;
        } else if (gameObject.getFrame() < frame) {
            gameObject.setFrame(frame);
            return gameObject;
        } else {
            return null; // we have actual version
        }
    }

    public synchronized void move(final GameObject gameObject, final Long x, final Long y, final Double angel) {
        gameObject.setX(x);
        gameObject.setY(y);
        gameObject.setAngel(angel);
    }

    public synchronized void setResourceProxy(final GameObject gameObject, final Long resourceId) {
        gameObject.setResource(new Resource() {
            @Override
            public Long getId() {
                setupResource(gameObject, resourceId);
                return super.getId();
            }

            @Override
            public String getName() {
                setupResource(gameObject, resourceId);
                return super.getName();
            }

            @Override
            public int getVersion() {
                setupResource(gameObject, resourceId);
                return super.getVersion();
            }

            @Override
            public String toString() {
                setupResource(gameObject, resourceId);
                return super.toString();
            }
        });
    }

    private void setupResource(final GameObject gameObject, final Long resourceId) {
        final Resource resource = resourceIndex.get(resourceId);
        if (resource == null) {
            return;
        }

        gameObject.setResource(resource);
    }

}
