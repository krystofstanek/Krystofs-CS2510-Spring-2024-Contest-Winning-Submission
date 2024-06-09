package student;

import java.util.ArrayList;
import java.util.List;
import java.awt.image.BufferedImage;

import java.util.Random;
import java.awt.Color;

import java.util.Collections;


import java.util.HashSet;
import java.util.Set;

public class Grid extends AbstractGrid {

    private List<colAndBlue> blueTracker = new ArrayList<>();
    private int imageWidth;
    private final int imageHeight;

    private int[][] pixelGrid;

    private Set<Integer> removedColumns = new HashSet<>();

    Grid(BufferedImage image, long seed) {

        super(image, seed);
        random = new Random(seed);

        imageWidth = image.getWidth();
        imageHeight = image.getHeight();

        this.pixelGrid = new int[imageHeight][imageWidth];
        for (int y = 0; y < imageHeight; y++) {
            for (int x = 0; x < imageWidth; x++) {

                this.pixelGrid[y][x] = image.getRGB(x, y);
            }
        }
        createBluenessDict();
    }

    Grid(BufferedImage image, long seed, Boolean testMode) {

        super(image, seed, testMode);
        this.testMode = testMode;
        random = new Random(seed);

        imageWidth = image.getWidth();
        imageHeight = image.getHeight();


        this.pixelGrid = new int[imageHeight][imageWidth];
        for (int y = 0; y < imageHeight; y++) {
            for (int x = 0; x < imageWidth; x++) {

                this.pixelGrid[y][x] = image.getRGB(x, y);
            }
        }
        createBluenessDict();

    }

    @Override
    protected BufferedImage convertToBufferedImage() {
        int currentSize = imageWidth - removedColumns.size();
        BufferedImage image = new BufferedImage(currentSize, imageHeight, BufferedImage.TYPE_INT_RGB);
        int currentX = 0;
        for (int x = 0; x < imageWidth; x++) {
            if (removedColumns.contains(x)){
                continue;
            }
            for (int y = 0; y < imageHeight; y++) {
                    image.setRGB(currentX, y, pixelGrid[y][x]);
            }
            currentX = currentX + 1;
        }
        return image;
    }

    int blueAmount(int rgb) {
        Color color = new Color(rgb);
        return color.getBlue();
    }

    public class colAndBlue {
        long blueAmount;
        int col;

        colAndBlue(int newCol, long newBlueAmount) {
            col = newCol;
            blueAmount = newBlueAmount;
        }

        long getBlueAmount() {
            return blueAmount;
        }

        int getCol() {
            return col;
        }
    }

    void createBluenessDict() {
        for (int x = 0; x < imageWidth; x++) {
            long totalBlue = 0;
            for (int y = 0; y < imageHeight; y++) {
                totalBlue = totalBlue + blueAmount(pixelGrid[y][x]);
            }
            blueTracker.add(new colAndBlue(x, totalBlue));
        }
        Collections.sort(blueTracker, (col1, col2) -> Long.compare(col2.getBlueAmount(), col1.getBlueAmount()));

    }

    // needed amountOfChange to pass the contest's testing enviroment
    int amountOfChange;
    @Override
    protected int getBluestColumnIndex() throws RequestFailedException {

        if (imageWidth - removedColumns.size() == 0) {
            throw new RequestFailedException("Cant get a column from an image with 0 columns");
        }
        amountOfChange = 0;

        int rawIndex = blueTracker.get(removedColumns.size()).getCol();

        for (int alreadyRemovedCol : removedColumns) {
            if (alreadyRemovedCol < rawIndex) {
                amountOfChange = amountOfChange - 1;
            }
        }
        return (rawIndex + amountOfChange);
    }
    @Override
    protected void removeBluestColumn() throws RequestFailedException {

        if (imageWidth - removedColumns.size() == 1) {
            throw new RequestFailedException("Cant remove the last column of an image");
        }

        int colToRemove = getBluestColumnIndex();
        

        colToRemove = colToRemove - amountOfChange;

        removedColumns.add(colToRemove);
    }


    void removeRandomColumn() throws RequestFailedException {
    }
    void undo() throws RequestFailedException {
    }
}