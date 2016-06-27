package io.github.TidyTracks;

import javax.xml.parsers.ParserConfigurationException;

import java.io.File;

interface TrackParser {
    void parser(File inputFile, File outputFile);
}