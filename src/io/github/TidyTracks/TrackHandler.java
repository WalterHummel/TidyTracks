package io.github.TidyTracks;

import javafx.scene.layout.GridPane;

import java.util.Set;

class TrackHandler {
    private String fileExtension;
    private TrackParser trackParser;
    private Set<String> parserOptions;
    private GridPane optionsPane;

    TrackHandler(String fileExtension, TrackParser trackParser,
                        Set<String> parserOptions, GridPane optionsPane) {
        this.fileExtension = fileExtension;
        this.trackParser = trackParser;
        this.parserOptions = parserOptions;
        this.optionsPane = optionsPane;
    }

    String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    TrackParser getTrackParser() {
        return trackParser;
    }

    void setTrackParser(TrackParser trackParser) {
        this.trackParser = trackParser;
    }

    Set<String> getParserOptions() {
        return parserOptions;
    }

    void setParserOptions(Set<String> parserOptions) {
        this.parserOptions = parserOptions;
    }

    GridPane getOptionsPane() {
        return optionsPane;
    }

    void setOptionsPane(GridPane optionsPane) {
        this.optionsPane = optionsPane;
    }
}
