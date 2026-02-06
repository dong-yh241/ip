package cipher.parser;
import cipher.CipherException;
import cipher.command.Command;
import cipher.command.DeadlineCommand;
import cipher.command.DeleteCommand;
import cipher.command.EventCommand;
import cipher.command.ExitCommand;
import cipher.command.FindCommand;
import cipher.command.ListCommand;
import cipher.command.MarkCommand;
import cipher.command.TodoCommand;
import cipher.command.UnmarkCommand;

public class Parser {

    public static Command parse(String fullCommand) throws CipherException {
        if (fullCommand == null) {
            throw new CipherException("Please type a command.");
        }
        String trimmed = fullCommand.trim();
        if (trimmed.isEmpty()) {
            throw new CipherException("Please type a command.");
        }

        String[] parts = trimmed.split("\\s+", 2);
        String keyword = parts[0];
        String args = parts.length == 2 ? parts[1].trim() : "";

        switch (keyword) {
        case "bye":
            return new ExitCommand();
        case "list":
            return new ListCommand();
        case "mark":
            return new MarkCommand(args);
        case "unmark":
            return new UnmarkCommand(args);
        case "delete":
            return new DeleteCommand(args);
        case "find":
            return new FindCommand(args);
        case "todo":
            return new TodoCommand(args);
        case "deadline":
            return new DeadlineCommand(args);
        case "event":
            return new EventCommand(args);
        default:
            throw new CipherException("I'm sorry, but I don't know what that means :-(");
        }
    }
}
