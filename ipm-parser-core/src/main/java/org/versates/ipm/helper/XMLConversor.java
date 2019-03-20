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
 * Classe utilitária para geração de output em XML de arquivos IPM.
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
     * Imprime o output do arquivo IPM PRE-EDIT (ASCII) no formato XML no console do sistema.
     *
     * @param filename O nome do arquivo IPM - deve ser informado o caminho completo
     */
    public static void dumpPreEdit(String filename) {
        dump(filename, ISOLayout.MASTERCARD_ASCII);
    }

    /**
     * Imprime o output do arquivo IPM EBCDIC (IBM1047) no formato XML no console do sistema.
     *
     * @param filename O nome do arquivo IPM - deve ser informado o caminho completo
     */
    public static void dumpEbcdic(String filename) {
        dump(filename, ISOLayout.MASTERCARD_EBCDIC);
    }

    /**
     * Imprime o output do arquivo IPM de acordo com o layout definido no formato XML no console do sistema.
     *
     * @param filename O nome do arquivo IPM - deve ser informado o caminho completo
     * @param layout   O layout ISO 8583 compatível com o arquivo a ser processado
     */
    public static void dump(String filename, ISOLayout layout) {
        System.out.println(xml(filename, layout));
    }

    /**
     * Converte o arquivo IPM EBCDIC (IBM1047) para o formato XML.
     *
     * @param filename O nome do arquivo IPM - deve ser informado o caminho completo
     */
    public static String ebcdicIpmToXml(String filename) {
        return xml(filename, ISOLayout.MASTERCARD_EBCDIC);
    }

    /**
     * Converte o arquivo IPM PRE-EDIT (ASCII) para o formato XML.
     *
     * @param filename O nome do arquivo IPM - deve ser informado o caminho completo
     */
    public static String asciiIpmToXml(String filename) {
        return xml(filename, ISOLayout.MASTERCARD_ASCII);
    }

    /**
     * Converte o arquivo IPM de acordo com o layout definido para o formato XML.
     *
     * @param filename O nome do arquivo IPM - deve ser informado o caminho completo
     * @param layout   O layout ISO 8583 compatível com o arquivo a ser processado
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
