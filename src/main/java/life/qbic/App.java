package life.qbic;

import java.io.IOException;

import life.qbic.io.login.PostmanSessionManager;
import life.qbic.model.dataLoading.QbicDataDownloader;
import life.qbic.io.commandline.CommandLineParser;
import life.qbic.io.commandline.PostmanCommandLineOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * postman for staging data from openBIS
 */
public class App {

    private final static Logger LOG = LogManager.getLogger(App.class);

    public static void main(String[] args) throws IOException {
        // parse and verify all commandline parameters
        PostmanCommandLineOptions commandLineParameters = CommandLineParser.parseAndVerifyCommandLineParameters(args);

        // openBISAuthentication to OpenBIS
        PostmanSessionManager postmanSessionManager = PostmanSessionManager.getPostmanSessionManager();
        postmanSessionManager.loginToOpenBIS(commandLineParameters);

        QbicDataDownloader qbicDataDownloader = new QbicDataDownloader(postmanSessionManager.getApplicationServer(),
                postmanSessionManager.getDataStoreServer(),
                commandLineParameters.bufferMultiplier + 1024,
                commandLineParameters.datasetType);
        qbicDataDownloader.setSessionToken(postmanSessionManager.getSessionToken());

        // download all requested files by the user
        qbicDataDownloader.downloadRequestedFilesOfDatasets(commandLineParameters, qbicDataDownloader);
    }

}


