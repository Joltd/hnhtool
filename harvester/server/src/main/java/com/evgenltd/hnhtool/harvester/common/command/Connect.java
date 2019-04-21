package com.evgenltd.hnhtool.harvester.common.command;

import com.evgenltd.hnhtools.common.Assert;
import com.evgenltd.hnhtools.common.Result;
import com.evgenltd.hnhtools.complexclient.ComplexClient;
import org.jetbrains.annotations.NotNull;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 30-03-2019 18:11</p>
 */
public class Connect {

    private ComplexClient client;

    private Connect(final ComplexClient client) {
        this.client = client;
    }

    public static Result<Void> perform(@NotNull final ComplexClient client) {
        Assert.valueRequireNonEmpty(client, "Client");
        return new Connect(client).performImpl();
    }

    private Result<Void> performImpl() {
        client.connect();
        return CommandUtils.awaitWithResult(this::isAwaitDone);
    }

    private Result<Boolean> isAwaitDone() {
        if (client.isClosed()) {
            return Result.fail(client.getConnectionErrorCode());
        }

        return Result.ok(client.isLife());
    }

}
