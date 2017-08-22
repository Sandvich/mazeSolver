package org.uk.puppykit.maze;
import org.apache.commons.cli.ParseException;
import java.io.File;
import java.io.FileNotFoundException;

/*
 * Main class and entry point for the program.
 * Usage:
 */

public class mazeSolver{
    public static void main(String... args) {
        // Get the file from the command line input
        Input inputobj = new Input(args);
        File mazeFile = null;
        try {
            mazeFile = inputobj.getFile();
        } catch (FileNotFoundException | ParseException e) {
            System.out.print(e.getMessage());
            System.exit(1);
        }

        // Make the maze
        Maze maze = new Maze(mazeFile);
        // Apply algorithm to solve it
        // Print output
    }
}
