package club.kittenpvp.lintcardboard.plugin.bridge;

public class BridgeException extends RuntimeException {
    private final String hook;

    public BridgeException(String hook, Throwable cause) {
        super("LintCardboard bridge forwarding failed for hook " + hook + ": " + cause, cause);
        this.hook = hook;
    }

    public String hook() {
        return hook;
    }
}
