package life.qbic.io.commandline;

import life.qbic.core.SupportedFileTypes;
import life.qbic.io.parser.IdentifierParser;
import picocli.CommandLine;

import java.io.IOException;

public class CommandLineParser {

    /**
     * parses all passed CLI parameters
     * Prints help menu if no commandline parameters were passed
     * verifies whether all mandatory commandline parameters have been passed (IDs and username)
     *
     * also reads the provided identifiers if they were passed as a file
     *
     * @param args
     * @return
     * @throws IOException
     */
    public static PostmanCommandLineOptions parseAndVerifyCommandLineParameters(String[] args) throws IOException {
        if (args.length == 0) {
            CommandLine.usage(new PostmanCommandLineOptions(), System.out);
            System.exit(0);
        }

        PostmanCommandLineOptions commandLineParameters = new PostmanCommandLineOptions();
        new CommandLine(commandLineParameters).parse(args);

        if (commandLineParameters.helpRequested) {
            CommandLine.usage(new PostmanCommandLineOptions(), System.out);
            System.exit(0);
        }

        // user wants to see the list of all supported dataset types for filtering
        if (commandLineParameters.datasetTypeHelp) {
            SupportedFileTypes.printSupportedFileTypes();
            System.exit(0);
        }

        if ((commandLineParameters.ids == null || commandLineParameters.ids.isEmpty()) && commandLineParameters.sampleIDsFilePath == null) {
            System.out.println("You have to provide one ID as command line argument or a file containing IDs.");
            System.exit(1);
        } else if ((commandLineParameters.ids != null) && (commandLineParameters.sampleIDsFilePath != null)) {
            System.out.println("Arguments --identifier and --file are mutually exclusive, please provide only one.");
            System.exit(1);
        } else if (commandLineParameters.sampleIDsFilePath != null) {
            commandLineParameters.ids = IdentifierParser.readProvidedIdentifiers(commandLineParameters.sampleIDsFilePath.toFile());
        }

        return commandLineParameters;
    }
}
