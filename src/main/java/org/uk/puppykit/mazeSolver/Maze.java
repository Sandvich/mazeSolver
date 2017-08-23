package org.uk.puppykit.mazeSolver;
import math.geom2d.Vector2D;
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
    private Vector2D endVector;
    private Vector2D position;
    private int[][] walls;
    private Map<Integer, Character> lookup;

    public Maze(FileReader mazeFile) throws IOException {
        BufferedReader mazeBuffered = new BufferedReader(mazeFile);
        size = makePoint(mazeBuffered.readLine());
        start = makePoint(mazeBuffered.readLine());
        end = makePoint(mazeBuffered.readLine());

        // Copy the maze to memory
        walls = new int[size.y][size.x];
        for (int i=0; i<size.y; i++) {
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

    private void readMazeLine(String line, int[] array) {
        String[] splitline = line.split(" ");
        for (int i=0; i < size.x; i++) {
            array[i] = Integer.parseInt(splitline[i]);
        }
    }

    private void setCell(Point coord, int value) {
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

    /*
     * The algorithm being used here is known as the maze-routing algorithm. It will always find a path if there is one,
     * but it will not always find the shortest path. The algorithm itself is explained in the comments
     */
    public void solve() {
        Vector2D cursor = new Vector2D(start.x, start.y);
        position = new Vector2D(start.x, start.y);
        Vector2D MDBestPosition;
        int MDBest;
        endVector = new Vector2D(end.x, end.y);

        // So long as the maze is not solved
        while (Math.round(cursor.x()) != end.x || Math.round(cursor.y()) != end.y) {
            // Check if there is a productive path (one which reduces the manhattan distance). If so, take it.
            if (!checkProductivePaths(position, true)) {
                // If not, mark this position.
                MDBest = manhattanDistance(endVector, position);
                MDBestPosition = new Vector2D((int)position.x(), (int)position.y());

                System.out.println("MDBestPosition: " + MDBestPosition.toString());
                printMaze();
                // Then use the left hand rule to keep progressing until it either fails or finds its way to a point
                // which has a lower manhattan distance to the exit than the previous position marked.
                if (!handRule(MDBest, true)) {
                    // If this fails, reset to the marked position and try again with the right hand rule.
                    position = new Vector2D(MDBestPosition.x(), MDBestPosition.y());
                    if (!handRule(MDBest, false)) {
                        // If neither works, the algorithm has failed/there is no solution.
                        printMaze("Maze could not be solved\nProgress before exit:");
                        System.exit(255);
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
    private Boolean checkProductivePaths(Vector2D position, Boolean move) {
        //right
        Vector2D cursor = new Vector2D(position.x()+1, position.y());
        if (!check(cursor, move)){
            // left
            cursor = cursor.plus(new Vector2D(-2, 0));
            if (!check(cursor, move)){
                //down
                cursor = cursor.plus(new Vector2D(1,1));
                if (!check(cursor, move)){
                    //up
                    cursor = cursor.plus(new Vector2D(0,-2));
                    if (!check(cursor, move)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private Boolean check(Vector2D cursor, Boolean move) {
        if (walls[(int)cursor.y()][(int)cursor.x()] != 1) {
            if (manhattanDistance(endVector, cursor) < manhattanDistance(endVector, position)) {
                if (move) {
                    position = new Vector2D(cursor.x(), cursor.y());
                    walls[(int)position.y()][(int)position.x()] = 4;
                }
                return true;
            }
        }
        return false;
    }

    public static Integer manhattanDistance(Vector2D point1, Vector2D point2) {
        return (int)abs(point1.x() - point2.x() + point1.y() - point2.y());
    }

    private Vector2D handRuleMove(Integer xadd, Integer yadd) {
        Vector2D prev = new Vector2D(position.x(), position.y());
        position = position.plus(new Vector2D(xadd, yadd));
        return prev;
    }

    /*
     * This only makes one move, deciding somewhat arbitrarily which way to go. Useful for the first move, as prev ==
     * position.
     */
    private void handRuleFirstMove(Boolean left) {
        if (left) {
            if (walls[(int)position.y()][(int)position.x() - 1] != 1) {
                position = position.plus(new Vector2D(-1, 0));
            } else if (walls[(int)position.y() + 1][(int)position.x()] != 1) {
                position = position.plus(new Vector2D(0, 1));
            } else if (walls[(int)position.y()][(int)position.x() + 1] != 1) {
                position = position.plus(new Vector2D(1, 0));
            } else if (walls[(int)position.y() - 1][(int)position.x()] != 1) {
                position = position.plus(new Vector2D(0, -1));
            }
        } else { // Same as above, but starting right
            if (walls[(int)position.y()][(int)position.x() + 1] != 1) {
                position = position.plus(new Vector2D(1, 0));
            } else if (walls[(int)position.y() - 1][(int)position.x()] != 1) {
                position = position.plus(new Vector2D(0, -1));
            } else if (walls[(int)position.y()][(int)position.x() - 1] != 1) {
                position = position.plus(new Vector2D(-1, 0));
            } else if (walls[(int)position.y() + 1][(int)position.x()] != 1) {
                position = position.plus(new Vector2D(0, 1));
            }
        }
    }

    private Boolean handRule(Integer MDBest, Boolean left) {
        Vector2D prev = new Vector2D(position.x(), position.y());
        Vector2D lastMove;
        Vector2D cursorLeft;
        Vector2D cursorForwards;
        Vector2D cursorRight;
        while(manhattanDistance(position,endVector) >= MDBest) {
            lastMove = position.minus(prev);
            cursorLeft = position.plus(lastMove.rotate(Math.PI * 3/2));
            cursorForwards = position.plus(lastMove);
            cursorRight = position.plus(lastMove.rotate(Math.PI/2));
            System.out.println("Current position: " + position.toString());
            if (manhattanDistance(prev, position).equals(0)) {
                handRuleFirstMove(left);
            } else if (left) {
                System.out.println("LastMove = " + lastMove.toString());
                System.out.println("CursorLeft = " + cursorLeft.toString() + ", Valid: " + (walls[(int)cursorLeft.y()][(int)cursorLeft.x()] != 1));
                System.out.println("CursorForwards = " + cursorForwards.toString() + ", Valid: " + (walls[(int)cursorForwards.y()][(int)cursorForwards.x()]!=1));
                System.out.println("CursorRight = " + cursorRight.toString() + ", Valid: " + (walls[(int)cursorRight.y()][(int)cursorRight.x()]!=1));
                // Try going left, then forwards, then right.
                if (walls[(int)cursorLeft.y()][(int)cursorLeft.x()] != 1) {
                    prev = handRuleMove((int)lastMove.rotate(Math.PI * 3/2).x(), (int)lastMove.rotate(Math.PI * 3/2).y());
                } else if (walls[(int)cursorForwards.y()][(int)cursorForwards.x()]!=1) {
                    prev = handRuleMove((int)lastMove.x(), (int)lastMove.y());
                } else if (walls[(int)cursorRight.y()][(int)cursorRight.x()]!=1) {
                    prev = handRuleMove((int)lastMove.rotate(Math.PI/2).x(),(int)lastMove.rotate(Math.PI/2).y());
                } else { return false; }
            } else {
                System.out.println("Ya didn't write the right-hand version, ya doof.");
                return false;
            }
        }
        return true;
    }
}
