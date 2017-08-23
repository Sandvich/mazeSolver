package org.uk.puppykit.mazeSolver;
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
 *
 * Note: the start and end points are also stored in the start and end Point objects
 */

public class Maze {
    private Point size;
    private Point start;
    private Point end;
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

    public Point getSize() { return size; }
    public Point getStart() {  return start; }
    public Point getEnd() { return end; }
    public int[][] getWalls() { return walls; }
    public void setCell(int x, int y, int value) { walls[y][x] = value; }

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
}