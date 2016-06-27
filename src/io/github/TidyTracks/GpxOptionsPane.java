package io.github.TidyTracks;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

import java.util.HashSet;
import java.util.Set;

class GpxOptionsPane {
  private Controller controller;
  private Set<String> parserOptions = new HashSet<>();

  GpxOptionsPane(Controller controller) {
    this.controller = controller;
  }

  GridPane getGpxOptionsPane() {
    Set<CheckBox> storeAdvanceOptions = new HashSet<>();
    GridPane optionsPane = new GridPane();
    optionsPane.getStylesheets().addAll(
        "/io/github/TidyTracks/GpxStyle.css");
    optionsPane.setVgap(10.0);
    ColumnConstraints column1 = new ColumnConstraints();
    column1.setHalignment(HPos.CENTER);
    column1.setHgrow(Priority.ALWAYS);
    optionsPane.getColumnConstraints().addAll(column1);
    RowConstraints row1 = new RowConstraints();
    RowConstraints row2 = new RowConstraints();
    RowConstraints row3 = new RowConstraints();
    row2.setVgrow(Priority.SOMETIMES);
    optionsPane.getRowConstraints().addAll(row1, row2, row3);
    Label optionsLabel
        = new Label("GPX Options (Check to remove from track, " +
        "hover for details)");
    optionsPane.add(optionsLabel, 0, 0);
    FlowPane optionsFlowPane =
        new FlowPane(Orientation.VERTICAL, 10.0, 10.0);
    optionsFlowPane.setPrefWrapLength(75.0);
    optionsFlowPane.setAlignment(Pos.TOP_CENTER);


    CheckBox[] optionsCheckBoxes = new CheckBox[15];
    optionsCheckBoxes[0] = new CheckBox("Elevation");
    optionsCheckBoxes[0].setTooltip(new Tooltip("Elevation (in meters)" +
        " of the point."));
    optionsCheckBoxes[0].setOnAction((event) -> {
      if (optionsCheckBoxes[0].isSelected()) {
        parserOptions.add("ele");
      } else {
        parserOptions.remove("ele");
      }
    });
    optionsCheckBoxes[1] = new CheckBox("Time");
    optionsCheckBoxes[1]
        .setTooltip(new Tooltip("Creation/modification timestamp " +
            "for element. Date and time in are in Univeral " +
            "Coordinated Time (UTC)"));
    optionsCheckBoxes[1].setOnAction(event -> {
      if (optionsCheckBoxes[1].isSelected()) {
        parserOptions.add("time");
      } else {
        parserOptions.remove("time");
      }
    });
    optionsCheckBoxes[2] = new CheckBox("Metadata");
    optionsCheckBoxes[2]
        .setTooltip(new Tooltip("Information about the GPX file, " +
            "and author goes in the metadata section."));
    optionsCheckBoxes[2].setOnAction(event -> {
      if (optionsCheckBoxes[2].isSelected()) {
        parserOptions.add("metadata");
      } else {
        parserOptions.remove("metadata");
      }
    });
    optionsCheckBoxes[3] = new CheckBox("Keywords");
    optionsCheckBoxes[3]
        .setTooltip(new Tooltip(
            "Keywords associated with the file. Search engines " +
                "or databases can use this information to " +
                "classify the data."));
    optionsCheckBoxes[3].setOnAction(event -> {
      if (optionsCheckBoxes[3].isSelected()) {
        parserOptions.add("keywords");
      } else {
        parserOptions.remove("keywords");
      }
    });
    optionsCheckBoxes[4] = new CheckBox("Comments");
    optionsCheckBoxes[4].setTooltip(new Tooltip("GPS comments for track."));
    optionsCheckBoxes[4].setOnAction(event -> {
      if (optionsCheckBoxes[4].isSelected()) {
        parserOptions.add("cmt");
      } else {
        parserOptions.remove("cmt");
      }
    });
    optionsCheckBoxes[5] = new CheckBox("Descriptions");
    optionsCheckBoxes[5]
        .setTooltip(new Tooltip("User descriptions of track."));
    optionsCheckBoxes[5].setOnAction(event -> {
      if (optionsCheckBoxes[5].isSelected()) {
        parserOptions.add("desc");
      } else {
        parserOptions.remove("desc");
      }
    });
    optionsCheckBoxes[6] = new CheckBox("Names");
    optionsCheckBoxes[6].setTooltip(new Tooltip("Names of tracks, " +
        "waypoints, and routes"));
    optionsCheckBoxes[6].setOnAction(event -> {
      if (optionsCheckBoxes[6].isSelected()) {
        parserOptions.add("name");
      } else {
        parserOptions.remove("name");
      }
    });
    optionsCheckBoxes[7] = new CheckBox("Links");
    optionsCheckBoxes[7]
        .setTooltip(new Tooltip("URLs associated with the " +
            "location described in the file."));
    optionsCheckBoxes[7].setOnAction(event -> {
      if (optionsCheckBoxes[7].isSelected()) {
        parserOptions.add("link");
      } else {
        parserOptions.remove("link");
      }
    });
    optionsCheckBoxes[8] = new CheckBox("Author");
    optionsCheckBoxes[8]
        .setTooltip(new Tooltip("The person or organization who " +
            "created the GPX file."));
    optionsCheckBoxes[8].setOnAction(event -> {
      if (optionsCheckBoxes[8].isSelected()) {
        parserOptions.add("author");
      } else {
        parserOptions.remove("author");
      }
    });
    optionsCheckBoxes[9] = new CheckBox("Copyright");
    optionsCheckBoxes[9]
        .setTooltip(new Tooltip("Copyright and license information " +
            "governing use of the file."));
    optionsCheckBoxes[9].setOnAction(event -> {
      if (optionsCheckBoxes[9].isSelected()) {
        boolean result = controller.showConfirmationDialog("Copyright" +
            " Warning", "Copyright Warning" + System.lineSeparator()
            + "Tidy Tracks Is Not Responsible For Removing" +
            " Copyright Without Permission", "Do you have " +
            "permission to remove the copyright information" +
            " from this file?");
        if (result) {
          parserOptions.add("copyright");
        } else {
          optionsCheckBoxes[9].setSelected(false);
        }
      } else {
        parserOptions.remove("copyright");
      }
    });
    optionsCheckBoxes[10] = new CheckBox("Bounds");
    optionsCheckBoxes[10]
        .setTooltip(new Tooltip(
            "Minimum and maximum coordinates which describe the" +
                " extent of the coordinates in the file."));
    optionsCheckBoxes[10].setOnAction(event -> {
      if (optionsCheckBoxes[10].isSelected()) {
        parserOptions.add("bounds");
      } else {
        parserOptions.remove("bounds");
      }
    });
    optionsCheckBoxes[11] = new CheckBox("Extensions");
    optionsCheckBoxes[11]
        .setTooltip(new Tooltip("Elements from another schema " +
            "extend GPX."));
    optionsCheckBoxes[11].setOnAction(event -> {
      if (optionsCheckBoxes[11].isSelected()) {
        parserOptions.add("extensions");
      } else {
        parserOptions.remove("extensions");
      }
    });
    optionsCheckBoxes[12] = new CheckBox("All Tracks");
    optionsCheckBoxes[12].setTooltip(new Tooltip("All tracks stored in" +
        " the GPX file"));
    optionsCheckBoxes[12].setOnAction(event -> {
      if (optionsCheckBoxes[12].isSelected()) {
        parserOptions.add("trk");
      } else {
        parserOptions.remove("trk");
      }
    });
    optionsCheckBoxes[13] = new CheckBox("All Waypoints");
    optionsCheckBoxes[13].setTooltip(new Tooltip("All waypoints stored" +
        " in the GPX file"));
    optionsCheckBoxes[13].setOnAction(event -> {
      if (optionsCheckBoxes[13].isSelected()) {
        parserOptions.add("wpt");
      } else {
        parserOptions.remove("wpt");
      }
    });
    optionsCheckBoxes[14] = new CheckBox("All Routes");
    optionsCheckBoxes[14].setTooltip(new Tooltip("All routes stored" +
        " in the GPX file"));
    optionsCheckBoxes[14].setOnAction(event -> {
      if (optionsCheckBoxes[14].isSelected()) {
        parserOptions.add("rte");
      } else {
        parserOptions.remove("rte");
      }
    });

    CheckBox[] advanceCheckBoxes = new CheckBox[9];
    advanceCheckBoxes[0] = new CheckBox("Magnetic Variations");
    advanceCheckBoxes[0]
        .setTooltip(new Tooltip("Magnetic variation (in degrees)" +
            " at the point"));
    advanceCheckBoxes[0].setOnAction(event -> {
      if (advanceCheckBoxes[0].isSelected()) {
        parserOptions.add("magvar");
      } else {
        parserOptions.remove("magvar");
      }
    });
    advanceCheckBoxes[1] = new CheckBox("Geoid Heights");
    advanceCheckBoxes[1]
        .setTooltip(new Tooltip("Height (in meters) of geoid " +
            "(mean sea level) above WGS84 earth ellipsoid.  " +
            "As defined in NMEA GGA message."));
    advanceCheckBoxes[1].setOnAction(event -> {
      if (advanceCheckBoxes[1].isSelected()) {
        parserOptions.add("geoidheight");
      } else {
        parserOptions.remove("geoidheight");
      }
    });
    advanceCheckBoxes[2] = new CheckBox("Sources");
    advanceCheckBoxes[2]
        .setTooltip(new Tooltip(
            "Source of data. Included to give user some idea of" +
                " reliability and accuracy of data."));
    advanceCheckBoxes[2].setOnAction(event -> {
      if (advanceCheckBoxes[2].isSelected()) {
        parserOptions.add("src");
      } else {
        parserOptions.remove("src");
      }
    });
    advanceCheckBoxes[3] = new CheckBox("Symbols");
    advanceCheckBoxes[3]
        .setTooltip(new Tooltip("Text of GPS symbol names."));
    advanceCheckBoxes[3].setOnAction(event -> {
      if (advanceCheckBoxes[3].isSelected()) {
        parserOptions.add("sym");
      } else {
        parserOptions.remove("sym");
      }
    });
    advanceCheckBoxes[4] = new CheckBox("Fixes");
    advanceCheckBoxes[4].setTooltip(new Tooltip("Type of GPX fix."));
    advanceCheckBoxes[4].setOnAction(event -> {
      if (advanceCheckBoxes[4].isSelected()) {
        parserOptions.add("fix");
      } else {
        parserOptions.remove("fix");
      }
    });
    advanceCheckBoxes[5] = new CheckBox("Number of Satellites");
    advanceCheckBoxes[5]
        .setTooltip(new Tooltip("Number of satellites used to" +
            " calculate the GPX fix."));
    advanceCheckBoxes[5].setOnAction(event -> {
      if (advanceCheckBoxes[5].isSelected()) {
        parserOptions.add("sat");
      } else {
        parserOptions.remove("sat");
      }
    });
    advanceCheckBoxes[6] = new CheckBox("Dilutions");
    advanceCheckBoxes[6].setTooltip(new Tooltip("Dilutions of precision."));
    advanceCheckBoxes[6].setOnAction(event -> {
      if (advanceCheckBoxes[6].isSelected()) {
        parserOptions.add("hdop");
        parserOptions.add("vdop");
        parserOptions.add("pdop");
      } else {
        parserOptions.remove("hdop");
        parserOptions.remove("vdop");
        parserOptions.remove("pdop");
      }
    });
    advanceCheckBoxes[7] = new CheckBox("Age of DGPS Data");
    advanceCheckBoxes[7].setTooltip(
        new Tooltip("Number of seconds since last DGPS update."));
    advanceCheckBoxes[7].setOnAction(event -> {
      if (advanceCheckBoxes[7].isSelected()) {
        parserOptions.add("ageofdgpsdata");
      } else {
        parserOptions.remove("ageofdgpsdata");
      }
    });
    advanceCheckBoxes[8] = new CheckBox("DGPS ID");
    advanceCheckBoxes[8]
        .setTooltip(new Tooltip("ID of DGPS station used in " +
            "differential correction."));
    advanceCheckBoxes[8].setOnAction(event -> {
      if (advanceCheckBoxes[8].isSelected()) {
        parserOptions.add("dgpsid");
      } else {
        parserOptions.remove("dgpsid");
      }
    });

    optionsFlowPane.getChildren().addAll(optionsCheckBoxes);
    optionsPane.add(optionsFlowPane, 0, 1);

    Hyperlink selectAll = new Hyperlink("Select All");
    selectAll.getStyleClass().add("select-hyperlinks");
    selectAll.setOnAction(event -> {
      for (int i = 0; i < 12; i++) {
        if (!optionsCheckBoxes[i].isSelected()) {
          optionsCheckBoxes[i].fire();
        }
      }
      for (CheckBox checkBox : advanceCheckBoxes) {
        if (!checkBox.isSelected()) {
          checkBox.fire();
        }
      }
    });
    Hyperlink deselectAll = new Hyperlink("Deselect All");
    deselectAll.getStyleClass().add("select-hyperlinks");
    deselectAll.setOnAction(event -> {
      for (CheckBox checkBox : optionsCheckBoxes) {
        if (checkBox.isSelected()) {
          checkBox.fire();
        }
      }
      for (CheckBox checkBox : advanceCheckBoxes) {
        if (checkBox.isSelected()) {
          checkBox.fire();
        }
      }
    });

    FlowPane advanceFlowPane =
        new FlowPane(Orientation.VERTICAL, 10.0, 10.0);
    advanceFlowPane.setPrefWrapLength(75.0);
    advanceFlowPane.setAlignment(Pos.TOP_CENTER);
    advanceFlowPane.setManaged(false);
    advanceFlowPane.setPadding(new Insets(5, 5, 5, 5));
    advanceFlowPane.getChildren().addAll(advanceCheckBoxes);
    advanceFlowPane.getChildren().addAll(selectAll, deselectAll);

    TitledPane titledPane = new TitledPane();
    titledPane.setText("Advanced Options");
    titledPane.setExpanded(false);
    titledPane.setContent(advanceFlowPane);
    titledPane.setAnimated(false);
    titledPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    titledPane.expandedProperty().addListener(
        (observable, oldValue, newValue) -> {
          if (newValue) {
            storeAdvanceOptions.stream().forEach(checkBox -> {
              if (!checkBox.isSelected()) {
                checkBox.fire();
              }
            });
            storeAdvanceOptions.clear();
          } else {
            for (CheckBox checkBox : advanceCheckBoxes) {
              if (checkBox.isSelected()) {
                storeAdvanceOptions.add(checkBox);
                checkBox.fire();
              }
            }
          }
          advanceFlowPane.setManaged(newValue);
          controller.setStageSize();
        });
    optionsPane.add(titledPane, 0, 2);
    return optionsPane;
  }

  Set<String> getParserOptions() {
    return parserOptions;
  }
}
