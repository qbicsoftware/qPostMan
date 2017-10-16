package life.qbic;

import ch.ethz.sis.openbis.generic.asapi.v3.IApplicationServerApi;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.common.search.SearchResult;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.DataSet;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.fetchoptions.DataSetFetchOptions;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.id.DataSetPermId;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.Sample;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.fetchoptions.SampleFetchOptions;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.search.SampleSearchCriteria;
import ch.ethz.sis.openbis.generic.dssapi.v3.IDataStoreServerApi;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.download.DataSetFileDownload;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.download.DataSetFileDownloadOptions;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.download.DataSetFileDownloadReader;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.id.DataSetFilePermId;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.id.IDataSetFileId;
import ch.systemsx.cisd.common.spring.HttpInvokerUtils;
import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class QbicDataLoader {

    private String AppServerUri;

    private String DataServerUri;

    private String user;

    private String password;

    private IApplicationServerApi applicationServer;

    private static Logger log = LogManager.getLogger(QbicDataLoader.class);

    private String sessionToken;

    public QbicDataLoader(String AppServerUri, String DataServerUri,
                                         String user, String password){
        this.AppServerUri = AppServerUri;
        this.DataServerUri = DataServerUri;
        if (!AppServerUri.isEmpty()){
            this.applicationServer = HttpInvokerUtils.createServiceStub(
                    IApplicationServerApi.class,
                    this.AppServerUri + IApplicationServerApi.SERVICE_URL, 10000);
        } else {
            this.applicationServer = null;
        }
        this.setCredentials(user, password);
    }


    public QbicDataLoader setCredentials(String user, String password) {
        this.user = user;
        this.password = password;
        return this;
    }

    public int testConnection() {
        try {
            String sessionToken = this.applicationServer.login(this.user, this.password);
        } catch (Exception exc) {
            log.error(String.format("Connection to the application server %s failed.", this.AppServerUri));
            log.error(exc);
            return 1;
        }
        return 0;
    }


    public List<DataSet> findDatasets(String sampleId) {


        SampleSearchCriteria criteria = new SampleSearchCriteria();
        criteria.withCode().thatEquals("QICGC0001AE");

        // tell the API to fetch all descendents for each returned sample
        SampleFetchOptions fetchOptions = new SampleFetchOptions();
        DataSetFetchOptions dsFetchOptions = new DataSetFetchOptions();
        fetchOptions.withChildrenUsing(fetchOptions);
        fetchOptions.withDataSetsUsing(dsFetchOptions);
        SearchResult<Sample> result = applicationServer.searchSamples(sessionToken, criteria, fetchOptions);
        System.out.println(result.getTotalCount());

        // get all datasets of sample with provided sample code and all descendents
        List<DataSet> foundDatasets = new ArrayList<DataSet>();
        for (Sample sample : result.getObjects()) {
            foundDatasets.addAll(sample.getDataSets());
            System.out.println(sample.getDataSets());
            for (Sample desc : sample.getChildren()) {
                System.out.println(desc.getDataSets());
                foundDatasets.addAll(desc.getDataSets());
            }
        }

        return foundDatasets;
    }
}
        /*
        // Reference the DSS
        IDataStoreServerApi dataStoreServer = HttpInvokerUtils.createStreamSupportingServiceStub(
                IDataStoreServerApi.class, this.DataServerUri + IDataStoreServerApi.SERVICE_URL, 10000);
        // Download the files of found datasets
        System.out.println(foundDatasets.size());
        for (DataSet dataset : foundDatasets) {
            DataSetPermId permID = dataset.getPermId();
            System.out.println(permID.toString());

            DataSetFileDownloadOptions options = new DataSetFileDownloadOptions();
            IDataSetFileId fileId = new DataSetFilePermId(new DataSetPermId(permID.toString()));
            options.setRecursive(true);
            InputStream stream = dss.downloadFiles(sessionToken, Arrays.asList(fileId), options);
            DataSetFileDownloadReader reader = new DataSetFileDownloadReader(stream);
            DataSetFileDownload file = null;

            while ((file = reader.read()) != null) {
                InputStream initialStream = file.getInputStream();

                if(file.getDataSetFile().getFileLength() > 0) {
                    String[] splitted = file.getDataSetFile().getPath().split("/");
                    String lastOne = splitted[splitted.length - 1];
                    OutputStream os = new FileOutputStream("/home/sven1103/Downloads/" + lastOne);

                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    //read from is to buffer
                    while ((bytesRead = initialStream.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                    initialStream.close();
                    //flush OutputStream to write any buffered data to file
                    os.flush();
                    os.close();
                }

                //System.out.println("Downloaded " + file.getDataSetFile().getPath() + " "
                //  + file.getDataSetFile().getFileLength());
                //while ((outfile = initialStream.read()) != null){

*/

/*
          String[] splitted = file.getDataSetFile().getPath().split("/");
          String lastOne = splitted[splitted.length-1];
          File targetFile = new File("/home/sven1103/Downloads/" +  lastOne);
          OutputStream outStream = new FileOutputStream(targetFile);
          outStream.write(buffer);
          */

