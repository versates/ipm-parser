package org.versates.ipm.parser;

import org.versates.ipm.iso.ISOMessage;
import org.versates.ipm.iso.packager.ISOLayout;
import org.jpos.iso.ISOException;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe do parser IPM para arquivos IPM MasterCard com codificação ASCII/PRE-EDIT.
 *
 * @author Ramses Vidor
 */
public class IPMMasterCardPreEditFileParser extends IPMFileParser {

    private static final long serialVersionUID = -4097488041492435082L;

    private static final int OVERFLOW_LIMIT = 396;
    private static final int MIN_MESSAGE_LENGTH = 20;
    private static final int DEFAULT_HEADER_LENGTH = 132;

    /**
     * Constrói uma instância desse parser.
     */
    public IPMMasterCardPreEditFileParser() {
        super(ISOLayout.MASTERCARD_ASCII);
    }

    @Override
    public List<ISOMessage> parse(byte[] bytes) throws IPMParserException {
        return parse(bytes, DEFAULT_HEADER_LENGTH);
    }

    private List<ISOMessage> parse(byte[] bytes, int position) throws IPMParserException {
        final List<ISOMessage> messages = new ArrayList<ISOMessage>();

        try {
            if (position < (bytes.length - MIN_MESSAGE_LENGTH)) {
                final ISOMessage message = createMessage();
                byte[] messageContent = createMessageBuffer(bytes, position);
                position += message.unpack(messageContent);
                messages.add(message);
                parse(bytes, position);
            }
        } catch (final ISOException e) {
            if ((position == OVERFLOW_LIMIT) && (messages.size() == 0)) {
                throw new IPMParserException("ipmparser.parser.nomessages", e);
            }

            parse(bytes, ++position);
        }

        return messages;
    }

    private byte[] createMessageBuffer(final byte[] bytes, final int position) {
        int c = 0;
        final byte[] buffer = new byte[bytes.length - position];

        for (int i = position; i < bytes.length; i++) {
            buffer[c++] = bytes[i];
        }

        return buffer;
    }

}
