package life.qbic.io.commandline;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;


@Command(name = "Postman",
        footer = "Optional: specify a config file by running postman with '@/path/to/config.txt'. Details can be found in the README.",
        description = "A client software for dataset downloads from QBiC's data management system openBIS.")
public class PostmanCommandLineOptions {

    @Option(names = {"-old", "--old"},
            description = "Use the old, temporary, but faster API. Not all features may always be available or supported!!")
    public boolean old;

    @Option(names = {"-u", "--user"},
            description = "openBIS user name")
    public String user;

    // this consumes all parameters that are not labeled!
    @Parameters(paramLabel = "SAMPLE_ID",
            description = "one or more QBiC sample ids")
    public List<String> ids;

    @Option(names = {"-f", "--file"},
            description = "a file with line-separated list of QBiC sample ids")
    public Path sampleIDsFilePath;

    @Option(names = {"-o", "--output"},
            description = "output path for all downloaded files")
    public String outputPath;

    @Option(names = {"-b", "--buffer-size"},
            description = "dataset download performance can be improved by increasing this value with a multiple of 1024 (default)."
            + " Only change this if you know what you are doing.")
    public int bufferMultiplier = 1;

    @Option(names = {"-s", "--suffix"},
            arity = "0..*",
            description = "downloads all files of datasets containing the supplied suffix")
    public List<String> suffixes = new ArrayList<>();

    @Option(names = {"-r", "--regex"},
            arity = "0..*",
            description = "downloads all files of datasets filtered by the supplied regular expression")
    public List<String> regexPatterns = new ArrayList<>();

    @Option(names = {"-t", "--type"},
            description = "downloads all files of a given openBIS dataset type")
    public String datasetType = "";

    @Option(names = {"-c", "--dscode"},
            arity = "0..*",
            description = "downloads all matching datasetcodes")
    public List<String> datasetCodes = new ArrayList<>();

    @Option(names = {"-dss", "--dss_url"},
            description = "DataStoreServer URL")
    public String dss_url = "https://qbis.qbic.uni-tuebingen.de:444/datastore_server";

    @Option(names = {"-as", "--as_url"},
            description = "ApplicationServer URL")
    public String as_url = "https://qbis.qbic.uni-tuebingen.de/openbis/openbis";

    @Option(names = {"-d", "--dstypes"},
            description = "prints all supported dataset types for filtering")
    public boolean datasetTypeHelp;

    @Option(names = {"-h", "--help"},
            usageHelp = true,
            description = "display a help message")
    public boolean helpRequested = false;

    @Override
    public String toString() {
        return "PostmanCommandLineOptions{" +
                "old=" + old +
                ", user='" + user + '\'' +
                ", ids=" + ids +
                ", sampleIDsFilePath=" + sampleIDsFilePath +
                ", outputPath='" + outputPath + '\'' +
                ", bufferMultiplier=" + bufferMultiplier +
                ", suffixes=" + suffixes +
                ", regexPatterns=" + regexPatterns +
                ", datasetType='" + datasetType + '\'' +
                ", datasetCodes=" + datasetCodes +
                ", dss_url='" + dss_url + '\'' +
                ", as_url='" + as_url + '\'' +
                ", datasetTypeHelp=" + datasetTypeHelp +
                ", helpRequested=" + helpRequested +
                '}';
    }
}

