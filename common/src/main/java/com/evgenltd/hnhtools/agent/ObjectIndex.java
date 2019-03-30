package com.evgenltd.hnhtools.agent;

import com.evgenltd.hnhtools.common.Result;
import com.evgenltd.hnhtools.entity.IntPoint;
import com.evgenltd.hnhtools.entity.ResultCode;
import com.evgenltd.hnhtools.message.InboundMessageAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 30-03-2019 11:59</p>
 */
final class ObjectIndex {

    private final Map<Long, WorldObject> index = new HashMap<>();
    private WorldObject character;
    private Supplier<Long> getCharacterObjectId = () -> -1L;

    synchronized void setGetCharacterObjectId(final Supplier<Long> getCharacterObjectId) {
        this.getCharacterObjectId = getCharacterObjectId;
    }

    @NotNull
    public synchronized Result<WorldObject> getCharacter() {
        if (character == null) {
            character = index.get(getCharacterObjectId.get());
        }

        return character != null
                ? Result.of(character)
                : Result.fail(ResultCode.NO_CHARACTER);
    }

    @Nullable
    public synchronized WorldObject getWorldObject(final Long id) {
        return index.get(id);
    }

    synchronized void registerObjectData(final InboundMessageAccessor.ObjectDataAccessor accessor) {
        final Long objectId = accessor.getId();
        final WorldObject object = index.computeIfAbsent(objectId, WorldObject::new);

        final int frame = accessor.getFrame();
        if (object.getFrame() >= frame) {
            return;
        }

        object.setFrame(frame);

        for (final InboundMessageAccessor.ObjectDataDeltaAccessor delta : accessor.getDeltas()) {
            if (delta.getType() == null) {
                continue;
            }
            switch (delta.getType()) {
                case OD_REM:
                    index.remove(objectId);
                    break;
                case OD_MOVE:
                    object.setPosition(new IntPoint(delta.getX(), delta.getY()));
                    break;
                case OD_LINBEG:
                    object.setMoving(true);
                    break;
                case OD_LINSTEP:
                    object.setMoving(delta.getW() != -1);
                    break;
                case OD_RES:
                    object.setResourceId(delta.getResourceId());
                    break;
            }
        }
    }

}
