package life.qbic

import java.nio.file.Path
import java.nio.file.Paths

/**
 * File system implementation of the ChecksumWriter interface.
 *
 * Provides methods to write matching checksums and failed checksums
 * into different summary files on the local file system.
 *
 * @author: Sven Fillinger
 */
class FileSystemWriter implements ChecksumWriter {

    /**
     * File that stores the summary report content for valid checksums.
     */
    final private File matchingSummaryFile

    /**
     * File that stores the summary report content for invalid checksums.
     */
    final private File failureSummaryFile

    /**
     * FileSystemWriter constructor with the paths for the summary files.     *
     *
     * @param matchingSummarFile The path where to write the matching checksum summary
     * @param failureSummaryFile The path where to write the failed checksum summary
     */
    FileSystemWriter(Path matchingSummaryFile, Path failureSummaryFile) {
        this.matchingSummaryFile = new File(matchingSummarFile.toString())
        this.failureSummaryFile = new File(failureSummaryFile.toString())
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void writeMatchingChecksum(String expectedChecksum, String computedChecksum, URL fileLocation) {
        def content = "$expectedChecksum\t$computedChecksum\t${Paths.get(fileLocation.toURI())}\n"
        this.matchingSummaryFile.append(content, "UTF-8")
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void writeInvalidChecksum(String expectedChecksum, String computedChecksum, URL fileLocation) {
        def content = "$expectedChecksum\t$computedChecksum\t${Paths.get(fileLocation.toURI())}\n"
        this.failureSummaryFile.append(content, "UTF-8")
    }
}
