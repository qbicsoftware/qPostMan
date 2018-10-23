package life.qbic.io.login;

import ch.ethz.sis.openbis.generic.asapi.v3.IApplicationServerApi;
import ch.ethz.sis.openbis.generic.dssapi.v3.IDataStoreServerApi;
import ch.systemsx.cisd.common.spring.HttpInvokerUtils;
import life.qbic.io.commandline.OpenBISPasswordParser;
import life.qbic.io.commandline.PostmanCommandLineOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Singleton to provide access to OpenBIS openBISAuthentication functionality
 */
public class PostmanSessionManager {

    private final static Logger LOG = LogManager.getLogger(PostmanSessionManager.class);

    private static final PostmanSessionManager POSTMAN_SESSION_MANAGER = new PostmanSessionManager();
    private IApplicationServerApi applicationServer;
    private IDataStoreServerApi dataStoreServer;
    private String sessionToken;
    private int buffersize;

    /**
     * private, since we don't want any initialization to happen -> singleton pattern is used
     */
    private PostmanSessionManager() {

    }

    public static PostmanSessionManager getPostmanSessionManager() {
        return POSTMAN_SESSION_MANAGER;
    }

    /**
     * log into openBIS
     *
     * @param commandLineParameters
     */
    public void loginToOpenBIS(PostmanCommandLineOptions commandLineParameters) {
        System.out.format("Please provide password for user \'%s\':\n", commandLineParameters.user);

        String password = OpenBISPasswordParser.readPasswordFromInputStream();

        if (password.isEmpty()) {
            System.out.println("You need to provide a password.");
            System.exit(1);
        }

        PostmanSessionManager postmanSessionManager = PostmanSessionManager.getPostmanSessionManager();
        postmanSessionManager.setApplicationServer(commandLineParameters.as_url);
        postmanSessionManager.setDataStoreServer(commandLineParameters.dss_url);

        // authenticate at openBIS and verify
        int returnCode = postmanSessionManager.openBISAuthentication(postmanSessionManager.getApplicationServer(), commandLineParameters.user, password);
        LOG.info(String.format("OpenBis authentication returned with %s", returnCode));
        if (returnCode != 0) {
            LOG.error("Connection to openBIS failed.");
            System.exit(1);
        }

        LOG.info("Connection to openBIS was successful.");
    }

    /**
     * Login method for openBIS authentication
     * @return 0 if successful, 1 else
     */
    private int openBISAuthentication(IApplicationServerApi applicationServer, String user, String password) {
        try {
            String sessionTokenReturned = applicationServer.login(user, password);
            applicationServer.getSessionInformation(sessionTokenReturned);

            sessionToken = sessionTokenReturned;
            return 0;
        } catch (AssertionError | Exception err) {
            LOG.debug(err);

            sessionToken = "";
            return -1;
        }
    }

    /**
     * TODO
     * @param applicationServerURL
     */
    private void setApplicationServer(String applicationServerURL) {
        if (!applicationServerURL.isEmpty()) {
            applicationServer = HttpInvokerUtils.createServiceStub(
                    IApplicationServerApi.class,
                    applicationServerURL + IApplicationServerApi.SERVICE_URL, 10000);
        } else {
            applicationServer = null;
        }
    }

    /**
     * TODO
     * @param dataStoreServerURL
     */
    private void setDataStoreServer(String dataStoreServerURL) {
        if (!dataStoreServerURL.isEmpty()) {
            dataStoreServer = HttpInvokerUtils.createStreamSupportingServiceStub(
                    IDataStoreServerApi.class,
                    dataStoreServerURL + IDataStoreServerApi.SERVICE_URL, 10000);
        } else {
            dataStoreServer = null;
        }
    }

    public IApplicationServerApi getApplicationServer() {
        return applicationServer;
    }

    public IDataStoreServerApi getDataStoreServer() {
        return dataStoreServer;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public int getBuffersize() {
        return buffersize;
    }

    public void setBuffersize(int buffersize) {
        this.buffersize = buffersize;
    }



}
