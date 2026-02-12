package cipher;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class MainWindow {

    private final CipherGuiLogic logic;

    private final VBox root = new VBox();
    private final TextArea dialog = new TextArea();
    private final TextField input = new TextField();
    private final Button send = new Button("Send");

    public MainWindow(CipherGuiLogic logic) {
        this.logic = logic;

        dialog.setEditable(false);
        dialog.setWrapText(true);

        input.setPromptText("Type a command, e.g., todo read book");

        send.setOnAction(e -> handleSend());
        input.setOnAction(e -> handleSend()); // Enter to send

        VBox.setVgrow(dialog, Priority.ALWAYS);
        root.setPadding(new Insets(10));
        root.setSpacing(10);
        root.getChildren().addAll(dialog, input, send);
    }

    public Parent getRoot() {
        return root;
    }

    public void appendBot(String msg) {
        if (!msg.isBlank()) {
            dialog.appendText("Cipher: " + msg + "\n\n");
        }
    }

    private void appendUser(String msg) {
        if (!msg.isBlank()) {
            dialog.appendText("You: " + msg + "\n");
        }
    }

    private void handleSend() {
        String userText = input.getText().trim();
        if (userText.isEmpty()) {
            return;
        }
        input.clear();

        appendUser(userText);
        String response = logic.getResponse(userText);
        appendBot(response);
    }
}
