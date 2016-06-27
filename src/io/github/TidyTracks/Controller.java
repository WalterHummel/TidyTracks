package io.github.TidyTracks;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.Optional;

public class Controller {

  private Main main;
  private Map<String, TrackHandler> trackHandlers;
  private boolean draggedOver;
  private boolean overwriteConformationShown;
  private File inputFile;
  private File outputFile;

  @FXML
  private TextField txtInput;
  @FXML
  private TextField txtOutput;
  @FXML
  private StackPane paneOptions;

  // called by the FXMLLoader
  @FXML
  private void initialize() {
    txtInput.textProperty().addListener
        (((observable, oldValue, newValue) ->
            showOptions(oldValue, newValue)));
    txtInput.focusedProperty().addListener((observable, oldValue, newValue)
        -> inputFocused(newValue));
    txtOutput.textProperty().addListener
        ((observable, oldValue, newValue) -> shownConfirmation());
    txtOutput.focusedProperty()
        .addListener(((observable, oldValue, newValue)
            -> outputFocused(newValue)));
    TrackHandlers handlers = new TrackHandlers(this);
    trackHandlers = handlers.getTrackHandlers();
  }

  private void showOptions(String oldFile, String newFile) {
    if (!FileUtil.getFileExtension(oldFile)
        .equals(FileUtil.getFileExtension(newFile))
        && FileUtil.isValidExtension(newFile)) {
      TrackHandler trackHandler =
          trackHandlers.get(FileUtil.getFileExtension(newFile));
      if (trackHandler == null) {
        throw new AssertionError("No trackHandler found for " +
            FileUtil.getFileExtension(newFile)
            + " file extension.");
      }
      paneOptions.getChildren().addAll(trackHandler.getOptionsPane());
    } else if (!FileUtil.isValidExtension(newFile)) {
      paneOptions.getChildren().clear();
    }
    setStageSize();
  }

  private void shownConfirmation() {
    // Used to only show overwrite conformation once
    overwriteConformationShown = false;
  }

  private void inputFocused(Boolean newValue) {
    // Stackoverflow Workaround: http://goo.gl/nDSD8K
    Platform.runLater(() -> {
      if (newValue && txtInput.isFocused()
          && !txtInput.getText().isEmpty()) {
        txtInput.selectAll();
      }
    });
  }

  private void outputFocused(Boolean newValue) {
    // Stackoverflow Workaround: http://goo.gl/nDSD8K
    Platform.runLater(() -> {
      if (newValue && txtOutput.isFocused()
          && !txtOutput.getText().isEmpty()) {
        txtOutput.selectAll();
      }
    });
  }

  @FXML
  private void openFileChooser() {
    FileChooser fileChooser = FileUtil.getFileChooser();
    fileChooser.setTitle("Open GPS Track");
    File input = new File(txtInput.getText());
    if (FileUtil.isValidExtension(input.getAbsolutePath())
        && input.exists()) {
      inputFile = input;
    }
    if (inputFile != null) {
      fileChooser.setInitialDirectory(inputFile.getParentFile());
    }
    input = fileChooser.showOpenDialog(main.getPrimaryStage());
    if (input != null) {
      txtInput.setText(input.getAbsolutePath());
      inputFile = input;
    }
  }

  @FXML
  private void saveFileChooser() {
    FileChooser fileChooser = FileUtil.getFileChooser(txtInput.getText());
    fileChooser.setTitle("Save As GPS Track");
    File output = new File(txtOutput.getText());
    if (FileUtil.isValidExtension(output.getAbsolutePath())
        && output.exists()) {
      outputFile = output;
    } else {
      outputFile = null;
    }
    if (outputFile != null) {
      fileChooser.setInitialDirectory(outputFile.getParentFile());
    } else if (inputFile != null) {
      fileChooser.setInitialDirectory(inputFile.getParentFile());
      String inputFileName = inputFile.getName();
      fileChooser.setInitialFileName(inputFileName
          .substring(0, inputFileName.lastIndexOf(".")) + "_tidy"
          + FileUtil.getFileExtension(inputFileName));
    }
    output = fileChooser.showSaveDialog(main.getPrimaryStage());
    if (output != null) {
      txtOutput.setText(output.getAbsolutePath());
      outputFile = output;
      overwriteConformationShown = true;
    }
  }

  @FXML
  private void inputDragDropped(DragEvent event) {
    Dragboard db = event.getDragboard();
    boolean success = false;
    String filePath = null;
    if (db.hasFiles()) {
      success = true;
      for (File file : db.getFiles()) {
        filePath = file.getAbsolutePath();
      }
    } else if (db.hasString()) {
      success = true;
      filePath = db.getString();
    }
    if (success) {
      inputFile = new File(filePath);
      txtInput.setText(filePath);
    }
    event.setDropCompleted(success);
    event.consume();
  }

  @FXML
  private void inputDragOver(DragEvent event) {
    Dragboard db = event.getDragboard();
    String filePath = null;
    if (db.hasFiles()) {
      for (File file : db.getFiles()) {
        filePath = file.getAbsolutePath();
      }
    } else if (db.hasString()) {
      filePath = db.getString();
    }
    if (FileUtil.isValidExtension(filePath)
        && new File(filePath).exists()) {
      if (!draggedOver) {
        draggedOver = true;
        txtInput.getStyleClass().add("validExtension");
      }
      event.acceptTransferModes(TransferMode.ANY);
    } else if (!draggedOver) {
      draggedOver = true;
      txtInput.getStyleClass().add("invalidExtension");
    }
    event.consume();
  }

