package org.versates.ipm.iso.packager;

import org.apache.commons.lang.StringUtils;
import org.jpos.iso.ISOComponent;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOField;
import org.jpos.iso.packager.EuroSubFieldPackager;

import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Extraction class of ISO 8583 PDS subfields.
 *
 * @author Ramses Vidor
 */
public abstract class MasterCardSubfieldPackager extends EuroSubFieldPackager implements Serializable {

    private static final long serialVersionUID = 1291027855911332870L;

    private final int tagOffset = 7;

    /**
     * Transcodes an ISO 8583 message from <code> String </ code> to <code> byte [] </ code>
     *
     * @param message ISO 8583 message content to be transcoded into bytes
     * @return transcoded content in bytes
     */
    protected abstract byte[] transcode(String message);

    /**
     * Transcodes an ISO 8583 message from <code> byte [] </ code> to <code> String </ code>
     *
     * @param bytes Content of the ISO 8583 message to be transcoded into <code> byte [] </ code>
     * @return transcoded content in bytes
     */
    protected abstract String transcode(byte[] bytes);

    /**
     * Converts the extracted information to the ISO 8583 standard of the IPM file according to the encoding used.
     *
     * @param c The in-process ISO component
     * @return the content in <code> byte [] </ code> of the ISO 8583 component being processed
     * @throws ISOException Throws an exception when an error occurs while parsing
     */
    @Override
    public byte[] pack(final ISOComponent c) throws ISOException {
        @SuppressWarnings("unchecked")
        Map<Integer, ISOComponent> children = c.getChildren();
        StringBuilder message = new StringBuilder();

        if (children != null) {
            for (final Entry<Integer, ISOComponent> entry : children.entrySet()) {
                if (entry.getValue() instanceof ISOField) {
                    final ISOComponent field = entry.getValue();
                    final String content = (String) field.getValue();
                    message.append(padLeft((Integer) field.getKey(), 4));
                    message.append(padLeft(content.length(), 3));
                    message.append(content);
                }
            }
        }

        return transcode(message.toString());
    }

    /**
     * Extracts information from the ISO 8583 message component of the IPM file according to the encoding used.
     *
     * @param m The in-process ISO component
     * @param buffer The buffer with the ISO 8583 content to be processed
     * @return the content in <code> byte [] </ code> of the ISO 8583 component being processed
     * @throws ISOException Throws an exception if the extraction fails
     */
    @Override
    public int unpack(final ISOComponent m, final byte[] buffer) throws ISOException {
        String message = transcode(buffer);
        int length = 0;

        if (StringUtils.isNotEmpty(message)) {
            length = message.length();

            while (message.length() > 0) {
                final ISOField field = unpackField(message);
                m.set(field);
                message = message.substring(tagOffset + ((String) field.getValue()).length());
            }
        }

        return length;
    }

    private ISOField unpackField(final String message) throws ISOException {
        int tagSize = 4;
        String index = message.substring(0, tagSize);

        if (!index.matches("[0-9]{" + tagSize + ",}")) {
            throw new ISOException("ISO Message may be corrupted. Invalid field index: " + index);
        }

        return new ISOField(Integer.parseInt(index), message.substring(tagOffset,
                Integer.parseInt(message.substring(tagSize, tagOffset)) + tagOffset));
    }

    private String padLeft(final int value, final int paddingSize) {
        return String.format("%0" + paddingSize + "d", value);
    }

}
