package org.uk.puppykit.mazeSolver;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/*
 * A virtual representation of the maze.
 * 0s represent empty space
 * 1s represent walls
 * 2 is the start point
 * 3 is the end point
 * 4s represent the route taken
 * 5s represent failed routes
 *
 * Note: the start and end points are also stored in the start and end Point objects
 */

public class Maze {
    private Point size;
    private Point start;
    private Point end;
    private Integer[][] walls;
    private Map<Integer, Character> lookup;

    public Maze(FileReader mazeFile) throws IOException {
        BufferedReader mazeBuffered = new BufferedReader(mazeFile);
        size = makePoint(mazeBuffered.readLine());
        start = makePoint(mazeBuffered.readLine());
        end = makePoint(mazeBuffered.readLine());

        // Copy the maze to memory
        walls = new Integer[size.y][size.x];
        for (Integer i=0; i<size.y; i++) {
            readMazeLine(mazeBuffered.readLine(), walls[i]);
        }

        // Make the Int/Char map
        // This isn't really needed, but it means that we can work with ints, which is nicer
        lookup = new HashMap<>();
        lookup.put(0, ' ');
        lookup.put(1,'#');
        lookup.put(2,'S');
        lookup.put(3,'E');
        lookup.put(4,'X');
        lookup.put(5,' ');

        // Set start and end points.
        setCell(start, 2);
        setCell(end, 3);

        // Cleanup
        mazeBuffered.close();
    }

    private Point makePoint(String coords) {
        Integer x = Integer.parseInt(coords.split(" ")[0]);
        Integer y = Integer.parseInt(coords.split(" ")[1]);
        return new Point(x, y);
    }

    private void readMazeLine(String line, Integer[] array) {
        String[] splitline = line.split(" ");
        for (int i=0; i < size.x; i++) {
            array[i] = Integer.parseInt(splitline[i]);
        }
    }

    private void setCell(Point coord, Integer value) {
        walls[coord.y][coord.x] = value;
    }

    public void printMaze() {
        StringBuilder line;
        for (Integer i=0; i<size.y; i++) {
            line = new StringBuilder(size.x);
            for (Integer item:walls[i]) {line.append(lookup.get(item));}
            System.out.println(line.toString());
        }
    }
    public void printMaze(String message) {
        System.out.println(message);
        printMaze();
    }

    public void solve() {
        try {
            solve(start.y, start.x);
            walls[start.y][start.x] = 2;
        } catch (StackOverflowError e) {
            System.out.print("Maze could not be solved in the stack space available!\nThis probably means that it is too big for this algorithm to handle.");
            System.exit(255);
        }
    }
    private Boolean solve(Integer y, Integer x) {
        walls[y][x] = 4;

        if (walls[y][x+1] != 1) {
            if (walls[y][x+1] == 3) {
                return true;
            } else {
                solve(y, x+1);
            }
        }

        else if (walls[y+1][x] != 1) {
            if (walls[y+1][x] == 3) {
                return true;
            } else {
                solve(y+1, x);
            }
        }

        else if (walls[y][x-1] != 1) {
            if (walls[y][x-1] == 3) {
                return true;
            } else {
                solve(y, x-1);
            }
        }

        else if (walls[y-1][x] != 1) {
            if (walls[y-1][x] == 3) {
                return true;
            } else {
                solve(y-1, x);
            }
        }

        else {
            walls[y][x] = 5;
            return false;
        }
        return true;
    }
}