  @FXML
  private void inputDragExited(DragEvent event) {
    draggedOver = false;
    txtInput.getStyleClass().remove("validExtension");
    txtInput.getStyleClass().remove("invalidExtension");
    event.consume();
  }

  @FXML
  private void outputDragDropped(DragEvent event) {
    Dragboard db = event.getDragboard();
    boolean success = false;
    String filePath = null;
    if (db.hasFiles()) {
      success = true;
      for (File file : db.getFiles()) {
        filePath = file.getAbsolutePath();
      }
    } else if (db.hasString()) {
      success = true;
      filePath = db.getString();
    }
    if (success) {
      outputFile = new File(filePath);
      txtOutput.setText(filePath);
    }
    event.setDropCompleted(success);
    event.consume();
  }

  @FXML
  private void outputDragOver(DragEvent event) {
    Dragboard db = event.getDragboard();
    String filePath = null;
    if (db.hasFiles()) {
      for (File file : db.getFiles()) {
        filePath = file.getAbsolutePath();
      }
    } else if (db.hasString()) {
      filePath = db.getString();
    }
    if (((FileUtil.isValidExtension(txtInput.getText())
        && new File(txtInput.getText()).exists()
        && FileUtil.getFileExtension(filePath)
        .equals(FileUtil.getFileExtension(txtInput.getText())))
        || ((!FileUtil.isValidExtension(txtInput.getText())
        || !new File(txtInput.getText()).exists())
        && FileUtil.isValidExtension(filePath)))) {
      if (!draggedOver) {
        draggedOver = true;
        txtOutput.getStyleClass().add("validExtension");
      }
      event.acceptTransferModes(TransferMode.ANY);
    } else if (!draggedOver) {
      draggedOver = true;
      txtOutput.getStyleClass().add("invalidExtension");
    }
    event.consume();
  }

  @FXML
  private void outputDragExited(DragEvent event) {
    draggedOver = false;
    txtOutput.getStyleClass().remove("validExtension");
    txtOutput.getStyleClass().remove("invalidExtension");
    event.consume();
  }

  @FXML
  private void tidyUp() {
    String inputFile = txtInput.getText();
    String outputFile = txtOutput.getText();
    if (inputFile.isEmpty()) {
      showDialog(Alert.AlertType.WARNING, "Input Track Required",
          "No Input Track", "Please select an input track file");
      return;
    }
    if (outputFile.isEmpty()) {
      showDialog(Alert.AlertType.WARNING, "Output Track Required",
          "No Output Track", "Please select an output track file");
      return;
    }
    if (new File(outputFile).exists() && !overwriteConformationShown) {
      boolean result = showConfirmationDialog("Overwrite file", null,
          "Are you sure you want to overwrite " + outputFile + "?");
      if (!result) {
        return;
      }
    }
    shownConfirmation();
    if (FileUtil.isValidExtension(inputFile)) {
      TrackHandler trackHandler =
          trackHandlers.get(FileUtil.getFileExtension(inputFile));
      if (trackHandler == null) {
        throw new AssertionError("No trackHandler found for " +
            FileUtil.getFileExtension(inputFile)
            + " file extension.");
      }
      TrackParser trackParser = trackHandler.getTrackParser();
      trackParser.parser(new File(inputFile), new File(outputFile));
    } else {
      String message = "\"" + FileUtil.getFileExtension(inputFile)
          + "\" is not supported by Tidy Tracks. "
          + "Please select a supported file.";
      showDialog(Alert.AlertType.INFORMATION, "File not supported",
          "File extension not supported", message);
    }
  }

  @FXML
  private void exit() {
    Platform.exit();
  }

  void showDialog(Alert.AlertType alertType, String title, String header,
                  String message) {
    Alert alert = new Alert(alertType);
    alert.setTitle(title);
    alert.setHeaderText(header);
    alert.setContentText(message);
    alert.initOwner(main.getPrimaryStage());
    alert.showAndWait();
  }

  boolean showConfirmationDialog(String title, String header,
                                 String message) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle(title);
    alert.setHeaderText(header);
    alert.setContentText(message);
    alert.initOwner(main.getPrimaryStage());
    alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
    Optional<ButtonType> result = alert.showAndWait();
    return result.isPresent() && result.get() == ButtonType.YES;
  }

  void showExceptionDialog(String title, String header, String message,
                           Exception e) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(header);
    alert.setContentText(message);
    alert.initOwner(main.getPrimaryStage());
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    e.printStackTrace(printWriter);
    String exceptionText = stringWriter.toString();
    Label label = new Label("The exception stacktrace was:");
    TextArea textArea = new TextArea(exceptionText);
    textArea.setEditable(false);
    textArea.setWrapText(true);
    textArea.setMaxWidth(Double.MAX_VALUE);
    textArea.setMaxHeight(Double.MAX_VALUE);
    GridPane.setVgrow(textArea, Priority.ALWAYS);
    GridPane.setHgrow(textArea, Priority.ALWAYS);
    GridPane expandableContent = new GridPane();
    expandableContent.setMaxWidth(Double.MAX_VALUE);
    expandableContent.add(label, 0, 0);
    expandableContent.add(textArea, 0, 1);
    alert.getDialogPane().setExpandableContent(expandableContent);
    alert.showAndWait();
  }

  void setStageSize() {
    Platform.runLater(() -> {
      main.getPrimaryStage().sizeToScene();
    });
  }

  void setMain(Main main) {
    this.main = main;
  }
}
