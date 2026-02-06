package cipher;

import cipher.command.Command;
import cipher.command.CommandResult;
import cipher.parser.Parser;
import cipher.storage.Storage;
import cipher.task.TaskList;
import cipher.ui.Ui;

public class Cipher {
    private static final String NAME = "Cipher";
    private static final String FILE_PATH = "data/cipher.txt";

    private final Ui ui;
    private final Storage storage;
    private TaskList tasks;

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

    public static void main(String[] args) {
        new Cipher(FILE_PATH).run();
    }
}
