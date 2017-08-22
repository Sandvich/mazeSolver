package org.uk.puppykit.mazeSolver;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import static java.lang.Math.abs;

/*
 * A virtual representation of the maze.
 * 0s represent empty space
 * 1s represent walls
 * 2 is the start point
 * 3 is the end point
 * 4s represent the route taken
 *
 * Note: the start and end points are also stored in the start and end Point objects
 */

public class Maze {
    private Point size;
    private Point start;
    private Point end;
    private Point position;
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
        Point cursor = new Point(start.x,start.y);
        position = new Point(start.x, start.y);
        Integer MDBest;

        while (cursor.x !=end.x || cursor.y != end.y) {
            if (!checkProductivePaths(position, cursor, true)) {
                MDBest = manhattanDistance(end, position);
                while(!manhattanDistance(position,end).equals(MDBest)) {
                    // Follow left hand rule
                    if (!checkProductivePaths(position,cursor,false)) {
                        while (!manhattanDistance(position, end).equals(MDBest)) {
                            // Follow right hand rule
                            if (!checkProductivePaths(position, cursor, false)) {
                                printMaze("Maze could not be solved\nProgress before exit:");
                                System.exit(255);
                            }
                        }
                    }
                }
            }
        }
    }

    /*
     * This function checks each direction from the Point position (using cursor to do so). In each cardinal direction,
     * it checks if moving one space in that direction would be a 'Productive' move - that is, one which moves it closer
     * to the end (based on manhattan distance).
     * If there is a productive move that does not hit a wall and move is true, it makes that move (returning true
     * regardless of whether or not it was told to make the move). If not, it returns false (so that the algorithm can
     * try other things).
     */
    private Boolean checkProductivePaths(Point position, Point cursor, Boolean move) {
        //right
        cursor.x = position.x + 1;
        cursor.y = position.y;
        if (!check(cursor, move)){
            // left
            cursor.x = position.x - 1;
            if (!check(cursor, move)){
                //down
                cursor.x = position.x;
                cursor.y = position.y + 1;
                if (!check(cursor, move)){
                    //up
                    cursor.y = position.y - 1;
                    if (!check(cursor, move)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private Boolean check(Point cursor, Boolean move) {
        if (walls[cursor.y][cursor.x] != 1) {
            if (manhattanDistance(end, cursor) < manhattanDistance(end, position)) {
                if (move) {
                    position.x = cursor.x;
                    position.y = cursor.y;
                    walls[position.y][position.x] = 4;
                }
                return true;
            }
        }
        return false;
    }

    public static Integer manhattanDistance(Point point1, Point point2) {
        return abs(point1.x - point2.x + point1.y - point2.y);
    }
}
