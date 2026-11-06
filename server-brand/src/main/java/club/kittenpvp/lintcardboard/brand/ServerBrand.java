package club.kittenpvp.lintcardboard.brand;

public final class ServerBrand {
    public static final String ID = "lintcardboard";
    public static final String NAME = "LintCardboard";
    public static final String VERSION = "0.1.0";

    private ServerBrand() {
    }

    public static String startupLine(String serverVersion) {
        return "This server is running " + NAME + " version " + serverVersion;
    }
}
