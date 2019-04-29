package org.versates.ipm.iso.packager;

import org.jpos.iso.EbcdicPrefixer;

/**
 * Extraction class of ISO 8583 PDS subfields in EBCDIC format and encoding.
 *
 * @author Ramses Vidor
 */
public class MasterCardEBCDICSubfieldPackager extends MasterCardSubfieldPackager {

    private static final long serialVersionUID = -713999477959913829L;

    /**
     * Constructs an instance of this object.
     */
    public MasterCardEBCDICSubfieldPackager() {
        super();
        tagPrefixer = EbcdicPrefixer.LLLL;
    }

    @Override
    protected byte[] transcode(String message) {
        return message.getBytes(ISOLayout.MASTERCARD_EBCDIC.getCharset());
    }

    @Override
    protected String transcode(byte[] bytes) {
        return new String(new String(bytes, ISOLayout.MASTERCARD_EBCDIC.getCharset()).getBytes());
    }

}
