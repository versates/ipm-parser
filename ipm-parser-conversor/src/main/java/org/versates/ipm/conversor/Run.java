package org.versates.ipm.conversor;

import org.versates.ipm.conversor.action.Action;
import org.versates.ipm.conversor.action.ConversorAction;
import org.versates.ipm.conversor.action.EncodingAction;
import org.versates.ipm.conversor.action.HelpAction;
import org.versates.ipm.iso.packager.ISOLayout;
import org.apache.commons.cli.*;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import static org.versates.ipm.helper.Assertion.isTrue;

/**
 * Classe de execução do cliente de conversão de arquivos IPM para outros formatos.
 *
 * @author Ramses Vidor
 */
public enum Run {
    SINGLETON;

    private static final CommandLineParser PARSER = new PosixParser();
    private static final Map<String, Action> ACTIONS = new HashMap<String, Action>();
    private static final EncodingAction ENCODING_ACTION = new EncodingAction();
    private static final String ASK_HELP = "Utilize -h para ajuda.";
    private static final String VALIDATION_MESSAGE_FILE = "Informe o caminho do arquivo. " + ASK_HELP;
    private static final String HELP_ACTION_MESSAGE = "Ajuda!";
    private static final String CONVERSOR_ACTION_MESSAGE = "Caminho do arquivo a ser convertido.";
    private static final String ENCODING_ACTION_MESSAGE = "Codificação/formato do arquivo IPM. Opções válidas: ASCII ou EBCDIC";
    private static final Options OPTIONS = new Options();

    static {
        registerCommandOption(new Option("e", ENCODING_ACTION_MESSAGE), ENCODING_ACTION);
        registerCommandOption(new Option("f", CONVERSOR_ACTION_MESSAGE), new ConversorAction());
        registerCommandOption(new Option("h", HELP_ACTION_MESSAGE), new HelpAction(OPTIONS));
    }

    /**
     * Método de execução do cliente.
     *
     * @param arguments Argumentos passados para o programa
     */
    public static void main(String[] arguments) {
        final PrintWriter writer = new PrintWriter(System.out);

        try {
            final CommandLine commandLine = PARSER.parse(OPTIONS, arguments);
            validateCommandLine(commandLine);
            execute(commandLine);
        } catch (final Exception e) {
            writer.print(e.getMessage());
        }

        writer.flush();
        writer.close();
    }

    /**
     * Retorna o layout ISO 8583 configurado para a conversão do arquivo IPM.
     *
     * @return layout ISO 8583 a ser utilizado para conversão
     */
    public static ISOLayout getLayout() {
        return ENCODING_ACTION.layout();
    }

    private static void execute(CommandLine commandLine) {
        if (commandLine.hasOption("e")) {
            ACTIONS.get("e").execute(commandLine.getOptionValue("e"));
        }

        ACTIONS.get("f").execute(commandLine.getOptionValue("f"));
    }

    private static void registerCommandOption(final Option option, final Action action) {
        OPTIONS.addOption(option);
        ACTIONS.put(option.getOpt(), action);
    }

    private static void validateCommandLine(final CommandLine commandLine) {
        isTrue(commandLine.getOptions().length != 0, ASK_HELP);
        isTrue(commandLine.hasOption("f"), VALIDATION_MESSAGE_FILE);
    }

}
