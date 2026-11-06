package club.kittenpvp.lintcardboard.plugin.bridge;

@FunctionalInterface
public interface ModEndpoint<C, R> {
    R handle(C context);
}
