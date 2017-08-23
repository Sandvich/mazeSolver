package org.uk.puppykit.mazeSolver;
import math.geom2d.Vector2D;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static java.lang.Math.abs;

/*
 * A virtual representation of the maze.
 * 0s represent empty space
 * 1s represent walls
 * 2 is the start point
 * 3 is the end point
 * 4s represent the route taken
 * 5s represent dead ends
 * 6s are wave crests
 * 5 and 6 should not be seen unless debugging, and are only stored in the editableMaze array (in the solve function)
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
        lookup.put(5,'O');
        lookup.put(6,'@');

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
     * An implementation of the Collision Solver. crests tracks the crest of each wave.
     */
    public void solve() {
        List<Point> crests = new ArrayList<>();
        int[][] editableMaze = new int[size.y][size.x];
        for (int i=0; i<walls.length; i++) {
            editableMaze[i] = Arrays.copyOf(walls[i],size.x);
        }
        List<Point> paths;
        List<Point> toRemove = new ArrayList<>();
        List<Point> toAdd = new ArrayList<>();
        Point crest;

        crests.add(new Point(start.x, start.y));

        while (crests.size() != 0) {
            for (int i=0; i<crests.size(); i++) {
                crest = crests.get(i);

                if (crest.x == end.x && crest.y == end.y) {
                    toRemove.add(crest);

                } else {
                    // For each wave crest, check where it can move and move it there (splitting if necessary)
                    paths = possiblePaths(editableMaze, crest, true);
                    editableMaze[crest.y][crest.x] = 4;

                    if (paths.size() == 0) {
                        editableMaze = followBack(crest, editableMaze);
                        toRemove.add(crest);
                    } else if (paths.size() == 1) {
                        // Follow path
                        crest.x = paths.get(0).x;
                        crest.y = paths.get(0).y;
                        editableMaze[crest.y][crest.x] = 6;
                    } else {
                        // At each junction, split into multiple paths
                        toAdd.addAll(paths);
                        toRemove.add(crest);
                        for (Point path:paths) {
                            editableMaze[path.y][path.x] = 6;
                        }
                    }

                    // Check for collisions
                }
            }
            crests.removeAll(toRemove);
            crests.addAll(toAdd);
            toRemove = new ArrayList<>();
            toAdd = new ArrayList<>();
        }

        for (int y=0; y<size.y; y++) {
            for (int x=0; x<size.x; x++) {
                if (editableMaze[y][x]==4) {
                    walls[y][x] = 4;
                }
            }
        }
    }

    private int[][] followBack(Point crest, int[][] maze) {
        // When we find a dead end, we follow it backwards marking it as a dead end.
        List<Point> paths;

        maze[crest.y][crest.x] = 5;
        while(possiblePaths(maze, crest, false).size() == 1) {
            paths = possiblePaths(maze, crest, false);
            crest.x = paths.get(0).x;
            crest.y = paths.get(0).y;
            maze[crest.y][crest.x] = 5;
        }
        maze[crest.y][crest.x] = 4;
        return maze;
    }

    private List<Point> possiblePaths(int[][] maze, Point location, boolean forwards) {
        List<Point> paths;
        Set<Integer> validSpace = new HashSet<>();
        if (forwards) {
            validSpace.add(0);
            validSpace.add(3);
            validSpace.add(6);
        } else {
            validSpace.add(0);
            validSpace.add(4);
        }

        paths = possiblePathsCalc(maze, location, validSpace);
        return paths;
    }

    private List<Point> possiblePathsCalc(int[][] maze, Point location, Set<Integer> validSpace) {
        List<Point> paths = new ArrayList<>();
        // above: test if the space is empty, a wave crest or the end
        if (validSpace.contains(maze[location.y+1][location.x])) {
            paths.add(new Point(location.x,location.y+1));
        }
        // below
        if (validSpace.contains(maze[location.y-1][location.x])) {
            paths.add(new Point(location.x,location.y-1));
        }
        // left
        if (validSpace.contains(maze[location.y][location.x-1])) {
            paths.add(new Point(location.x-1,location.y));
        }
        // right
        if (validSpace.contains(maze[location.y][location.x+1])) {
            paths.add(new Point(location.x+1,location.y));
        }
        return paths;
    }
}