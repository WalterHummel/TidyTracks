package io.github.TidyTracks;

import javafx.scene.control.Alert;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


class GpxParser extends DefaultHandler implements LexicalHandler, TrackParser {
  private final String CREATOR = "Tidy Tracks " + Main.VERSION;
  private final String INDENT = " ";

  private Controller controller;
  private Set<String> parserOptions;
  private StringBuffer stringBuffer;
  private Writer outputFileWriter;
  private int indentCount;
  private int lastIndentCount;
  private boolean suppressOutput;
  private String suppressOutputTag;
  private long startExecutionTime;
  private long endExecutionTime;
  private boolean inCDATA;
  private boolean printedStartCDATA;

  private Map<String, Integer> counts = new HashMap<>();


  GpxParser(Controller controller, Set<String> parserOptions) {
    this.controller = controller;
    this.parserOptions = parserOptions;
  }

  /*
   * Content Handler
   */
  @Override
  public void startDocument() throws SAXException {
    startExecutionTime = System.currentTimeMillis();
    print("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>");
    counts.clear();
  }

  @Override
  public void startElement(String uri, String localName, String qName,
                           Attributes attributes) throws SAXException {
    // catch special case
    if (stringBuffer != null) {
      if (!suppressOutput) {
        print(stringBuffer.toString().trim());
      }
      stringBuffer = null;
    }
    if (!suppressOutput && !inCDATA && printedStartCDATA) {
      print("]]>");
      printedStartCDATA = false;
    }
    String name = qName;
    if (name.isEmpty()) {
      // should never happen, parser namespace-prefixes = true
      name = localName;
    }
    if (parserOptions.contains(name)) {
      if (!suppressOutput) {
        suppressOutput = true;
        suppressOutputTag = name;
      }
      Integer value = counts.get(name);
      if (value == null) {
        counts.put(name, 1);
      } else {
        int count = value;
        count++;
        counts.put(name, count);
      }
      return;
    }
    if (suppressOutput) {
      return;
    }
    newLine();
    print("<" + name);
    for (int i = 0; i < attributes.getLength(); i++) {
      String attributeName = attributes.getQName(i);
      if (attributeName.isEmpty()) {
        // should never happen, parser namespace-prefixes = true
        attributeName = attributes.getLocalName(i);
      }
      if (name.equals("gpx") && attributeName.equals("creator")) {
        print(" " + "creator=\"" + CREATOR + "\"");
      } else {
        print(" " + attributeName + "=\"" + attributes.getValue(i) +
            "\"");
      }
    }
    print(">");
    indentCount++;
    lastIndentCount = indentCount;
  }

  @Override
  public void characters(char[] ch, int start, int length)
      throws SAXException {
    if (stringBuffer == null) {
      stringBuffer = new StringBuffer();
    }
    stringBuffer.append(ch, start, length);
  }

  @Override
  public void endElement(String uri, String localName, String qName)
      throws SAXException {
    if (stringBuffer != null) {
      if (!suppressOutput) {
        print(stringBuffer.toString().trim());
      }
      stringBuffer = null;
    }
    if (!suppressOutput && !inCDATA && printedStartCDATA) {
      print("]]>");
      printedStartCDATA = false;
    }
    String name = qName;
    if (name.isEmpty()) {
      // should never happen, parser namespace-prefixes = true
      name = localName;
    }
    if (suppressOutput) {
      if (name.equals(suppressOutputTag)) {
        suppressOutput = false;
      }
      return;
    }
    // print on correct line for nested elements
    if (indentCount < lastIndentCount) {
      indentCount--;
      newLine();
      print("</" + name + ">");
    } else {
      print("</" + name + ">");
      indentCount--;
    }
  }

