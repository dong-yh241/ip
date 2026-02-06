package cipher.ui;
import java.util.Scanner;

import cipher.CipherException;

public class Ui {
    public static final String LINE = "____________________________________________________________";

    private final String name;
    private final Scanner scanner;

    public Ui(String name) {
        this.name = name;
        this.scanner = new Scanner(System.in);
    }

    public void showWelcome() {
        System.out.println(LINE);
        System.out.println("Hello! I'm " + name);
        System.out.println("What can I do for you?");
        System.out.println(LINE);
    }

    public void showBye() {
        System.out.println("Bye. Hope to see you again soon!");
    }

    public void showLine() {
        System.out.println(LINE);
    }

    public String readCommand() throws CipherException {
        if (!scanner.hasNextLine()) {
            throw new CipherException("No input detected.");
        }
        return scanner.nextLine().trim();
    }

    public void showError(String message) {
        System.out.println("OOPS!!! " + message);
    }

    public void showMessage(String message) {
        System.out.println(message);
    }
}
