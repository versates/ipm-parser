package org.versates.ipm;

import org.versates.ipm.helper.XMLConversor;
import org.versates.ipm.iso.ISOMessage;
import org.versates.ipm.iso.ISOTransaction;
import org.versates.ipm.parser.IPMFileParser;
import org.versates.ipm.parser.IPMParserException;
import org.apache.commons.lang.StringUtils;
import org.jpos.iso.ISOException;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static java.text.MessageFormat.format;

/**
 * Classe de representação de arquivo de Integrated Products Messages (IPM). A compensação por meio do formato de
 * Mensagens de Produtos Integrados (IPM) é o movimento de informações referentes à transação enviado pelo membro ao
 * sistema de compensação. Cada arquivo IPM contém um grupo de mensagens ISO 8583, os quais apresentam essas informações
 * de transação.
 *
 * @author Ramses Vidor
 */
public class IPMFile implements Serializable {

    private static final long serialVersionUID = -3141984967806646322L;
    private static final int FUNCTION_CODE_DE = 24;
    private static final int CICLE_ID_DE = 63;
    private static final String HEADER_MTI = "1644";
    private static final String FUNCTION_CODE_HEADER = "697";
    private static final String FUNCTION_CODE_FOOTER = "695";

    private ISOTransaction header;
    private ISOTransaction footer;
    private final String name;
    private final List<ISOTransaction> transactions = new ArrayList<ISOTransaction>();

    /**
     * Constrói uma instância de arquivo IPM.
     *
     * @param name   O nome do arquivo IPM
     * @param bytes  Conteúdo do arquivo IPM em bytes
     * @param parser O parser a ser utilizado para interpretar o arquivo IPM. Deve ser compatível com a codificação e o
     *               layout do arquivo IPM a ser processado
     * @throws IPMParserException Exceção lançada se o arquivo IPM não corresponder aos critérios de processamento.
     */
    public IPMFile(String name, byte[] bytes, IPMFileParser parser) throws IPMParserException {
        if (StringUtils.isEmpty(name)) {
            throw new IPMParserException("ipmparser.file.invalidfilename");
        }

        this.name = name;
        extractTransactions(bytes, parser);
    }

    /**
     * Retorna o nome do arquivo IPM físico.
     *
     * @return o nome do arquivo IPM
     */
    public String name() {
        return name;
    }

    /**
     * Retorna a mensagem de cabeçalho de arquivo (primeira mensagem ISO 8583 apresentada no arquivo), a qual apresenta
     * informações referentes ao arquivo. O cabeçalho de arquivo é identificado pelo MTI 1644, código de função (DE 24)
     * 697.
     *
     * @return cabeçalho do arquivo IPM
     */
    public ISOTransaction header() {
        return header;
    }

    /**
     * Retorna a mensagem de rodapé de arquivo (última mensagem ISO 8583 apresentada no arquivo), a qual apresenta
     * informações referentes ao arquivo. O rodapé de arquivo é identificado pelo MTI 1644, código de função (DE 24)
     * 695.
     *
     * @return rodapé do arquivo IPM
     */
    public ISOTransaction footer() {
        return footer;
    }

    /**
     * Retorna o total de mensagens ISO 8583 contidas no arquivo IPM, não contabilizando as mensagens de cabeçalho e
     * rodapé.
     *
     * @return total de mensagens ISO 8583 contidas no arquivo IPM
     */
    public int countTransactions() {
        return transactions.size();
    }

    /**
     * Retorna as mensagens ISO 8583 contidas no arquivo IPM, com exceção das mensagens de cabeçalho e rodapé.
     *
     * @return mensagens ISO 8583 contidas no arquivo IPM
     */
    public List<ISOTransaction> transactions() {
        return transactions;
    }

    /**
     * Imprime a saída do conteúdo do arquivo no formato XML no stream definido pelo parâmetro de entrada.
     *
     * @param printStream Stream para saída do output onde o resultado deve ser impresso
     */
    public void dump(PrintStream printStream) {
        printStream.println(xml());
    }

    /**
     * Retorna o conteúdo do arquivo IPM convertido para XML.
     *
     * @return conversão do conteúdo do arquivo IPM para XML
     */
    public String xml() {
        StringBuilder output = new StringBuilder(openXmlTag() + infoAsXml(header, "header"));

        for (ISOTransaction transaction : transactions) {
            output.append("\n").append(transaction.xml("\t"));
        }

        return output.append("\n").append(infoAsXml(footer, "footer")).append(closeXmlTag()).toString();
    }

    private void extractTransactions(byte[] bytes, IPMFileParser parser) throws IPMParserException {
        for (ISOMessage message : parser.parse(bytes)) {
            ISOTransaction tx = new ISOTransaction(message);
            if (isHeader(tx)) {
                header = tx;
            } else if (isFooter(tx)) {
                footer = tx;
            } else if (isValidTransaction(tx) || tx.isCorrupted()) {
                transactions.add(tx);
            }
        }

        if (header == null) {
            throw new IPMParserException("ipmparser.file.noheader");
        }
    }

    private boolean isValidTransaction(ISOTransaction tx) {
        return tx.hasDe(CICLE_ID_DE);
    }

    private boolean isHeader(ISOTransaction tx) throws IPMParserException {
        return isHeaderMessage(tx, FUNCTION_CODE_HEADER);
    }

    private boolean isFooter(ISOTransaction tx) throws IPMParserException {
        return isHeaderMessage(tx, FUNCTION_CODE_FOOTER);
    }

    private boolean isHeaderMessage(ISOTransaction tx, String functionCode) throws IPMParserException {
        try {
            return HEADER_MTI.equals(tx.mti()) && functionCode.equals(tx.de(FUNCTION_CODE_DE).value());
        } catch (ISOException e) {
            throw new IPMParserException(e);
        }
    }

    private String infoAsXml(ISOTransaction info, String type) {
        return info.xml("\t").replaceAll("<(/)?message>", "<$1" + type + ">");
    }

    private String openXmlTag() {
        return format(XMLConversor.HEADER_TAG, name(), countTransactions());
    }

    private String closeXmlTag() {
        return XMLConversor.FOOTER_TAG;
    }

}
