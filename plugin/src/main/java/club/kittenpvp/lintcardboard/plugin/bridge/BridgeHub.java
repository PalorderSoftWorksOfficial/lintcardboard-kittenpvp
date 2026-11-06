package club.kittenpvp.lintcardboard.plugin.bridge;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class BridgeHub {
    private final Map<String, List<Entry>> endpoints = new LinkedHashMap<>();

    public Registration register(String hook, ModEndpoint<Object, Object> endpoint) {
        if (hook == null || hook.isBlank()) {
            throw new IllegalArgumentException("Bridge hook name must not be blank");
        }
        if (endpoint == null) {
            throw new IllegalArgumentException("Bridge endpoint must not be null");
        }
        Entry entry = new Entry(endpoint);
        synchronized (endpoints) {
            endpoints.computeIfAbsent(hook, ignored -> new ArrayList<>()).add(entry);
        }
        return () -> {
            synchronized (endpoints) {
                List<Entry> registered = endpoints.get(hook);
                if (registered != null) {
                    registered.remove(entry);
                    if (registered.isEmpty()) {
                        endpoints.remove(hook);
                    }
                }
            }
        };
    }

    public boolean hasHook(String hook) {
        synchronized (endpoints) {
            List<Entry> registered = endpoints.get(hook);
            return registered != null && !registered.isEmpty();
        }
    }

    public List<Object> dispatch(String hook, Object context) {
        List<Entry> snapshot;
        synchronized (endpoints) {
            List<Entry> registered = endpoints.get(hook);
            snapshot = registered == null ? List.of() : List.copyOf(registered);
        }
        List<Object> forwarded = new ArrayList<>(snapshot.size());
        for (Entry entry : snapshot) {
            Object result;
            try {
                result = entry.endpoint.handle(context);
            } catch (RuntimeException failure) {
                throw new BridgeException(hook, failure);
            } catch (Exception failure) {
                throw new BridgeException(hook, failure);
            }
            if (result != null) {
                forwarded.add(result);
            }
        }
        return List.copyOf(forwarded);
    }

    private record Entry(ModEndpoint<Object, Object> endpoint) {
    }
}
