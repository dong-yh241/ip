package cipher.command;

/**
 * Represents the outcome of executing a {@link Command}.
 * <p>
 * Currently, the only state tracked is whether the application should exit.
 */
public class CommandResult {
    private final boolean isExit;

    /**
     * Creates a command result.
     *
     * @param isExit Whether the application should exit after executing the command
     */
    public CommandResult(boolean isExit) {
        this.isExit = isExit;
    }

    /**
     * Returns whether the application should exit after executing the command.
     *
     * @return {@code true} if the app should exit; otherwise {@code false}
     */
    public boolean isExit() {
        return isExit;
    }

    /**
     * Convenience factory method for an "exit" result.
     *
     * @return A {@code CommandResult} indicating the app should exit
     */
    public static CommandResult exit() {
        return new CommandResult(true);
    }

    /**
     * Convenience factory method for a "continue" result.
     *
     * @return A {@code CommandResult} indicating the app should continue
     */
    public static CommandResult cont() {
        return new CommandResult(false);
    }
}
