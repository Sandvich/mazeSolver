package org.uk.puppykit.mazeSolver;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileReader;
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

    public FileReader getFile() throws FileNotFoundException, ParseException {
        ParseOpts();
        System.out.println("Reading in: " + new File(filename).getAbsolutePath());
        FileReader inFile = new FileReader(filename);
        return inFile;
    }

    private void ParseOpts() throws ParseException {
        CommandLine cl = parser.parse(options, cli);
        filename = cl.getOptionValue("filename");
    };
}
