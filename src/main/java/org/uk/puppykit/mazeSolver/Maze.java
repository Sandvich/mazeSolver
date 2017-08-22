package org.uk.puppykit.mazeSolver;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Maze {
    private Point size;
    private Point start;
    private Point end;

    public Maze(FileReader mazeFile) throws IOException {
        BufferedReader mazeBuffered = new BufferedReader(mazeFile);
        size = makePoint(mazeBuffered.readLine());
        start = makePoint(mazeBuffered.readLine());
        end = makePoint(mazeBuffered.readLine());
    }

    private Point makePoint(String coords) {
        Integer x = Integer.parseInt(coords.substring(0,1));
        Integer y = Integer.parseInt(coords.substring(2,3));
        return new Point(x, y);
    }
}
