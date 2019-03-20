package org.versates.ipm.parser;

import org.versates.ipm.iso.ISOMessage;
import org.versates.ipm.iso.packager.ISOLayout;
import org.versates.ipm.iso.packager.ISOLayoutNotFoundException;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;
import org.jpos.iso.packager.GenericPackager;

import java.io.InputStream;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.List;

/**
 * Classe principal de processamento dos arquivos IPM.
 *
 * @author Ramses Vidor
 */
public abstract class IPMFileParser implements Serializable {

    private static final long serialVersionUID = 5695555652184529809L;

    private final ISOLayout layout;
    private final ISOPackager packager;

    /**
     * Processa o conteúdo do arquivo IPM em <code>byte[]</code>, gerando uma lista de objetos ISOMessage.
     *
     * @param bytes O conteúdo do arquivo IPM
     * @return A lista de ISOMessage extraída do arquivo IPM
     * @throws IPMParserException Se houver qualquer falha no processo de extração de mensagens do arquivo IPM, uma exceção
     *                            IPMParserException é lan~cada
     */
    abstract public List<ISOMessage> parse(byte[] bytes) throws IPMParserException;

    /**
     * Constrói uma instância desse objeto para processamento de arquivos no layout definido.
     *
     * @param layout O layout do arquivo a ser processado
     */
    public IPMFileParser(ISOLayout layout) {
        this.layout = layout;
        packager = createPackager();
    }

    /**
     * Retorna o layout configurado para processamento do arquivo IPM.
     *
     * @return layout ISO 8583 utilizado
     */
    public ISOLayout getLayout() {
        return layout;
    }

    /**
     * Retorna o packager utilizado para extração das mensagens ISO 8583.
     *
     * @return o packager utilizado para extração
     */
    public ISOPackager getPackager() {
        return packager;
    }

    /**
     * Cria um parser com base no layout ISO 8583 definido.
     *
     * @param layout Layout ISO 8583 para criação do parser
     * @return uma instância de parser para extração de mensagens ISO 8583
     */
    public static IPMFileParser createFileParser(ISOLayout layout) {
        switch (layout) {
            case MASTERCARD_ASCII:
                return createMasterCardPreEditFileParser();
            case MASTERCARD_EBCDIC:
                return createMasterCardEbcdicFileParser();
            default:
                return createGenericFileParser();
        }
    }

    /**
     * Cria um parser configurado com layout ISO 8583 para arquivos IPM MasterCard, codificação ASCII (
     * <code>ISOLayout.MASTERCARD_ASCII</code>).
     *
     * @return uma instância de parser para extração de mensagens ISO 8583 MasterCard ASCII
     */
    public static IPMFileParser createMasterCardPreEditFileParser() {
        return new IPMMasterCardPreEditFileParser();
    }

    /**
     * Cria um parser configurado com layout ISO 8583 para arquivos IPM MasterCard, codificação ASCII (
     * <code>ISOLayout.MASTERCARD_EBCDIC</code>).
     *
     * @return uma instância de parser para extração de mensagens ISO 8583 MasterCard EBCDIC
     */
    public static IPMFileParser createMasterCardEbcdicFileParser() {
        return new IPMMasterCardEbcdicFileParser();
    }

    /**
     * Cria um parser configurado com layout ISO 8583 para arquivos IPM genéricos (<code>ISOLayout.GENERIC</code>).
     *
     * @return uma instância de parser para extração de mensagens ISO 8583 MasterCard ASCII
     */
    public static IPMFileParser createGenericFileParser() {
        return new IPMGenericFileParser();
    }

    /**
     * Cria uma mensagem ISO para o packager em uso.
     *
     * @return A ISOMessage criada para o packager
     */
    protected ISOMessage createMessage() {
        final ISOMessage message = new ISOMessage();
        message.setPackager(getPackager());
        return message;
    }

    /**
     * Preenche informações padrão de mensagem corrompida.
     *
     * @param message A mensagem corrompida
     * @param e       A exceção lançada na tentativa de extrair a mensagem corrompida
     * @return Uma instância ISOMsg clonada da mensagem corrompida
     */
    protected ISOMsg feedCorruptedMessage(ISOMessage message, Exception e) {
        try {
            message.set(1, MessageFormat.format("corrupted [{0}]", e.getMessage()));
            message.setError(e);
        } catch (ISOException ignore) {
        }

        return (ISOMsg) message.clone();
    }

    private ISOPackager createPackager() {
        try {
            InputStream configuration = getClass().getClassLoader().getResourceAsStream(layout.getConfigurationPath());

            if (configuration == null) {
                configuration = getClass().getClassLoader().getResourceAsStream("/" + layout.getConfigurationPath());
            }

            return new GenericPackager(configuration);
        } catch (ISOException e) {
            throw new ISOLayoutNotFoundException(e);
        }
    }

}
