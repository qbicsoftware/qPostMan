package life.qbic.model.dataLoading;

import ch.ethz.sis.openbis.generic.asapi.v3.IApplicationServerApi;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.DataSet;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.id.DataSetPermId;
import ch.ethz.sis.openbis.generic.dssapi.v3.IDataStoreServerApi;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.download.DataSetFileDownloadOptions;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.id.DataSetFilePermId;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.id.IDataSetFileId;
import life.qbic.io.commandline.PostmanCommandLineOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QbicDataStreamProvider {

    private final static Logger LOG = LogManager.getLogger(QbicDataStreamProvider.class);

    private IApplicationServerApi applicationServer;

    private IDataStoreServerApi dataStoreServer;

    private String sessionToken;

    private String filterType;

    private final int defaultBufferSize;

    public QbicDataStreamProvider(IApplicationServerApi applicationServer, IDataStoreServerApi dataStoreServer, String sessionToken, String filterType, int defaultBufferSize) {
        this.applicationServer = applicationServer;
        this.dataStoreServer = dataStoreServer;
        this.sessionToken = sessionToken;
        this.filterType = filterType;
        this.defaultBufferSize = defaultBufferSize;
    }

    /**
     * Provides Inputstreams for IDs
     * checks whether any filtering option (suffix or regex) has been passed and applies filtering if needed
     *
     * @param commandLineParameters
     */
    public InputStream provideInputStreamForIds(PostmanCommandLineOptions commandLineParameters) {
        QbicDataFinder qbicDataFinder = new QbicDataFinder(applicationServer,
                dataStoreServer,
                sessionToken,
                filterType);

        LOG.info(String.format("%s provided openBIS identifiers have been found: %s",
                commandLineParameters.ids.size(), commandLineParameters.ids.toString()));

        // a suffix was provided -> only provide stream for files which contain the suffix string
        if (!commandLineParameters.suffixes.isEmpty()) {
            List<InputStream> inputStreams = new ArrayList<>();

            for (String ident : commandLineParameters.ids) {
                LOG.info(String.format("Downloading files for provided identifier %s", ident));
                List<IDataSetFileId> foundSuffixFilteredIDs = qbicDataFinder.findAllSuffixFilteredIDs(ident, commandLineParameters.suffixes);

                LOG.info(String.format("Number of files found: %s", foundSuffixFilteredIDs.size()));

                inputStreams.add(getDatasetStreamFromFilteredIds(foundSuffixFilteredIDs));
            }

            return new SequenceInputStream(Collections.enumeration(inputStreams));

            // a regex pattern was provided -> only provide stream for files which contain the regex pattern
        } else if (!commandLineParameters.regexPatterns.isEmpty()) {
            List<InputStream> inputStreams = new ArrayList<>();

            for (String ident : commandLineParameters.ids) {
                LOG.info(String.format("Downloading files for provided identifier %s", ident));
                List<IDataSetFileId> foundRegexFilteredIDs = qbicDataFinder.findAllRegexFilteredIDs(ident, commandLineParameters.regexPatterns);

                LOG.info(String.format("Number of files found: %s", foundRegexFilteredIDs.size()));

                inputStreams.add(getDatasetStreamFromFilteredIds(foundRegexFilteredIDs));
            }

            return new SequenceInputStream(Collections.enumeration(inputStreams));

            // no suffix or regex was supplied -> provide stream for all datasets
        } else {
            List<InputStream> inputStreams = new ArrayList<>();

            for (String ident : commandLineParameters.ids) {
                LOG.info(String.format("Downloading files for provided identifier %s", ident));
                List<DataSet> foundDataSets = qbicDataFinder.findAllDatasetsRecursive(ident);

                LOG.info(String.format("Number of datasets found: %s", foundDataSets.size()));

                inputStreams.add(getDatasetStreamFromDatasetList(foundDataSets));
            }

            return new SequenceInputStream(Collections.enumeration(inputStreams));
        }
    }

    /**
     * Provides an InputStream files that have been found after filtering for suffixes/regexes by a list of supplied IDs
     *
     * @param filteredIDs
     * @return exitcode
     */
    private InputStream getDatasetStreamFromFilteredIds(List<IDataSetFileId> filteredIDs) {
        List<InputStream> inputStreams = new ArrayList<>();

        for (IDataSetFileId id : filteredIDs) {
            DataSetFileDownloadOptions options = new DataSetFileDownloadOptions();
            options.setRecursive(true);
            InputStream stream = this.dataStoreServer.downloadFiles(sessionToken, Collections.singletonList(id), options);
            inputStreams.add(stream);
        }

        return new SequenceInputStream(Collections.enumeration(inputStreams));
    }

    /**
     * Provides an InputStream for a given list of datasets
     * There was no filtering applied here!
     *
     * @param dataSetList A list of datasets
     * @return InputStream
     */
    private InputStream getDatasetStreamFromDatasetList(List<DataSet> dataSetList) {
        List<InputStream> inputStreams = new ArrayList<>();

        for (DataSet dataset : dataSetList) {
            DataSetPermId permID = dataset.getPermId();
            DataSetFileDownloadOptions options = new DataSetFileDownloadOptions();
            IDataSetFileId fileId = new DataSetFilePermId(new DataSetPermId(permID.toString()));
            options.setRecursive(true);
            InputStream stream = this.dataStoreServer.downloadFiles(sessionToken, Collections.singletonList(fileId), options);
            inputStreams.add(stream);
        }

        return new SequenceInputStream(Collections.enumeration(inputStreams));
    }
}
