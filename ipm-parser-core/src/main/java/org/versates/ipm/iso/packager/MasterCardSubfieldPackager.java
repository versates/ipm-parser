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
 * Classe de extração de subcampos PDS ISO 8583.
 *
 * @author Ramses Vidor
 */
public abstract class MasterCardSubfieldPackager extends EuroSubFieldPackager implements Serializable {

    private static final long serialVersionUID = 1291027855911332870L;

    private final int tagOffset = 7;

    /**
     * Transcodifica uma mensagem ISO 8583 de <code>String</code> para <code>byte[]</code>
     *
     * @param message Conteúdo da mensagem ISO 8583 a ser transcodificada em bytes
     * @return o conteúdo transcodificado em bytes
     */
    protected abstract byte[] transcode(String message);

    /**
     * Transcodifica uma mensagem ISO 8583 de <code>byte[]</code> para <code>String</code>
     *
     * @param bytes Conteúdo da mensagem ISO 8583 a ser transcodificada em <code>byte[]</code>
     * @return o conteúdo transcodificado em bytes
     */
    protected abstract String transcode(byte[] bytes);

    /**
     * Converte as informações extraídas ao padrão ISO 8583 do arquivo IPM de acordo com a codificação utilizada.
     *
     * @param c O componente ISO em processamento
     * @return o conteúdo em <code>byte[]</code> do componente ISO 8583 em processamento
     * @throws ISOException Throws an exception when an error occur while parsing
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
     * Extrai as informações do componente da mensagem ISO 8583 do arquivo IPM de acordo com a codificação utilizada.
     *
     * @param m      O componente ISO em processamento
     * @param buffer O buffer com o conteúdo ISO 8583 a ser processado
     * @return o conteúdo em <code>byte[]</code> do componente ISO 8583 em processamento
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
