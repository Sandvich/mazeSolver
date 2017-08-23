package org.uk.puppykit.mazeSolver;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
 * Solves an instance of the Maze class
 * If debug is turned on, you may see:
 * 5s, representing dead ends
 * 6s, representing wave crests
 */
public class Solver {

    /*
     * Solves a maze. An implementation of the Collision Solver. crests tracks the crest of each wave.
     */
    public static Maze solve(Maze toSolve) {
        java.util.List<Point> crests = new ArrayList<>();
        int[][] editableMaze = new int[toSolve.getSize().y][toSolve.getSize().x];
        for (int i=0; i<editableMaze.length; i++) {
            editableMaze[i] = Arrays.copyOf(toSolve.getWalls()[i],toSolve.getSize().x);
        }
        java.util.List<Point> paths;
        java.util.List<Point> toRemove = new ArrayList<>();
        java.util.List<Point> toAdd = new ArrayList<>();
        Point crest;

        crests.add(new Point(toSolve.getStart().x, toSolve.getStart().y));

        while (crests.size() != 0) {
            for (int i=0; i<crests.size(); i++) {
                crest = crests.get(i);

                if (crest.x == toSolve.getEnd().x && crest.y == toSolve.getEnd().y) {
                    toRemove.add(crest);
                    editableMaze[crest.y][crest.x] = 3;

                } else {
                    // For each wave crest, check where it can move and move it there (splitting if necessary)
                    paths = possiblePaths(editableMaze, crest, true);
                    if (editableMaze[crest.y][crest.x] != 2) {
                        editableMaze[crest.y][crest.x] = 4;
                    }

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
                }
                // Check for collisions and mark them
                for (Point otherCrest:crests) {
                    if (crest != otherCrest) {
                        if (crest.equals(otherCrest)) {
                            toRemove.add(crest);
                            editableMaze[crest.y][crest.x] = 1;
                        }
                    }
                }
            }
            crests.removeAll(toRemove);
            crests.addAll(toAdd);
            toRemove = new ArrayList<>();
            toAdd = new ArrayList<>();
        }

        for (int y=0; y<toSolve.getSize().y; y++) {
            for (int x=0; x<toSolve.getSize().x; x++) {
                if (editableMaze[y][x] == 5 || editableMaze[y][x] == 6) {
                    toSolve.setCell(x,y,0);
                } else {
                    toSolve.setCell(x,y,editableMaze[y][x]);
                }
            }
        }
        return toSolve;
    }

    private static int[][] followBack(Point crest, int[][] maze) {
        // When we find a dead end, we follow it backwards marking it as a dead end.
        java.util.List<Point> paths;
        paths = possiblePaths(maze, crest, false);

        // If there has been a collision, we set this point to be a wall and then apply the algorithm multiple times.
        if (paths.size() > 1) {
            maze[crest.y][crest.x] = 1;
            for (Point path: paths) {
                maze = followBack(path, maze);
            }
        } else {
            maze[crest.y][crest.x] = 5;
            while (paths.size() == 1) {
                crest.x = paths.get(0).x;
                crest.y = paths.get(0).y;
                maze[crest.y][crest.x] = 5;
                paths = possiblePaths(maze, crest, false);
            }
        }
        maze[crest.y][crest.x] = 4;
        return maze;
    }

    private static java.util.List<Point> possiblePaths(int[][] maze, Point location, boolean forwards) {
        java.util.List<Point> paths;
        Set<Integer> validSpace = new HashSet<>();
        if (forwards) {
            validSpace.add(0);
            validSpace.add(3);
            validSpace.add(6);
        } else {
            validSpace.add(4);
        }

        paths = possiblePathsCalc(maze, location, validSpace);
        return paths;
    }

    private static java.util.List<Point> possiblePathsCalc(int[][] maze, Point location, Set<Integer> validSpace) {
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
