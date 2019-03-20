package org.versates.ipm.parser;

import org.versates.ipm.iso.ISOMessage;
import org.versates.ipm.iso.packager.ISOLayout;
import org.apache.commons.lang.NotImplementedException;

import java.util.List;

/**
 * Parser genérico de extração de mensagens ISO 8583.
 *
 * @author Ramses Vidor
 */
public class IPMGenericFileParser extends IPMFileParser {

    private static final long serialVersionUID = -4065429267936576468L;

    /**
     * Constrói uma instância do parser
     */
    public IPMGenericFileParser() {
        super(ISOLayout.GENERIC);
    }

    @Override
    public List<ISOMessage> parse(byte[] bytes) {
        throw new NotImplementedException("ipmparser.notimplemented");
    }

}
