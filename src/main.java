import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("main.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("ReStreamer");
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}
