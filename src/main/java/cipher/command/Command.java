package cipher.command;

import cipher.CipherException;
import cipher.storage.Storage;
import cipher.task.TaskList;
import cipher.ui.Ui;

/**
 * Represents an executable user command in the Cipher application.
 * <p>
 * Concrete subclasses (e.g., {@code TodoCommand}, {@code DeadlineCommand}) implement {@link #execute(TaskList, Ui, Storage)}
 * to perform an action and return a {@link CommandResult} that indicates whether the app should exit.
 */
public abstract class Command {

    /**
     * Executes this command using the given task list, UI, and storage.
     *
     * @param tasks   Task list to read from and/or modify
     * @param ui      UI used to interact with the user (print messages, etc.)
     * @param storage Storage used to load/save tasks
     * @return Result containing whether the app should exit after this command
     * @throws CipherException If the command cannot be executed due to invalid input or I/O issues
     */
    public abstract CommandResult execute(TaskList tasks, Ui ui, Storage storage) throws CipherException;

    /**
     * Returns whether this command causes the application to exit.
     * <p>
     * Default is {@code false}. Most commands can simply return {@link CommandResult#cont()} instead.
     *
     * @return {@code true} if this command exits the app; otherwise {@code false}
     */
    public boolean isExit() {
        return false;
    }
}
