package org.versates.ipm.parser;

import org.versates.ipm.helper.XMLConversor;
import org.junit.Test;

import java.io.File;
import java.io.PrintWriter;

public class MasterCardFileParserTest {

    private static final String TEMP_PATH = "/tmp";
    private static final String ROOT_PATH = "ipm-files/ebcdic/";
    private static final String[] FILES = new String[]{
            "UPS2.IPM.ORG748.141020.1250.ipm",
            "UPS2.IPM.ORG748.141021.1250.ipm",
            "UPS2.IPM.ORG748.141022.1250.ipm",
            "UPS2.IPM.ORG748.141023.1250.ipm",
            "UPS2.IPM.ORG748.141024.1250.ipm",
            "UPS6.IPM.ORG748.181220.0420.ipm",
            "UPS2.IPM.ORG748.140814.pds26.ipm",
            "UPS2.IPM.ORG748.140814.pds25.ipm"
    };

    @Test
    public void testTransaction() throws Exception {
        for (String file : FILES) {
            testXmlConvertion(file);
        }
    }

    @Test
    public void testXmlConvertion() throws Exception {
        testXmlConvertion("UPS2.IPM.ORG748.141020.1250.ipm");
    }

    private void testXmlConvertion(String file) throws Exception {
        File xml = new File(TEMP_PATH, file.endsWith("ipm") ? file.replaceAll("ipm$", "xml") : file + ".xml");

        if (xml.exists()) {
            xml.delete();
        }

        xml.createNewFile();
        PrintWriter printer = new PrintWriter(xml);
        printer.print(XMLConversor.ebcdicIpmToXml(ROOT_PATH + file));
        printer.close();
    }

    public void dump(String filename) {
        XMLConversor.dumpEbcdic(ROOT_PATH + filename);
    }

}
