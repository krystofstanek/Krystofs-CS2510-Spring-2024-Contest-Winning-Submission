package student;
import java.util.ArrayList;
import java.util.List;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.awt.Color;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * The Grid class represents an image grid where columns can be removed based on their blueness.
 */
public class Grid extends AbstractGrid {

    private List<colAndBlue> blueTracker = new ArrayList<>();
    private int imageWidth;
    private final int imageHeight;
    private int[][] pixelGrid;
    private Set<Integer> removedColumns = new HashSet<>();

    /**
     * Creates a Grid object from a BufferedImage and a seed for random operations.
     *
     * @param image The BufferedImage to create the grid from.
     * @param seed The seed for random operations.
     */
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


    /**
     * Constructs a Grid object from a BufferedImage, a seed for random operations, and a test mode flag.
     *
     * @param image The BufferedImage to create the grid from.
     * @param seed The seed for random operations.
     * @param testMode The flag to indicate test mode.
     */
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

    /**
     * Converts the current state of the grid to a BufferedImage, skipping over the removed columns.
     *
     * @return The BufferedImage representing the current state of the image.
     */
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
    
    /**
     * Extracts the blue amount from an RGB value.
     *
     * @param rgb The RGB value.
     * @return The amount of blue in the RGB value.
     */
    int extractBlueAmount(int rgb) {
        return rgb & 0xFF; 
    }

    /**
     * The colAndBlue class represents a column and its total blue amount.
     */
    public class colAndBlue {
        long blueAmount;
        int col;

        /**
         * Creates a colAndBlue object with a column index and total blue amount.
         *
         * @param newCol The column index.
         * @param newBlueAmount The total blue amount in the column.
         */
        colAndBlue(int newCol, long newBlueAmount) {
            col = newCol;
            blueAmount = newBlueAmount;
        }

        /**
         * Gets the column's total blue amount.
         *
         * @return The total blue amount.
         */
        long getBlueAmount() {
            return blueAmount;
        }

        /**
         * Gets the column's index.
         *
         * @return The column index.
         */
        int getCol() {
            return col;
        }
    }

    /**
     * Creates a dictionary of columns and their total blue amounts.
     */
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

    /**
     * Gets the index of the bluest column.
     *
     * @return The current index of the bluest column.
     * @throws RequestFailedException If the image has no columns left.
     */
    @Override
    protected int getBluestColumnIndex() throws RequestFailedException {

        if (imageWidth - removedColumns.size() == 0) {
            throw new RequestFailedException("Cant get a column from an image with 0 columns");
        }
        amountOfChange = 0; // needed amountOfChange to pass the contest's testing enviroment, 
        // as without it I would return that column's original index, not the current one
        
        int rawIndex = blueTracker.get(removedColumns.size()).getCol();
        for (int alreadyRemovedCol : removedColumns) {
            if (alreadyRemovedCol < rawIndex) {
                amountOfChange = amountOfChange - 1;
            }
        }
        return (rawIndex + amountOfChange);
    }
    /**
     * Removes the bluest column from the grid.
     *
     * @throws RequestFailedException If the image has only one column left.
     */
    @Override
    protected void removeBluestColumn() throws RequestFailedException {
        if (imageWidth - removedColumns.size() == 1) {
            throw new RequestFailedException("Cant remove the last column of an image");
        }
        int colToRemove = getBluestColumnIndex();
        colToRemove = colToRemove - amountOfChange;
        removedColumns.add(colToRemove);
    }
    // wasnt needed for the contest
    void removeRandomColumn() throws RequestFailedException {
    }
    // wasnt needed for the contest
    void undo() throws RequestFailedException {
    }
}
