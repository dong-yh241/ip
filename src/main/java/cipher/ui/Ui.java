package cipher.ui;

import java.util.Scanner;

/**
 * Handles all user interaction for the Cipher application (text UI).
 * Provides helper methods to print messages and read user input.
 */
public class Ui {
    private static final String LINE = "____________________________________________________________";

    private final String appName;
    private final Scanner scanner;

    /**
     * Creates a UI instance with the given application name.
     *
     * @param appName Name of the chatbot shown in the welcome message.
     */
    public Ui(String appName) {
        this.appName = appName == null ? "Cipher" : appName;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Prints a separator line.
     */
    public void showLine() {
        System.out.println(LINE);
    }

    /**
     * Prints one or more lines. (Varargs version)
     *
     * @param lines Lines to print in order.
     */
    public void showLines(String... lines) {
        if (lines == null) {
            return;
        }
        for (String line : lines) {
            System.out.println(line == null ? "" : line);
        }
    }

    /**
     * Prints a message (single String). Kept for backward compatibility.
     *
     * @param message Message to print.
     */
    public void showMessage(String message) {
        showLines(message);
    }

    /**
     * Prints an error message.
     *
     * @param message Error message to print.
     */
    public void showError(String message) {
        showLines("OOPS!!! " + (message == null ? "" : message));
    }

    /**
     * Shows the welcome banner.
     */
    public void showWelcome() {
        showLine();
        showLines("Hello! I'm " + appName, "What can I do for you?");
        showLine();
    }

    /**
     * Shows the bye message.
     */
    public void showBye() {
        showLines("Bye. Hope to see you again soon!");
    }

    /**
     * Reads one full line from user input.
     *
     * @return The raw command line entered by the user.
     */
    public String readCommand() {
        return scanner.hasNextLine() ? scanner.nextLine() : "";
    }
}
