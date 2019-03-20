package org.versates.ipm.parser;

import org.versates.ipm.iso.ISOMessage;
import org.versates.ipm.iso.packager.ISOLayout;
import org.apache.commons.lang.StringUtils;
import org.jpos.iso.ISOException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Classe do parser IPM para arquivos IPM MasterCard com codificação EBCDIC.
 *
 * @author Ramses Vidor
 */
public class IPMMasterCardEbcdicFileParser extends IPMFileParser {

    private static final long serialVersionUID = -3686970010442090387L;

    private static final Pattern MTI_PATTERN = Pattern.compile("^(1240|1442|1644|1740)$");

    private final List<ISOMessage> messages = new ArrayList<ISOMessage>();

    /**
     * Constrói uma instância desse parser.
     */
    public IPMMasterCardEbcdicFileParser() {
        super(ISOLayout.MASTERCARD_EBCDIC);
    }

    @Override
    public List<ISOMessage> parse(byte[] bytes) {
        String content = new String(bytes, getLayout().getCharset());

        if (StringUtils.isNotEmpty(content)) {
            extractMessages(content);
        }

        return messages;
    }

    private void extractMessages(String content) {
        for (int i = 0; i < content.length(); i++) {
            if (content.length() > (i + 4) && MTI_PATTERN.matcher(content.substring(i, i + 4)).matches()) {
                i += extractMessage(content.substring(i));
            }
        }
    }

    private int extractMessage(String content) {
        ISOMessage message = createMessage();
        int i = -1;

        try {
            i += message.unpack(content.getBytes(getLayout().getCharset()));
        } catch (ISOException e) {
            feedCorruptedMessage(message, e);
            i += 2;
        }

        messages.add(message);

        return i;
    }

}
