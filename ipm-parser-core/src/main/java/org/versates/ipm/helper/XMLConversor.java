package org.versates.ipm.helper;

import org.versates.ipm.IPMFile;
import org.versates.ipm.iso.packager.ISOLayout;
import org.versates.ipm.parser.IPMFileParser;
import org.versates.ipm.parser.IPMParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.MessageFormat;

/**
 * Utility class for generation of output XML of IPM files.
 *
 * @author Ramses Vidor
 */
public enum XMLConversor {
    SINGLETON;

    public static final String HEADER_TAG;
    public static final String FOOTER_TAG = "\n</ipm-file>";
    public static final String ERROR_ELEMENT = "\t<error>Error on processing IPM file {0}.</error>";

    static {
        HEADER_TAG = "<?xml version=\"1.0\" encoding=\"" + Charset.defaultCharset()
                + "\"?>\n<!DOCTYPE ipm-file>\n<ipm-file name=\"{0}\" messages=\"{1}\">\n";
    }

    /**
     * Print IPM PRE-EDIT file output (ASCII) into XML format in the console system.
     * @param filenane The IPM file name - must be informed the complete path.
     */
    public static void dumpPreEdit(String filename) {
        dump(filename, ISOLayout.MASTERCARD_ASCII);
    }

    /**
     * Imprime o output do arquivo IPM EBCDIC (IBM1047) no formato XML no console do sistema.
     * Print IPM EBCDIC file output (IBM1074) into XML file in the console system.
     * @param filenane The IPM file name - must be informed the complete path.
     */
    public static void dumpEbcdic(String filename) {
        dump(filename, ISOLayout.MASTERCARD_EBCDIC);
    }

    /**
     * Print the IPM file output according to layout pre-difined into XML format in the console system.
     * @param filenane The IPM file name - must be informed the complete path.
     * @param layout   The layout ISO 8583 compatible to the file which will be processed.
     */
    public static void dump(String filename, ISOLayout layout) {
        System.out.println(xml(filename, layout));
    }

    /**
     * Convert IPM EBCDIC file output (IBM1074) into XML file format.
     * @param filenane The IPM file name - must be informed the complete path.
     */
    public static String ebcdicIpmToXml(String filename) {
        return xml(filename, ISOLayout.MASTERCARD_EBCDIC);
    }

    /**
     * Converte o arquivo IPM PRE-EDIT (ASCII) para o formato XML.
     * Convert IPM PRE-EDIT file (ASCII) into XML file format.
     * @param filenane The IPM file name - must be informed the complete path.
     */
    public static String asciiIpmToXml(String filename) {
        return xml(filename, ISOLayout.MASTERCARD_ASCII);
    }

    /**
     * Convert the IPM file output according to layout pre-difined into XML format.
     * @param filenane The IPM file name - must be informed the complete path.
     * @param layout   The layout ISO 8583 compatible to the file which will be processed.
     */
    public static String xml(String filename, ISOLayout layout) {
        try {
            return process(filename, layout).xml();
        } catch (Exception ignore) {
        }

        return MessageFormat.format(HEADER_TAG + ERROR_ELEMENT + FOOTER_TAG, filename, 0);
    }

    private static IPMFile process(String filename, ISOLayout layout) throws IOException, IPMParserException {
        return new IPMFile(filename, readFile(filename), IPMFileParser.createFileParser(layout));
    }

    private static byte[] readFile(final String filename) throws IOException {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
        if (in == null) {
            File file = new File(filename);

            if (!file.exists()) {
                throw new IOException(filename + " could not be found in the path provided.");
            }

            in = new FileInputStream(file);
        }

        final byte[] buffer = new byte[in.available()];
        in.read(buffer);
        in.close();

        return buffer;
    }

}
