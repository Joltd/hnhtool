package com.evgenltd.hnhtools;

import com.evgenltd.hnhtools.agent.ComplexClient;
import com.evgenltd.hnhtools.agent.ResourceProvider;
import com.evgenltd.hnhtools.command.Connect;
import com.evgenltd.hnhtools.command.Move;
import com.evgenltd.hnhtools.common.ApplicationException;
import com.evgenltd.hnhtools.entity.IntPoint;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hnh.auth.Authentication;
import com.hnh.auth.AuthenticationResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 13-03-2019 00:34</p>
 */
public class ApplicationPrototype {

    private static ObjectMapper mapper = new ObjectMapper();
    private static ResourceProvider resourceProvider = new ResourceProviderImpl();

    public static void main(String[] args) {

        final ComplexClient client = new ComplexClient(
                mapper,
                resourceProvider,
                "game.havenandhearth.com",
                1870,
                "Grafbredbery", auth(),
                "Surname"
        );

        Scanner scanner = new Scanner(System.in);
        while (true) {
            final String command = scanner.nextLine();
            switch (command) {
                case "c":
                    Connect.perform(client);
                    break;
                case "d":
                    client.disconnect();
                    return;
                case "i":
                    break;
                case "play":
                    client.play();
                    break;
                case "move":
                    final IntPoint value = client.getCharacterPosition().getValue();
                    Move.perform(client, value.add(-2000, -2000));
                    break;
                case "open":
//                    final Long cupboardId = client.getWorldObjects()
//                            .getValue()
//                            .entrySet()
//                            .stream()
//                            .filter(entry -> Objects.equals(entry.getValue(), "gfx/terobjs/cupboard"))
//                            .findFirst()
//                            .map(Map.Entry::getKey)
//                            .get();
//                    OpenInventory.perform(client, cupboardId).getValue().getItems().forEach(item -> System.out.println(item.getId()));
                    break;
//                case "print":
//                    client.printState();
//                    break;
            }
        }

    }

    private static byte[] auth() {

        try (final Authentication auth = Authentication.of()) {
            final byte[] token = Files.readAllBytes(Paths.get("D:\\token.dat"));
            auth.setHost("game.havenandhearth.com");
            auth.init();
            final AuthenticationResult result = auth.loginByToken(
                    "Grafbredbery",
                    token
            );
            return result.getCookie();
        } catch (Exception e) {
            throw new ApplicationException(e);
        }

    }

    private static final class ResourceProviderImpl implements ResourceProvider {

        private final Map<Integer,String> index = new ConcurrentHashMap<>();

        @Override
        @Nullable
        public String getResourceName(@NotNull final Integer id) {
            return index.get(id);
        }

        @Override
        public void saveResource(@NotNull final Integer id, @NotNull final String name) {
            index.put(id, name);
        }
    }

}
