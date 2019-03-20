package org.versates.ipm.iso.packager;

import java.nio.charset.Charset;

/**
 * Enumera os tipos de layout disponíveis para processamento de arquivos IPM.
 *
 * @author Ramses Vidor
 */
public enum ISOLayout {

    /**
     * Layout genérico, para arquivos IPM de origem desconhecida e/ou indefinidos.
     */
    GENERIC(Charset.forName("ISO-8859-1"), ""),

    /**
     * Layout PRE-EDIT/ASCII para arquivos IPM MasterCard.
     */
    MASTERCARD_ASCII(Charset.forName("ISO-8859-1"), "mastercard-ascii.xml"),

    /**
     * Layout EBCDIC/MainFrame (IBM1047) para arquivos IPM MasterCard.
     */
    MASTERCARD_EBCDIC(Charset.forName("IBM1047"), "mastercard-ebcdic.xml");

    private final Charset charset;
    private final String configuration;

    ISOLayout(Charset charset, String configuration) {
        this.charset = charset;
        this.configuration = configuration;
    }

    /**
     * Retorna o charset padrão definido do layout.
     *
     * @return charset do layout
     */
    public Charset getCharset() {
        return charset;
    }

    /**
     * Retorna o caminho do arquivo XML de configuração do layout ISO 8583.
     *
     * @return o caminho do arquivo de configuração
     */
    public String getConfigurationPath() {
        return "com/totvscard/ipm/iso/packager/" + configuration;
    }

}
