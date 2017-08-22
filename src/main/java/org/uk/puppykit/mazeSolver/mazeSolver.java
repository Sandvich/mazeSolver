package org.uk.puppykit.mazeSolver;
import org.apache.commons.cli.ParseException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/*
 * Main class and entry point for the program.
 * Usage: filename=<filename>
 */

public class mazeSolver{
    public static void main(String... args) {
        // Get the file from the command line input
        Input inputobj = new Input(args);
        FileReader mazeFile = null;
        try {
            mazeFile = inputobj.getFile();
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        } catch (FileNotFoundException e) {
            System.out.println("File could not be found!");
        }

        // Make the maze
        try {
            Maze maze = new Maze(mazeFile);
        } catch (IOException e) {
            System.out.print(e.getMessage());
        }

        // Apply algorithm to solve it
        // Print output
    }
}
