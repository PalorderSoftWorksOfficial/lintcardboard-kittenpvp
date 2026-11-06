package club.kittenpvp.lintcardboard.plugin.bridge;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BridgeHubTest {
    @Test
    void forwardsToEveryEndpointInRegistrationOrder() {
        BridgeHub hub = new BridgeHub();
        List<String> calls = new ArrayList<>();
        hub.register("player.join", context -> {
            calls.add("first");
            return "first";
        });
        hub.register("player.join", context -> {
            calls.add("second");
            return null;
        });
        hub.register("player.join", context -> {
            calls.add("third");
            return "third";
        });

        List<Object> forwarded = hub.dispatch("player.join", "context");

        assertEquals(List.of("first", "second", "third"), calls);
        assertEquals(List.of("first", "third"), forwarded);
    }

    @Test
    void unknownHookDispatchesToNothing() {
        BridgeHub hub = new BridgeHub();

        assertFalse(hub.hasHook("missing.hook"));
        assertEquals(List.of(), hub.dispatch("missing.hook", "context"));
    }

    @Test
    void closedRegistrationStopsForwarding() {
        BridgeHub hub = new BridgeHub();
        Registration registration = hub.register("server.tick", context -> "tick");
        registration.close();

        assertFalse(hub.hasHook("server.tick"));
        assertEquals(List.of(), hub.dispatch("server.tick", "context"));
    }

    @Test
    void endpointFailureIsLoudAndNamesTheHook() {
        BridgeHub hub = new BridgeHub();
        IllegalStateException cause = new IllegalStateException("mod broke");
        hub.register("server.tick", context -> {
            throw cause;
        });

        BridgeException failure = assertThrows(BridgeException.class, () -> hub.dispatch("server.tick", "context"));
        assertEquals("server.tick", failure.hook());
        assertEquals(cause, failure.getCause());
        assertTrue(hub.hasHook("server.tick"));
    }

    @Test
    void blankHookOrNullEndpointIsRejected() {
        BridgeHub hub = new BridgeHub();

        assertThrows(IllegalArgumentException.class, () -> hub.register(" ", context -> null));
        assertThrows(IllegalArgumentException.class, () -> hub.register("server.tick", null));
    }
}
