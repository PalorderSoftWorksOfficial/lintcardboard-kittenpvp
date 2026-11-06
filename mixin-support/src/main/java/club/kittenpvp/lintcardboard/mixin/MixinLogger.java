package club.kittenpvp.lintcardboard.mixin;

import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.logging.Level;

import java.util.Arrays;

public class MixinLogger implements ILogger {
    private final String id;

    private MixinLogger(String id) {
        this.id = id;
    }

    public static MixinLogger get(String name) {
        return new MixinLogger(name);
    }

    private void write(Level level, String message, Object... params) {
        StringBuilder text = new StringBuilder("[lintcardboard-mixin][").append(level).append("][").append(id).append("] ").append(message);
        if (params != null && params.length > 0) {
            text.append(" ").append(Arrays.toString(params));
        }
        synchronized (System.err) {
            System.err.println(text);
        }
    }

    private void write(Level level, String message, Throwable cause) {
        write(level, message + " " + cause);
        synchronized (System.err) {
            cause.printStackTrace(System.err);
        }
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getType() {
        return "console";
    }

    @Override
    public <T extends Throwable> T throwing(T cause) {
        write(Level.ERROR, "Throwing", cause);
        return cause;
    }

    @Override
    public void catching(Level level, Throwable cause) {
        write(level, "Caught exception", cause);
    }

    @Override
    public void catching(Throwable cause) {
        write(Level.ERROR, "Caught exception", cause);
    }

    @Override
    public void debug(String message, Object... params) {
        write(Level.DEBUG, message, params);
    }

    @Override
    public void debug(String message, Throwable cause) {
        write(Level.DEBUG, message, cause);
    }

    @Override
    public void error(String message, Object... params) {
        write(Level.ERROR, message, params);
    }

    @Override
    public void error(String message, Throwable cause) {
        write(Level.ERROR, message, cause);
    }

    @Override
    public void fatal(String message, Object... params) {
        write(Level.FATAL, message, params);
    }

    @Override
    public void fatal(String message, Throwable cause) {
        write(Level.FATAL, message, cause);
    }

    @Override
    public void info(String message, Object... params) {
        write(Level.INFO, message, params);
    }

    @Override
    public void info(String message, Throwable cause) {
        write(Level.INFO, message, cause);
    }

    @Override
    public void log(Level level, String message, Object... params) {
        write(level, message, params);
    }

    @Override
    public void log(Level level, String message, Throwable cause) {
        write(level, message, cause);
    }

    @Override
    public void trace(String message, Object... params) {
        write(Level.TRACE, message, params);
    }

    @Override
    public void trace(String message, Throwable cause) {
        write(Level.TRACE, message, cause);
    }

    @Override
    public void warn(String message, Object... params) {
        write(Level.WARN, message, params);
    }

    @Override
    public void warn(String message, Throwable cause) {
        write(Level.WARN, message, cause);
    }
}
