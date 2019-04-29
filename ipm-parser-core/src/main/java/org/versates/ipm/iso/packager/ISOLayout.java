package org.versates.ipm.iso.packager;

import java.nio.charset.Charset;

/**
 * Lists the layout types available for processing IPM files.
 *
 * @author Ramses Vidor
 */
public enum ISOLayout {

    /**
     * Generic layout, for unknown and / or undefined IPM files.
     */
    GENERIC(Charset.forName("ISO-8859-1"), ""),

    /**
     * PRE-EDIT / ASCII layout for IPM MasterCard files.
     */
    MASTERCARD_ASCII(Charset.forName("ISO-8859-1"), "mastercard-ascii.xml"),

    /**
     * EBCDIC / MainFrame Layout (IBM1047) for IPM MasterCard files.
     */
    MASTERCARD_EBCDIC(Charset.forName("IBM1047"), "mastercard-ebcdic.xml");

    private final Charset charset;
    private final String configuration;

    ISOLayout(Charset charset, String configuration) {
        this.charset = charset;
        this.configuration = configuration;
    }

    /**
     * Returns the default charset of the layout.
     *
     * @return charset of layout
     */
    public Charset getCharset() {
        return charset;
    }

    /**
     * Returns the path of the ISO 8583 layout configuration XML file.
     *
     * @return the path of the configuration file
     */
    public String getConfigurationPath() {
        return "com/totvscard/ipm/iso/packager/" + configuration;
    }

}
