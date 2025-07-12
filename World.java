package core;

import tileengine.TETile;
import tileengine.Tileset;

import java.util.Random;

public class World {
    public static void generateWorld(TETile[][] world, int width, int height, Random random) {
        int numberOfRooms = random.nextInt(6) + 6; //create between 5 and 10 rooms
        int[] centerX = new int[numberOfRooms];
        int[] centerY = new int[numberOfRooms];
        for (int i = 0; i < numberOfRooms; i++) {
            int roomWidth = random.nextInt(6) + 6;
            int roomHeight = random.nextInt(5) + 6;
            //(x,y) is the left-bottom of the room
            int flag = 0;
            int x = random.nextInt(width - roomWidth - 1) + 1;
            int y = random.nextInt(height - roomHeight - 1) + 1;
            centerX[i] = x + roomWidth / 2;
            centerY[i] = y + roomHeight / 2;
            for (int w  = x; w <= x + roomWidth; w++) {
                for (int j = y; j <= y + roomHeight; j++) {
                    if (world[w][j] == Tileset.FLOOR) {
                        flag = 1;
                    }
                }
            }
            if (flag == 0) {
                createRoom(world, x, y, roomWidth, roomHeight);
            } else {
                i--;
            }
            //store the center of the rooms for creating the hallways


        }
        for (int i = 0; i < numberOfRooms - 1; i++) {
            createHallway(world, centerX[i], centerY[i], centerX[i + 1], centerY[i + 1]);
        }

    }
    // build your own world!


    private static void createRoom(TETile[][] world, int x, int y, int roomWidth, int roomHeight) {
        for (int i = x; i < x + roomWidth; i++) {
            for (int j = y; j < y + roomHeight; j++) {
                if (i == x || i == x + roomWidth - 1 || j == y || j == y + roomHeight - 1) {
                    world[i][j] = Tileset.WALL;
                } else {
                    world[i][j] = Tileset.FLOOR;
                }

            }
        }
    }

    //(startX, startY) is the center of first room and (endX, endY) is the center of end room
    private static void createHallway(TETile[][] world, int startX, int startY, int endX, int endY) {
        int minX = Math.min(startX, endX);
        int maxX = Math.max(startX, endX);
        int minY = Math.min(startY, endY);
        int maxY = Math.max(startY, endY);

        //horizontal hallway
        for (int x = minX; x < maxX + 1; x++) {
            world[x][startY] = Tileset.FLOOR;
            //add wall beside the hallway but if there is floor then keep it.
            if (world[x][startY - 1] != Tileset.FLOOR) {
                world[x][startY - 1] = Tileset.WALL;
            }
            if (world[x][startY + 1] != Tileset.FLOOR) {
                world[x][startY + 1] = Tileset.WALL;
            }
        }
        //vertical hallway
        for (int y = minY; y < maxY + 1; y++) {
            world[endX][y] = Tileset.FLOOR;
            //add wall beside the hallway
            if (world[endX - 1][y] != Tileset.FLOOR) {
                world[endX - 1][y] = Tileset.WALL;
            }
            if (world[endX + 1][y] != Tileset.FLOOR) {
                world[endX + 1][y] = Tileset.WALL;
            }
        }
    }
}
