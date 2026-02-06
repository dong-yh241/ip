package cipher.command;
public class CommandResult {
    private final boolean isExit;

    public CommandResult(boolean isExit) {
        this.isExit = isExit;
    }

    public boolean isExit() {
        return isExit;
    }

    public static CommandResult exit() {
        return new CommandResult(true);
    }

    public static CommandResult cont() {
        return new CommandResult(false);
    }
}
