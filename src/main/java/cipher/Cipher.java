package cipher;

import cipher.command.Command;
import cipher.command.CommandResult;
import cipher.parser.Parser;
import cipher.storage.Storage;
import cipher.task.TaskList;
import cipher.ui.Ui;

/**
 * Entry point and main coordinator for the Cipher task-tracking application.
 * <p>
 * Cipher wires together the UI, storage, and task list, then runs a command loop:
 * read user input → parse into a command → execute the command → repeat until exit.
 */
public class Cipher {
    private static final String NAME = "Cipher";
    private static final String FILE_PATH = "data/cipher.txt";

    private final Ui ui;
    private final Storage storage;
    private TaskList tasks;

    /**
     * Constructs a Cipher app using the given file path for persistent storage.
     * If loading fails, the app starts with an empty task list and shows an error message.
     *
     * @param filePath Path to the storage file (e.g. {@code data/cipher.txt})
     */
    public Cipher(String filePath) {
        this.ui = new Ui(NAME);
        this.storage = new Storage(filePath);
        try {
            this.tasks = new TaskList(storage.load());
        } catch (CipherException e) {
            ui.showError("Loading failed: " + e.getMessage());
            this.tasks = new TaskList();
        }
    }

    /**
     * Runs the main command-processing loop.
     * <p>
     * Each iteration reads a command from the user, parses it into a {@link Command},
     * executes it, and checks whether the app should exit.
     */
    public void run() {
        ui.showWelcome();

        boolean isExit = false;
        while (!isExit) {
            try {
                String fullCommand = ui.readCommand();
                ui.showLine();
                Command command = Parser.parse(fullCommand);
                CommandResult result = command.execute(tasks, ui, storage);
                isExit = result.isExit();
            } catch (CipherException e) {
                ui.showLine();
                ui.showError(e.getMessage());
            } finally {
                ui.showLine();
            }
        }

        ui.showBye();
    }

    /**
     * Launches the application using the default data file path.
     *
     * @param args Unused command-line arguments
     */
    public static void main(String[] args) {
        new Cipher(FILE_PATH).run();
    }
}
