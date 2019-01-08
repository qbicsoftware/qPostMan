[![Build Status](https://travis-ci.com/qbicsoftware/postman-cli.svg?branch=development)](https://travis-ci.com/qbicsoftware/postman-cli)[![Code Coverage](https://codecov.io/gh/qbicsoftware/postman-cli/branch/development/graph/badge.svg)](https://codecov.io/gh/qbicsoftware/postman-cli)

# postman

**Current stable version: 0.4.0 (15. January 2019)**

A client software written in Java for dataset downloads from QBiC's data management system openBIS (https://wiki-bsse.ethz.ch/display/bis/Home).

We are making use of the V3 API of openBIS (https://wiki-bsse.ethz.ch/display/openBISDoc1605/openBIS+V3+API) in order to interact with the data management system from command line, to provide a quick data retrieval on server or cluster resources, where the download via the qPortal is impractical. Experimental support is also provided for the old V1 API.

## Download
You can download postman from the GitHub release page: https://github.com/qbicsoftware/postman-cli/releases .

## Requirements
You need to have **Java JRE** or **JDK** installed (**openJDK** is fine), at least version 1.8 or 11. And the client's host must have allowance to connect to the server, which is determined by our firewall settings. If you are unsure, if your client is allowed to connect, contact us at support@qbic.zendesk.com.

## Usage
### Options
Just execute postman with `java -jar postman-cli.jar` or `java -jar postman.jar -h` to get an overview of the options:
```bash

~$ java -jar postman.jar                    
Usage: <main class> [-h] [-b=<bufferMultiplier>] [-f=<filePath>]
                    [-t=<datasetType>] -u=<user> [SAMPLE_ID]...
      [SAMPLE_ID]...          one or more QBiC sample ids
  @/path/to/config.txt        config file which specifies the AS and DSS url
  -as, --as_url=<url>         AS URL 
  -dss,--dss_url=<url>        DSS URL 
  -u,  --user=<user>          openBIS user name   
  -f,  --file=<filePath>      a file with line-separated list of QBiC sample ids
  -t,  --type=<datasetType>   filter for a given openBIS dataset type
  -s,  --suffix=<suffix>      filter for a given openBIS file suffix
  -r,  --regex=<regex>        filter for a given openBIS file regex 
  -c,  --dscode=<datasetCode> filter for a given dataset code
  -o,  --output<path>         output path to which all files will be downloaded to
  -b,  --buffer-size=<bufferMultiplier>
                              a integer muliple of 1024 bytes (default). Only
                                change this if you know what you are doing.
  -old --old                  uses the old V1 API to download the files. May be up to 80% faster. Please not that filtering                                 support is experimental!    
  -d,  --dstypes              prints all supported dataset type filters    
  -h, --help                  display a help message
```
### Provide a QBiC ID
The simplest scenario is, that you want to download a dataset/datasets from a sample. Just provide the QBiC ID for that sample and your username (same as the one you use for the qPortal):
```bash
~$ java -jar postman.jar -u <your_qbic_username> <QBiC Sample ID>
```
postman will prompt you for your password, which is the password from your QBiC user account.

After you have provided your password and authenticate successfully, postman tries to download all datasets that are registered for that given sample ID and downloads them to the current working directory. Example run:

```bash
[bbbfs01@u-003-ncmu03 ~]$ java -jar postman.jar -u bbbfs01 QMFKD003AG                                                             
```

### Filter for dataset type

You can filter for dataset types, using the `-t` option and one of the following openBIS dataset types we are currently using. You can get a list of all currently supported dataset types by running postman with the `-d` option. Moreover you can refer to the list [Supported dataset types](doc/supportedDatasetTypes).

### Filter for file suffix

You can filter for file suffixes, using the `-s` option:    
Example: `-s .pdf`

### Filter for file regex

You can filter for files by a provided regex, using the `-r` option:    
Example: `-r .*.jobscript.FastQC.*`

Please note that depending on your favorite shell, you may need quote your regex. Note that postman supports arity parameters. Therefore it is viable to run postman with multiple parameters after an option which will all be assigned correctly:    
`-s .pdf .html` will interpret `.pdf` and `.html` as suffixes.


### Provide a file with several QBiC IDs
In order to download datasets from several samples at once, you can provide a simple text file with multiple, line-separated, QBiC IDs and hand it to postman with the `-f` option.

postman will automatically iterate over the IDs and try to download them.

### Config file

Postman uses picocli file arguments. Therefore a config file has to be passed with the '@' prefix to its path:    
Example: 
```bash
java -jar postman.jar -u <user> <sample> @path/to/config.txt 
```
The structure of the configuration file is:       <code>[-cliOption] [value]</code>   
For example: To set the ApplicationServerURL to another URL we have to use:    
<code>-as [URL] </code>    
Therefore to use our openbis URL we write the following line in the config file (Anything beginning with '#' is a comment):    
<code># Set the AS_URL (ApplicationServerURL) to the value defined below </code>    
<code>-as https://qbis.qbic.uni-tuebingen.de/openbis/openbis</code>       
The following config file options are currently supported:    
AS_URL (ApplicationServerURL)       
-as [URL]       
DSS_URL (DataStoreServerURL)     
-dss [URL]       

A default file is provided here: [default-config](https://github.com/qbicsoftware/postman-cli/blob/development/config.txt). If no config file is provided postman uses the default values set in the PostmanCommandLineOptions class.   

If no config file or commandline option is provided, Postman will resort to the defaults set here: [Defaults](https://github.com/qbicsoftware/postman-cli/blob/development/src/main/java/life/qbic/io/commandline/PostmanCommandLineOptions.java).    
Hence, the default AS is set to: <code>https://qbis.qbic.uni-tuebingen.de/openbis/openbis</code>    
and the DSS defaults to: <code>https://qbis.qbic.uni-tuebingen.de:444/datastore_server</code>    

### Performance issues
We discovered, that a default buffer size of 1024 bytes seems not always to get all out of the performance that is possible for the dataset download. Therefore, we allow you to enter a multipler Integer value that increases the buffer size. For example a multipler of 2 will result in 2x1024 = 2048 bytes and so on.

Just use the `-b` option for that. The default buffer size remains 1024 bytes, if you don't specify this value.


