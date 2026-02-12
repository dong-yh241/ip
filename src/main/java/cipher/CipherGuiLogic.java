package cipher;

import cipher.command.Command;
import cipher.command.CommandResult;
import cipher.parser.Parser;
import cipher.storage.Storage;
import cipher.task.TaskList;

public class CipherGuiLogic {

    private final Storage storage;
    private TaskList tasks;

    public CipherGuiLogic(String filePath) {
        storage = new Storage(filePath);
        try {
            tasks = new TaskList(storage.load());
        } catch (CipherException e) {
            tasks = new TaskList();
        }
    }

    public String getWelcome() {
        return "Hello! I'm Cipher\nWhat can I do for you?";
    }

    public String getResponse(String userInput) {
        try {
            Command c = Parser.parse(userInput);
            // Use a GUI-specific Ui adapter that returns strings instead of printing
            GuiUi ui = new GuiUi();
            CommandResult r = c.execute(tasks, ui, storage);

            // If user typed bye, still allow exit (GUI can close window)
            if (r.isExit()) {
                return ui.getOutput() + "\nBye. Hope to see you again soon!";
            }
            return ui.getOutput();
        } catch (CipherException e) {
            return e.getMessage();
        }
    }
}
