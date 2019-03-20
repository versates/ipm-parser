package org.versates.ipm.iso;

import org.versates.ipm.parser.IPMParserException;
import org.apache.commons.lang.StringUtils;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import java.io.PrintStream;
import java.io.Serializable;

/**
 * Classe que representa a mensagem ISO 8583 como uma transação financeira.
 *
 * @author Ramses Vidor
 */
public class ISOTransaction implements Serializable {

    private static final long serialVersionUID = -1521612733518231608L;
    private static final int PDS_INDEX = 48;

    private final ISOMessage message;
    private ISOTransaction pds;

    /**
     * Constrói uma instância de mensagem de transação financeira.
     *
     * @param message A mensagem ISO 8583 que deverá ser representada como transação financeira
     * @throws IPMParserException Se os critérios de processamento da mensagem ISO 8583 não forem obedecidos, uma IPMParserException é
     *                            lançada
     */
    public ISOTransaction(ISOMessage message) throws IPMParserException {
        if (message == null) {
            throw new IPMParserException("ipmparser.transaction.nomessage");
        }

        this.message = message;

        try {
            if (message.hasField(PDS_INDEX)) {
                pds = new ISOTransaction(new ISOMessage((ISOMsg) message.getValue(PDS_INDEX)));
            }
        } catch (ISOException e) {
            throw new IPMParserException("ipmparser.transaction.noprivatedata");
        }
    }

    /**
     * Retorna o MTI da mensagem ISO 8583.
     *
     * @return o código MTI da mensagem
     * @throws ISOException Se o valor do campo MTI não é válido ou não for possível extrair as informações do campo, uma
     *                      ISOException é lançada
     */
    public String mti() throws ISOException {
        return message.getMTI();
    }

    /**
     * Verifica se o campo está presente na mensagem.
     *
     * @param index O índice de identificação do campo DE
     * @return TRUE se o índice está presente, FALSE caso contrário
     */
    public boolean hasDe(int index) {
        return message.hasField(index);
    }

    /**
     * Verifica se o campo é um PDS.
     *
     * @return TRUE se for um PDS, FALSE caso contrário
     */
    public boolean isPds() {
        return pds == null && !isCorrupted();
    }

    /**
     * Verifica se a mensagem ISO 8583 está corrompida.
     *
     * @return TRUE se a mensagem estiver corrompida, FALSE caso contrário
     */
    public boolean isCorrupted() {
        return message.isCorrupted();
    }

    /**
     * Retorna a Exception de uma mensagem corrompida.
     *
     * @return a Exception caso a mensagem esteja corrompida, ou null se a mensagem for íntegra
     */
    public Exception error() {
        return message.getError();
    }

    /**
     * Retorna o campo DE com o índice informado.
     *
     * @param index O índice de identificação do campo
     * @return O campo DE indicado pelo índice
     * @throws ISOException Se o valor do campo DE não é válido ou não for possível extrair as informações do campo, uma
     *                      ISOException é lançada
     */
    public ISODe de(int index) throws ISOException {
        return (hasDe(index)) ? new ISODe(this, index, message.getValue(index)) : new ISODe(this, index);
    }

    /**
     * Retorna o campo PDS com o índice informado.
     *
     * @param index O índice de identificação do campo
     * @return O campo PDS indicado pelo índice
     * @throws ISOException Se o valor do campo PDS não é válido ou não for possível extrair as informações do campo, uma
     *                      ISOException é lançada
     */
    public ISODe pds(int index) throws ISOException {
        return (pds != null) ? pds.de(index) : new ISODe(index);
    }

    /**
     * Retorna a mensagem em seu formato original antes da extração em byte[].
     *
     * @return a mensagem ISO 8583 em byte[]
     * @throws ISOException Se ocorrer algum erro ao converter a mensagem ao seu valor original, uma ISOException é lançada
     */
    public byte[] pack() throws ISOException {
        return isCorrupted() ? error().toString().getBytes() : message.pack();
    }

    /**
     * Imprime a saída do conteúdo da mensagem no formato XML no stream definido pelo parâmetro de entrada.
     *
     * @param stream Stream para saída do output onde o resultado deve ser impresso
     */
    public void dump(PrintStream stream) {
        stream.println(xml("\t"));
    }

    /**
     * Retorna o conteúdo da mensagem ISO 8583 convertida para XML.
     *
     * @return conversão do conteúdo da mensagem ISO 8583 para XML
     */
    public String xml(String ident) {
        if (StringUtils.isEmpty(ident)) {
            ident = "\t";
        }

        StringBuilder output = new StringBuilder(ident);
        output.append((isPds() && !isCorrupted() ? "<de id=\"" + PDS_INDEX + "\">" : "<message>"));

        for (int i = 0; i <= message.getMaxField(); i++) {
            try {
                output.append(generateXmlOutput(ident, i));
            } catch (ISOException ignore) {
            }
        }

        return output.append("\n").append(ident).append((isPds() ? "</de>" : "</message>")).toString();
    }

    private String generateXmlOutput(String ident, int i) throws ISOException {
        return (i == PDS_INDEX && pds != null) ? convertPdsToXml(ident) : convertDeToXml(ident, i);
    }

    private String convertDeToXml(String ident, int i) throws ISOException {
        return hasDe(i) ? "\n" + ident + de(i).xml(ident) : "";
    }

    private String convertPdsToXml(String ident) {
        return "\n" + pds.xml(ident + ident);
    }

}
