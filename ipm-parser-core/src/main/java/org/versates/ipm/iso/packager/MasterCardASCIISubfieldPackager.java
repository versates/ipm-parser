package org.versates.ipm.iso.packager;

import org.jpos.iso.AsciiPrefixer;

/**
 * Classe de extração de subcampos PDS ISO 8583 no formato e codificação ASCII.
 *
 * @author Ramses Vidor
 */
public class MasterCardASCIISubfieldPackager extends MasterCardSubfieldPackager {

    private static final long serialVersionUID = 8915742064905847237L;

    /**
     * Constrói uma instância desse objeto.
     */
    public MasterCardASCIISubfieldPackager() {
        super();
        tagPrefixer = AsciiPrefixer.LLLL;
    }

    @Override
    protected byte[] transcode(String message) {
        return message.getBytes();
    }

    @Override
    protected String transcode(byte[] bytes) {
        return new String(bytes);
    }

}