  @Override
  public void endDocument() throws SAXException {
    try {
      outputFileWriter.flush();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        outputFileWriter.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    endExecutionTime = System.currentTimeMillis();
    showCountsDialog();
  }

  private void print(String s) throws SAXException {
    try {
      outputFileWriter.write(s);
    } catch (IOException e) {
      throw new SAXException(e);
    }
  }

  private void newLine() throws SAXException {
    String nl = System.lineSeparator();
    print(nl);
    for (int i = 0; i < indentCount; i++) {
      print(INDENT);
    }
  }

  /*
   * Lexical Handler
   */
  @Override
  public void startDTD(String name, String publicId, String systemId)
      throws SAXException {
    // Do Nothing
  }

  @Override
  public void endDTD() throws SAXException {
    // Do Nothing
  }

  @Override
  public void startEntity(String name) throws SAXException {
    // Do Nothing
  }

  @Override
  public void endEntity(String name) throws SAXException {
    // Do Nothing
  }

  @Override
  public void startCDATA() throws SAXException {
    inCDATA = true;
    if (!suppressOutput) {
      print("<![CDATA[");
      printedStartCDATA = true;
    }
  }

  @Override
  public void endCDATA() throws SAXException {
    inCDATA = false;
  }

  @Override
  public void comment(char[] ch, int start, int length) throws SAXException {
    // Do Nothing
  }

  /*
   * Error Handler
   */
  @Override
  public void warning(SAXParseException e) throws SAXException {
    System.err.println("SAX Parser Warning: " + e.getMessage());
  }

  @Override
  public void error(SAXParseException e) throws SAXException {
    String message = "Error reading GPX file on line "
        + e.getLineNumber() + ", column " + e.getColumnNumber() + ". "
        + e.getMessage();
    System.err.println("SAX Parser Error: " + message);
    //controller.showDialog(Alert.AlertType.ERROR,
    //        "GPX Parsing Error", "Error Reading GPX File", message);
  }

  @Override
  public void fatalError(SAXParseException e) throws SAXException {
    throw new SAXException(e);
  }

  private void showCountsDialog() {
    StringBuilder message = new StringBuilder();
    if (counts.isEmpty()) {
      message.append("Nothing was removed from the track");
    } else {
      message.append("Removed the following from the track:");
      message.append(System.lineSeparator());
      for (String key : counts.keySet()) {
        boolean oneEntry = false;
        int entry = counts.get(key);
        message.append(String.format("%,d", entry));
        if (entry == 1) {
          oneEntry = true;
        }
        switch (key) {
          case "ele":
            message.append(" Elevation");
            break;
          case "time":
            message.append(" Time");
            break;
          case "metadata":
            message.append(" Metadata");
            break;
          case "keywords":
            message.append(" Keyword");
            break;
          case "cmt":
            message.append(" Comment");
            break;
          case "desc":
            message.append(" Description");
            break;
          case "name":
            message.append(" Name");
            break;
          case "link":
            message.append(" Link");
            break;
          case "author":
            message.append(" Author");
            break;
          case "copyright":
            message.append(" Copyright");
            break;
          case "bounds":
            message.append(" Bounds");
            break;
          case "extensions":
            message.append(" Extension");
            break;
          case "trk":
            message.append(" Track");
            break;
          case "wpt":
            message.append(" Waypoint");
            break;
          case "rte":
            message.append(" Route");
            break;
          case "magvar":
            message.append(" Magnetic Variation");
            break;
          case "geoidheight":
            message.append("Geoid Height");
            break;
          case "src":
            message.append(" Source");
            break;
          case "sym":
            message.append(" Symbol");
            break;
          case "fix":
            message.append(" Fix");
            break;
          case "sat":
            message.append(" Satellite");
            break;
          case "hdop":
            message.append(" Horizontal Dilution");
            break;
          case "vdop":
            message.append(" Vertical Dilution");
            break;
          case "pdop":
            message.append(" Position Dilution");
            break;
          case "ageofdgpsdata":
            message.append(" Age of DGPS");
            break;
          case "dgpsid":
            message.append("DGPS ID");
            break;
          default:
            message.append(" Unknown");
        }
        if (oneEntry) {
          message.append(" entry");
        } else {
          message.append(" entries");
        }
        message.append(System.lineSeparator());
      }
    }
    long executionTime = endExecutionTime - startExecutionTime;
    controller.showDialog(Alert.AlertType.INFORMATION, "Tidy Up Successful",
        "Tidy Up Completed Successfully in " + executionTime + " ms",
        message.toString());
  }

  private void tidyUpFailed(File outputFile) {
    controller.showDialog(Alert.AlertType.ERROR, "Tidy Up Failed",
        null, "Tidy Tracks failed to tidy track :(");
    try {
      outputFileWriter.close();
      Files.delete(outputFile.toPath());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void parser(File inputFile, File outputFile) {
    if (!inputFile.exists()) {
      controller.showDialog(Alert.AlertType.ERROR, "Input File Error",
          null, "Input file cannot be found");
      return;
    }
    if (!inputFile.canRead()) {
      controller.showDialog(Alert.AlertType.ERROR, "Input File Error",
          null, "Cannot read the input file");
      return;
    }
    if (!FileUtil.getFileExtension(outputFile.getAbsolutePath())
        .equals(FileUtil
            .getFileExtension(inputFile.getAbsolutePath()))) {
      controller.showDialog(Alert.AlertType.WARNING,
          "Cannot Convert File Format",
          "Cannot convert the current file format",
          "Tidy Tracks cannot convert the between file formats. "
              + "Please save the output track with the \".gpx\" "
              + "file extension");
      return;
    }
    if (parserOptions.isEmpty()) {
      controller
          .showDialog(Alert.AlertType.WARNING, "No Options Selected",
              "No options have been selected",
              "Please check at least one option to continue.");
      return;
    }
    try {
      outputFileWriter = new BufferedWriter(new OutputStreamWriter
          (new FileOutputStream(outputFile), "UTF8"));
    } catch (IOException e) {
      controller.showDialog(Alert.AlertType.ERROR, "Output File Error",
          "Error writing to output file", e.getMessage());
      return;
    }

    try {
      SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
      saxParserFactory.setNamespaceAware(true);
      saxParserFactory.setFeature(
          "http://xml.org/sax/features/namespace-prefixes", true);
      saxParserFactory.setValidating(true);
      SAXParser saxParser = saxParserFactory.newSAXParser();
      // Set property to validate with schema defined in the document
      //import com.sun.org.apache.xerces.internal.jaxp.JAXPConstants;
      // JAXPConstants.JAXP_SCHEMA_LANGUAGE
      final String JAXP_SCHEMA_LANGUAGE =
          "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
      final String W3C_XML_SCHEMA =
          "http://www.w3.org/2001/XMLSchema";
      saxParser.setProperty(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
      XMLReader xmlReader = saxParser.getXMLReader();
      xmlReader.setContentHandler(this);
      xmlReader.setErrorHandler(this);
      xmlReader.setProperty(
          "http://xml.org/sax/properties/lexical-handler", this);
      xmlReader.parse(inputFile.toURI().toString());
    } catch (SAXException e) {
      // Unwrap SAXException
      Exception ex = e.getException();
      if (ex == null) {
        ex = e;
      }
      if (ex instanceof IOException) {
        controller.showDialog(Alert.AlertType.ERROR, "I/O Error",
            "I/O Error", e.getMessage());
        tidyUpFailed(outputFile);
        return;
      }
      if (ex instanceof SAXParseException) {
        String message = "Error reading GPX file on line "
            + ((SAXParseException) ex).getLineNumber() + ", column "
            + ((SAXParseException) ex).getColumnNumber() + ". "
            + ex.getMessage();
        controller.showDialog(Alert.AlertType.ERROR,
            "GPX Parsing Error", "Error Reading GPX File", message);
        tidyUpFailed(outputFile);
        return;
      }
      // Unknown error
      controller.showExceptionDialog("Parsing Exception",
          "Parsing Exception", "Unknown SAXException", ex);
      tidyUpFailed(outputFile);
    } catch (IOException e) {
      controller.showExceptionDialog("I/O Error",
          "I/O Error", "Unknown IOException", e);
      tidyUpFailed(outputFile);
    } catch (ParserConfigurationException e) {
      controller.showExceptionDialog("Parser Configuration Error", null,
          "XML parser does not support the required configuration",
          e);
      tidyUpFailed(outputFile);
    } finally {
      try {
        outputFileWriter.close();
      } catch (IOException e) {
        // Nothing left to do
        e.printStackTrace();
      }
    }
  }
}
