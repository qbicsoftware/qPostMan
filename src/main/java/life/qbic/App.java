package life.qbic;

import life.qbic.core.PostmanFilterOptions;
import life.qbic.core.SupportedFileTypes;
import life.qbic.core.authentication.PostmanConfig;
import life.qbic.core.authentication.PostmanSessionManager;
import life.qbic.dataLoading.PostmanDataDownloaderOldAPI;
import life.qbic.dataLoading.PostmanDataDownloaderV3;
import life.qbic.dataLoading.PostmanDataFinder;
import life.qbic.exceptions.PostmanOpenBISLoginFailedException;
import life.qbic.io.commandline.CommandLineParser;
import life.qbic.io.commandline.OpenBISPasswordParser;
import life.qbic.io.commandline.PostmanCommandLineOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;


/**
 * postman for staging data from openBIS
 */
public class App {

    private final static Logger LOG = LogManager.getLogger(App.class);

    public static void main(String[] args) {
        try {
            PostmanCommandLineOptions postmanCommandLineOptions = CommandLineParser.parseAndVerifyCommandLineParameters(args);

            String password = OpenBISPasswordParser.readPasswordFromInputStream(postmanCommandLineOptions.user);
            PostmanConfig postmanConfig = new PostmanConfig(postmanCommandLineOptions.user,
                                                            password,
                                                            postmanCommandLineOptions.as_url,
                                                            postmanCommandLineOptions.dss_url);

            PostmanSessionManager postmanSessionManager = PostmanSessionManager.getPostmanSessionManager();
            postmanSessionManager.loginToOpenBIS(postmanConfig);

            PostmanFilterOptions postmanFilterOptions = new PostmanFilterOptions();
            if (!postmanCommandLineOptions.suffixes.isEmpty()) { postmanFilterOptions.setSuffixes(postmanCommandLineOptions.suffixes); }
            if (!postmanCommandLineOptions.regexPatterns.isEmpty()) { postmanFilterOptions.setRegexPatterns(postmanCommandLineOptions.regexPatterns); }
            if (!postmanCommandLineOptions.datasetType.isEmpty()) { postmanFilterOptions.setFileType(postmanCommandLineOptions.datasetType); }
            if (!postmanCommandLineOptions.datasetCodes.isEmpty()) { postmanFilterOptions.setDatasetCodes(postmanCommandLineOptions.datasetCodes); }

            PostmanDataFinder postmanDataFinder = new PostmanDataFinder(postmanSessionManager.getApplicationServer(),
                                                                        postmanSessionManager.getDataStoreServer(),
                                                                        postmanSessionManager.getSessionToken());

            // Use old API? Likely quicker, but won't be around forever!
            if (postmanCommandLineOptions.old) {
                PostmanDataDownloaderOldAPI postmanDataDownloaderOldAPI = new PostmanDataDownloaderOldAPI(postmanCommandLineOptions.as_url,
                                                                                                          postmanSessionManager.getSessionToken());
                postmanDataDownloaderOldAPI.downloadRequestedFilesOfDatasets(postmanCommandLineOptions.ids,
                                                                             postmanFilterOptions,
                                                                             postmanDataFinder,
                                                                             postmanCommandLineOptions.outputPath);
            } else {
                PostmanDataDownloaderV3 postmanDataDownloaderV3 = new PostmanDataDownloaderV3(postmanSessionManager.getDataStoreServer(),
                                                                                              postmanSessionManager.getSessionToken());
                postmanDataDownloaderV3.downloadRequestedFilesOfDatasets(postmanCommandLineOptions.ids,
                                                                         postmanFilterOptions,
                                                                         postmanDataFinder,
                                                                         postmanCommandLineOptions.outputPath);
            }
        } catch (IOException e) {
            LOG.error("Commandline options could not be parsed correctly! Error message: ", e.getMessage());
        } catch (PostmanOpenBISLoginFailedException e) {
            LOG.error("Logging into OpenBIS failed! Error message: ", e.getMessage());
        }

    }

}


