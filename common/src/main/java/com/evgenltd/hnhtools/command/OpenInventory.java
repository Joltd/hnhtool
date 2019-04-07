package com.evgenltd.hnhtools.command;

import com.evgenltd.hnhtools.agent.ComplexClient;
import com.evgenltd.hnhtools.common.Assert;
import com.evgenltd.hnhtools.common.Result;
import com.evgenltd.hnhtools.entity.Inventory;
import org.jetbrains.annotations.NotNull;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 30-03-2019 20:41</p>
 */
public class OpenInventory {

    private ComplexClient client;
    private Long objectId;

    private OpenInventory(final ComplexClient client, final Long objectId) {
        this.client = client;
        this.objectId = objectId;
    }

    public static Result<Inventory> perform(@NotNull final ComplexClient client, @NotNull final Long objectId) {
        Assert.valueRequireNonEmpty(client, "Client");
        Assert.valueRequireNonEmpty(objectId, "ObjectId");
        return new OpenInventory(client, objectId).performImpl();
    }

    private Result<Inventory> performImpl() {
        return client.interact(objectId)
                .then(() -> CommandUtils.awaitWithResult(this::isAwaitDone))
                .then(p -> client.getLastOpenedInventory());
    }

    private Result<Boolean> isAwaitDone() {
        return client.isCharacterMoving().map(moving -> !moving);
    }
}
