package org.versates.ipm.iso.packager;

import org.jpos.iso.AsciiPrefixer;

/**
 * Extraction class of ISO 8583 PDS subfields in ASCII format and encoding.
 *
 * @author Ramses Vidor
 */
public class MasterCardASCIISubfieldPackager extends MasterCardSubfieldPackager {

    private static final long serialVersionUID = 8915742064905847237L;

    /**
     * Constructs an instance of this object.
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
