package io.github.TidyTracks;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

  final static String VERSION = "1.0";

  private Stage primaryStage;

  @Override
  public void start(Stage primaryStage) {
    this.primaryStage = primaryStage;
    FXMLLoader loader = new FXMLLoader();
    loader.setLocation(Main.class.getResource("Main.fxml"));
    Parent root;
    try {
      root = loader.load();
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }
    Controller controller = loader.getController();
    controller.setMain(this);
    primaryStage.setTitle("Tidy Tracks");
    primaryStage.setScene(new Scene(root));
    primaryStage.getIcons().add(new Image("/images/tidy.png"));
    primaryStage.show();
  }

  Stage getPrimaryStage() {
    return primaryStage;
  }

  public static void main(String[] args) {
    launch(args);
  }
}
