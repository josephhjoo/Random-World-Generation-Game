package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;
import utils.FileUtils;


import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static final int WIDTH = 50;
    public static final int HEIGHT = 30;
    private static long seed;
    private static Clip clip;
    private static boolean pressedKey = false;

    public static void main(String[] args) {
        final int canvas = 800;
        StdDraw.setCanvasSize(canvas, canvas);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        displayMainMenu();
    }

    private static void mainMenuMusic() {
        try {
            AudioInputStream audioInputStream =
                    AudioSystem.getAudioInputStream(new File("mainmenumusic.wav").getAbsoluteFile());
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            // Users/josephjoo/Downloads/cs61b/fa24-s717/proj3/

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }

    private static void gameMusic() {
        try {
            AudioInputStream audioInputStream =
                    AudioSystem.getAudioInputStream(new File("gamemusic.wav").getAbsoluteFile());
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            // Users/josephjoo/Downloads/cs61b/fa24-s717/proj3/

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }

    private static void stopMusic() {
        clip.stop();
        clip.close();

    }



    private static void displayMainMenu() {
        Scanner scanner = new Scanner(System.in);

        final double line1 = 0.625;
        final double line2 = 0.5;
        final double line3 = 0.375;
        final double line4 = 0.25;


        StdDraw.clear();
        StdDraw.text(WIDTH / 2.0, line1 * HEIGHT, "CS61B: BYOW");
        StdDraw.text(WIDTH / 2.0, line2 * HEIGHT, "(N) New Game");
        StdDraw.text(WIDTH / 2.0, line3 * HEIGHT, "(L) Load Game");
        StdDraw.text(WIDTH / 2.0, line4 * HEIGHT, "(Q) Quit Game");
        StdDraw.show();
        mainMenuMusic();

        while (true) {

            if (StdDraw.hasNextKeyTyped()) {
                char input = StdDraw.nextKeyTyped();
                input = Character.toUpperCase(input);
                if (input == 'N') {
                    startNewGame();
                    return;
                } else if (input == 'L') {
                    stopMusic();
                    loadGame();
                    return;
                } else if (input == 'Q') {
                    stopMusic();
                    System.exit(0);
                } else {
                    return;
                }
            }
        }
    }

    private static void startNewGame() {
        StringBuilder seedInput = new StringBuilder();

        final double line1 = 1.5;
        final double line2 = 2.0;
        final double line3 = 2.5;

        //how to deal with the flashing screen?
        while (true) {
            StdDraw.clear();
            StdDraw.text(WIDTH / 2.0, HEIGHT / line1, "CS61B: BYOW");
            StdDraw.text(WIDTH / 2.0, HEIGHT / line2, "Enter seed followed by 'S':");
            StdDraw.text(WIDTH / 2.0, HEIGHT / line3, "Seed: " + seedInput); // Show the entered seed
            StdDraw.show();

            if (StdDraw.hasNextKeyTyped()) {
                char input = StdDraw.nextKeyTyped();
                input = Character.toUpperCase(input);
                if (input == 'S') {
                    stopMusic();
                    seed = Long.parseLong(seedInput.toString());
                    generateWorld(seedInput.toString());
                    return;
                } else if (Character.isDigit(input)) {
                    seedInput.append(input);
                }
            }
        }
    }


    private static void loadGame() {
        StdDraw.clear();

        seed = Long.parseLong(readSeed());
        Random random = new Random(seed);

        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] world = new TETile[WIDTH][HEIGHT];

        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }

        StdDraw.clear();
        StdDraw.text(WIDTH / 2.0, HEIGHT / 2.0, "Loading saved game with seed: " + readSeed());
        StdDraw.show();
        final int pauseTime = 2000;
        StdDraw.pause(pauseTime); // Simulate generation time

        World.generateWorld(world, WIDTH, HEIGHT, random);
        gameMusic();

        int newX = Integer.parseInt(readX());
        int newY = Integer.parseInt(readY());

        world[Integer.parseInt(readX())][Integer.parseInt(readY())] = Tileset.AVATAR;
        Position avatarPosition = new Position(Integer.parseInt(readX()), Integer.parseInt(readY()));

        pressedKey = readPressedKey();


        TETile[][] newWorld = new TETile[WIDTH][HEIGHT];
        //render the world with the avatar
        if (pressedKey) {
            // handle reduced view
            for (int x = 0; x < WIDTH; x++) {
                for (int y = 0; y < HEIGHT; y++) {
                    if (Math.abs(x - newX) + Math.abs(y - newY) > 5) {
                        newWorld[x][y] = Tileset.NOTHING;
                    } else {
                        newWorld[x][y] = world[x][y];
                    }
                }
            }
            ter.renderFrame(newWorld);
        } else {
            ter.renderFrame(world);
        }

        //handle the movement
        handleAvatarMovement(world, avatarPosition, ter);
    }


    private static void generateWorld(String seedString) {
        Random random = new Random(Long.parseLong(seedString));

        //public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] world = new TETile[WIDTH][HEIGHT];

        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        StdDraw.clear();
        StdDraw.text(WIDTH / 2.0, HEIGHT / 2.0, "Generating world with seed: " + seedString);
        StdDraw.show();
        final int pauseTime = 2000;
        StdDraw.pause(pauseTime); // Simulate generation time

        //generate the world
        World.generateWorld(world, WIDTH, HEIGHT, random);
        gameMusic();

        // Find the starting position for the avatar
        Position avatarPosition = findStartingPosition(world);
        world[avatarPosition.x][avatarPosition.y] = Tileset.AVATAR;

        //render the world with the avatar
        ter.renderFrame(world);

        //handle the movement
        handleAvatarMovement(world, avatarPosition, ter);



    }

    private static Position findStartingPosition(TETile[][] world) {
        //put it at the left-bottom of the world
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (world[x][y] == Tileset.FLOOR) {
                    return new Position(x, y);
                }
            }
        }
        throw new RuntimeException("No valid starting position found!");
    }

    private static void handleAvatarMovement(TETile[][] world, Position avatarPosition, TERenderer ter) {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char input = StdDraw.nextKeyTyped();
                input = Character.toUpperCase(input);

                int newX = avatarPosition.x;
                int newY = avatarPosition.y;

                if (input == 'M') {
                    pressedKey = !pressedKey;
                }

                TETile[][] newWorld = new TETile[WIDTH][HEIGHT];

                if (pressedKey) {
                    // handle reduced view
                    for (int x = 0; x < WIDTH; x++) {
                        for (int y = 0; y < HEIGHT; y++) {
                            if (Math.abs(x - newX) + Math.abs(y - newY) > 5) {
                                newWorld[x][y] = Tileset.NOTHING;
                            } else {
                                newWorld[x][y] = world[x][y];
                            }
                        }
                    }
                    ter.renderFrame(newWorld);
                } else {
                    ter.renderFrame(world);
                }


                if (input == ':') {
                    saveAndQuit();
                }

                if (input == 'W') {
                    newY++;
                } else if (input == 'A') {
                    newX--;
                } else if (input == 'D') {
                    newX++;
                } else if (input == 'S') {
                    newY--;
                }


                if (isValidMove(newX, newY, world)) {
                    world[avatarPosition.x][avatarPosition.y] = Tileset.FLOOR;
                    avatarPosition.x = newX;
                    avatarPosition.y = newY;
                    world[newX][newY] = Tileset.AVATAR;
                    if (pressedKey) {
                        // handle reduced view
                        for (int x = 0; x < WIDTH; x++) {
                            for (int y = 0; y < HEIGHT; y++) {
                                if (Math.abs(x - newX) + Math.abs(y - newY) > 5) {
                                    newWorld[x][y] = Tileset.NOTHING;
                                } else {
                                    newWorld[x][y] = world[x][y];
                                }
                            }
                        }
                        ter.renderFrame(newWorld);
                    } else {
                        ter.renderFrame(world);
                    }

                }

            }
        }

    }

    public static void saveAndQuit() {

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char input2 = StdDraw.nextKeyTyped();
                input2 = Character.toUpperCase(input2);

                if (input2 == 'Q') {
                    writeSavedGame();
                    System.exit(0);
                } else {
                    break;
                }

            }
        }

    }

    private static void writeSavedGame() {



        FileUtils.createFile("seed.txt");
        FileUtils.createFile("x.txt");
        FileUtils.createFile("y.txt");
        FileUtils.createFile("pressedKey.txt");

        FileUtils.writeFile("seed.txt", String.valueOf(seed));
        FileUtils.writeFile("x.txt", String.valueOf(Position.x));
        FileUtils.writeFile("y.txt", String.valueOf(Position.y));
        FileUtils.writeFile("pressedKey.txt", String.valueOf(pressedKey));

        // /Users/josephjoo/Downloads/cs61b/fa24-s717/proj3/


    }

    private static String readSeed() {
        return FileUtils.readFile("seed.txt");
    }
    private static String readX() {
        return FileUtils.readFile("x.txt");
    }
    private static String readY() {
        return FileUtils.readFile("y.txt");
    }
    private static boolean readPressedKey() {
        return Boolean.parseBoolean(FileUtils.readFile("pressedKey.txt"));
    }


    private static boolean isValidMove(int x, int y, TETile[][] world) {
        return world[x][y] == Tileset.FLOOR;
    }


    public static class Position {
        static int x;
        static int y;
        Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int returnX() {
            return x;
        }

        public int returnY() {
            return y;
        }

    }

}
