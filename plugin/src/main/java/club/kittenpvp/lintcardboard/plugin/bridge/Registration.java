package club.kittenpvp.lintcardboard.plugin.bridge;

@FunctionalInterface
public interface Registration extends AutoCloseable {
    @Override
    void close();
}
