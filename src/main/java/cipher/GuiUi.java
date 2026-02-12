package cipher;

import cipher.ui.Ui;

/**
 * A minimal UI adapter for GUI mode.
 * Collects messages into a string instead of printing to console.
 */
public class GuiUi extends Ui {

    private final StringBuilder out = new StringBuilder();

    public GuiUi() {
        super("Cipher");
    }

    @Override
    public void showMessage(String message) {
        out.append(message);
    }

    @Override
    public void showError(String message) {
        out.append(message);
    }

    public String getOutput() {
        return out.toString().trim();
    }
}
