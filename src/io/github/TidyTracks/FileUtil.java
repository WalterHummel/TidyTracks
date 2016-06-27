package io.github.TidyTracks;

import javafx.stage.FileChooser;

import java.io.File;

final class FileUtil {
  private static final String[] ACCEPTED_EXTENSIONS =
      TrackHandlers.ACCEPTED_EXTENSIONS;
  private static final FileChooser.ExtensionFilter[] EXTENSION_FILTERS =
      TrackHandlers.EXTENSION_FILTERS;

  private FileUtil() {
  }

  static boolean isValidExtension(String filePath) {
    String fileExtension = getFileExtension(filePath);
    for (String extension : ACCEPTED_EXTENSIONS) {
      if (fileExtension.equals(extension)) {
        return true;
      }
    }
    return false;
  }

  static String getFileExtension(String filePath) {
    if (filePath.lastIndexOf(".") < 0) {
      return "";
    }
    return filePath.substring(filePath.lastIndexOf("."))
        .trim().toLowerCase();
  }

  static FileChooser getFileChooser() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setInitialDirectory
        (new File(System.getProperty("user.home")));
    for (FileChooser.ExtensionFilter filter : EXTENSION_FILTERS) {
      fileChooser.getExtensionFilters().add(filter);
    }
    return fileChooser;
  }

  static FileChooser getFileChooser(String filePath) {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setInitialDirectory
        (new File(System.getProperty("user.home")));
    if (isValidExtension(filePath) && new File(filePath).exists()) {
      String fileExtension = getFileExtension(filePath);
      for (FileChooser.ExtensionFilter filter : EXTENSION_FILTERS) {
        for (String extensions : filter.getExtensions()) {
          if (fileExtension.equals(getFileExtension(extensions))) {
            fileChooser.getExtensionFilters().add(filter);
            return fileChooser;
          }
        }
      }
    } else {
      for (FileChooser.ExtensionFilter filter : EXTENSION_FILTERS) {
        fileChooser.getExtensionFilters().add(filter);
      }
    }
    return fileChooser;
  }
}
