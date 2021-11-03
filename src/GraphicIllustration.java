import acm.graphics.*;
import acm.program.*;

import java.awt.*;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class GraphicIllustration extends GraphicsProgram {

    private static final int PAUSE_MILLISECONDS = 700;

    GTower startingTower;
    GTower tempTower;
    GTower finishTower;

    static GTower[] towers = new GTower[3];  // used to store tempTower and finishTower indexes, since the user can change them

    static int moveCounter;
    static int nOfDisks;

    public void run() {
        // Read the input
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter an amount of disks on the starting tower");
        nOfDisks = sc.nextInt();
        System.out.println("Enter a tower to transfer your disks (2 or 3)");
        int finishTowerIndex = sc.nextInt();

        int tempTowerIndex = 5 - finishTowerIndex;  // If finishTower is 3, then tempTower is 5-3=2 and vice versa

        // Create the towers
        initTowers(tempTowerIndex, finishTowerIndex);

        // Solve
        try {
            tow(nOfDisks, 1, finishTowerIndex, tempTowerIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // print out the result
        System.out.println("Total number of moves: " + moveCounter);
    }

    public void initTowers(int tempTowerIndex, int finishTowerIndex) {
        // Create towers
        startingTower = new GTower(200, 200);
        tempTower = new GTower(200, 200);
        finishTower = new GTower(200, 200);

        // Fill in the towers array
        towers[0] = startingTower;
        towers[tempTowerIndex-1] = tempTower;  // Convert indexes entered by user from base 1 to 0
        towers[finishTowerIndex-1] = finishTower;

        // Add towers
        for (int i = 0; i < towers.length; i++) {
            add(towers[i], 30 + 230 * i, 200);
        }

        // Create and add disks on the starting tower
        for (int i = 0; i < nOfDisks; i++) {
            int r = (int)(Math.random()*256);
            int g = (int)(Math.random()*256);
            int b = (int)(Math.random()*256);
            GTower.GDisk disk = new GTower.GDisk(180 - i * 15, 30 - i * 2, new Color(r, g, b));
            startingTower.addDiskOnTop(disk);
        }
    }

    class GTower extends GCompound {

        double thickness = 10;  // "thickness" of the two perpendicular lines that make the tower
        GDisk[] disks;  // the array of disks that compose the tower
        int disksCount;

        public GTower(double width, double height) {
            disks = new GDisk[nOfDisks];

            GRect bottom = new GRect(width, thickness);
            bottom.setFilled(true);

            GRect top = new GRect(thickness, height);
            top.setFilled(true);

            add(bottom, 0, height - thickness);
            add(top, width / 2 - thickness / 2, 0);
        }

        public void addDiskOnTop(GDisk diskToAdd) {
            double x, y;
            GDisk topDisk = disksCount == 0 ? null : disks[disksCount-1];
            if (topDisk == null) {
                x = this.getWidth() / 2 - diskToAdd.getWidth() / 2;
                y = this.getHeight() - thickness - diskToAdd.getHeight();
            }
            else {
                x = topDisk.getX() + (topDisk.getWidth() - diskToAdd.getWidth()) / 2;
                y = topDisk.getY() - diskToAdd.getHeight();
            }
            disks[disksCount] = diskToAdd;
            disksCount++;
            this.add(diskToAdd, x, y);
        }

        public GDisk removeDiskOnTop() {
            // get the disk
            GDisk topDisk = disksCount == 0 ? null : disks[disksCount-1];

            // check if tower is empty
            if (topDisk == null) return null;

            // remove disk
            this.remove(topDisk);
            disks[disksCount-1] = null;
            disksCount--;

            // return the removed disk
            return topDisk;
        }

        static class GDisk extends GCompound {
            public GDisk(double width, double height, Color color) {
                GRect rect = new GRect(width, height);
                rect.setFillColor(color);
                rect.setFilled(true);
                add(rect, 0, 0);
            }
        }
    }


    void tow(int n, int startTower, int finalTower, int tempTower) throws Exception {
        if (n == 1) {
            moveDisk(startTower, finalTower);
            return;
        }
        tow(n-1, startTower, tempTower, finalTower);
        moveDisk(startTower, finalTower);
        tow(n-1, tempTower, finalTower, startTower);
    }

    void moveDisk(int a, int b) throws Exception {
        moveCounter++;
        System.out.println(moveCounter + ". From " + a + " to " + b);

        // Get the towers from their indexes
        GTower fromTower = towers[a-1];
        GTower toTower = towers[b-1];

        TimeUnit.MILLISECONDS.sleep(PAUSE_MILLISECONDS);  // pause the animation each move

        GTower.GDisk removedDisk = fromTower.removeDiskOnTop();
        toTower.addDiskOnTop(removedDisk);
    }

}

