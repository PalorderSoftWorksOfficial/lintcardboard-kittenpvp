package club.kittenpvp.lintcardboard.mixin;

public interface Transformation {
    void preboot();

    int priority();

    boolean shouldTransform(String className);

    byte[] transform(String className, byte[] input) throws Throwable;
}
