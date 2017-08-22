package org.uk.puppykit.mazeSolver;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

/*
 * A virtual representation of the maze.
 */

public class Maze {
    private Point size;
    private Point start;
    private Point end;
    private Integer[][] walls;

    public Maze(FileReader mazeFile) throws IOException {
        BufferedReader mazeBuffered = new BufferedReader(mazeFile);
        size = makePoint(mazeBuffered.readLine());
        start = makePoint(mazeBuffered.readLine());
        end = makePoint(mazeBuffered.readLine());

        // Copy the maze to memory
        walls = new Integer[size.x][size.y];
        for (Integer i=0; i<size.y; i++) {
            readMazeLine(mazeBuffered.readLine(), walls[i]);
            System.out.println(Arrays.toString(walls[i]));
        }
    }

    private Point makePoint(String coords) {
        Integer x = Integer.parseInt(coords.substring(0,1));
        Integer y = Integer.parseInt(coords.substring(2,3));
        return new Point(x, y);
    }

    private void readMazeLine(String line, Integer[] array) {
        for (int i=0; i < size.x; i++) {
            array[i] = Integer.parseInt(line.substring(i*2, i*2+1));
        }
    }
}
