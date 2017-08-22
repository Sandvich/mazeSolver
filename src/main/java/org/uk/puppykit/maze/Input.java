package org.uk.puppykit.maze;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileNotFoundException;

public class Input {
    private CommandLineParser parser;
    private Options options;
    private String[] cli;
    private String filename;

    public Input(String... cli) {
        Option filenameOpt = Option.builder()
                                    .argName("f")
                                    .longOpt("filename")
                                    .hasArg(true)
                                    .desc("File containing maze")
                                    .required(true)
                                    .build();
        options = new Options();
        options.addOption(filenameOpt);
        this.cli = cli;
        filename = null;
        parser = new DefaultParser();
    }

    public File getFile() throws FileNotFoundException, ParseException {
        ParseOpts();
        File inFile = new File(filename);

        if (inFile.exists()) {
            return inFile;
        } else {
            throw new FileNotFoundException("File " + filename + " could not be found!");
        }
    }

    private void ParseOpts() throws ParseException {
        CommandLine cl = parser.parse(options, cli);
        filename = cl.getOptionValue("filename");
    };
}
