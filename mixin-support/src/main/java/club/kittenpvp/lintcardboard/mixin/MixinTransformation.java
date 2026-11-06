package club.kittenpvp.lintcardboard.mixin;

import org.spongepowered.asm.mixin.transformer.IMixinTransformer;
import org.spongepowered.asm.mixin.transformer.IMixinTransformerFactory;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MixinTransformation implements Transformation {
    private IMixinTransformerFactory factory;
    private IMixinTransformer transformer;
    private final Set<String> transformedClasses = ConcurrentHashMap.newKeySet();

    public void offer(IMixinTransformerFactory factory) {
        this.factory = factory;
    }

    @Override
    public void preboot() {
        if (factory == null) {
            throw new IllegalStateException("Mixin transformer factory was not offered by the engine");
        }
        transformer = factory.createTransformer();
    }

    @Override
    public int priority() {
        return 0;
    }

    @Override
    public boolean shouldTransform(String className) {
        return transformer != null
            && !className.startsWith("org.spongepowered.asm.")
            && !className.startsWith("club.kittenpvp.lintcardboard.mixin.");
    }

    @Override
    public byte[] transform(String className, byte[] input) {
        byte[] output = transformer.transformClassBytes(className, className, input);
        if (output == null || output == input || Arrays.equals(input, output)) {
            return null;
        }
        transformedClasses.add(className);
        MixinHost.noteTransformed(className);
        return output;
    }

    public Set<String> transformedClasses() {
        return transformedClasses;
    }
}
