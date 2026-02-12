package cipher;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    private CipherGuiLogic logic;

    @Override
    public void start(Stage stage) {
        logic = new CipherGuiLogic("data/cipher.txt");

        MainWindow mainWindow = new MainWindow(logic);

        Scene scene = new Scene(mainWindow.getRoot(), 420, 640);
        stage.setTitle("Cipher");
        stage.setScene(scene);
        stage.show();

        // Show welcome text in the UI
        mainWindow.appendBot(logic.getWelcome());
    }
}
