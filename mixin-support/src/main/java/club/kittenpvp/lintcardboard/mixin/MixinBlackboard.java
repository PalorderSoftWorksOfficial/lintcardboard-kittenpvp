package club.kittenpvp.lintcardboard.mixin;

import org.spongepowered.asm.service.IGlobalPropertyService;
import org.spongepowered.asm.service.IPropertyKey;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MixinBlackboard implements IGlobalPropertyService {
    private final Map<String, IPropertyKey> keys = new HashMap<>();
    private final PropertyStore store = new PropertyStore();

    @Override
    public IPropertyKey resolveKey(String name) {
        return keys.computeIfAbsent(name, key -> new Property<>(key, Object.class));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getProperty(IPropertyKey key) {
        return store.get((Property<T>) key, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setProperty(IPropertyKey key, Object value) {
        store.set((Property<Object>) key, value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getProperty(IPropertyKey key, T defaultValue) {
        return store.get((Property<T>) key, defaultValue);
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getPropertyString(IPropertyKey key, String defaultValue) {
        return store.get((Property<String>) key, defaultValue);
    }

    private record Property<T>(String name, Class<T> type) implements IPropertyKey {
    }

    private static final class PropertyStore {
        private final ConcurrentHashMap<Property<?>, Object> values = new ConcurrentHashMap<>();

        <T> void set(Property<T> property, T value) {
            values.put(property, value);
        }

        @SuppressWarnings("unchecked")
        <T> T get(Property<T> property, T fallback) {
            return (T) values.getOrDefault(property, fallback);
        }
    }
}
