package org.uk.puppykit.mazeSolver;
import org.apache.commons.cli.ParseException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/*
 * Main class and entry point for the program.
 * Usage:
 */

public class mazeSolver{
    public static void main(String... args) {
        // Get the file from the command line input
        Input inputobj = new Input(args);
        FileReader mazeFile = null;
        try {
            mazeFile = inputobj.getFile();
        } catch (FileNotFoundException | ParseException e) {
            System.out.print(e.getMessage());
            System.exit(1);
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
