package co.edu.uniquindio.panaderia;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 *  public void start(Stage stage) throws IOException {
 *         FXMLLoader loader = new FXMLLoader(App.class.getResource("view/main-view.fxml"));
 *         Scene scene = new Scene(loader.load(), 1000, 650);
 *         stage.setTitle("Panadería El Trigal - Gestión de Producción");
 *         stage.setScene(scene);
 *         stage.show();
 *     }
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("view/main-view.fxml"));
        Scene scene = new Scene(loader.load(), 1000, 650);
        stage.setTitle("Panadería El Trigal - Gestión de Producción");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}