package io.github.TidyTracks;

import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class TrackHandlers {
  // Used by FileUtil
  static final String[] ACCEPTED_EXTENSIONS = {".gpx"};
  static final FileChooser.ExtensionFilter[] EXTENSION_FILTERS =
      {new FileChooser.ExtensionFilter("GPX Files (*.gpx)", "*.gpx")};

  private Controller controller;
  private Map<String, TrackHandler> trackHandlers = new HashMap<>();

  TrackHandlers(Controller controller) {
    this.controller = controller;
    initializeHandlers();
  }

  Map<String, TrackHandler> getTrackHandlers() {
    return trackHandlers;
  }

  private void initializeHandlers() {
    TrackHandler gpxHandler = createGPXHandler();
    trackHandlers.put(gpxHandler.getFileExtension(), gpxHandler);
  }

  private TrackHandler createGPXHandler() {
    String fileExtension = ".gpx";
    GpxOptionsPane gpxOptionsPane = new GpxOptionsPane(controller);
    GridPane optionsPane = gpxOptionsPane.getGpxOptionsPane();
    Set<String> parserOptions = gpxOptionsPane.getParserOptions();
    TrackParser trackParser = new GpxParser(controller, parserOptions);

    return new TrackHandler
        (fileExtension, trackParser, parserOptions, optionsPane);
  }
}
