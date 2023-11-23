import java.util.Random;

/*
 * This class contains the main method and is the driver for the program
 */
public class Solver2DClosestPair {

    private Coordinate[] points; //Global variable to hold coordinate points
    private long basicOpCount = 0; //Counts basic operations

    public static void main(String[] ags) {
        int n = 10000; //Number of points in the coordinate plane
        int[] cIndex; //Index of the closest pair
        System.out.println("N = " + n + "\n");

        Solver2DClosestPair plane = new Solver2DClosestPair(); //Creates object to use for exhaustive search

        plane.points = new Coordinate[n]; //Creates array of points size n
        Random rand = new Random();
        for(int i = 0; i < n; i++) {
            plane.points[i] = new Coordinate(rand.nextInt(n*15), rand.nextInt(n*15));
        }
    
        //***********************/
        //*  Exhaustive Search  */
        //***********************/
        final long startExhS = System.currentTimeMillis(); //Starts timer
        cIndex = plane.ExhaustiveSearch();
        //cIndex = new int[] {0, 5};
        final long endExhS = System.currentTimeMillis(); //Stops timer

        System.out.print("Closest pair: " +
                            plane.points[cIndex[0]].toString() +
                            ", " +
                            plane.points[cIndex[1]].toString() +
                            "\nDistance: " );
        System.out.printf("%.4f", plane.distance(plane.points[cIndex[0]], plane.points[cIndex[1]]));
        System.out.println("\nExhaustive search time: " + (endExhS - startExhS) + " milliseconds");
        System.out.println("Basic operations: " + plane.basicOpCount);

        //***********************/
        //*  Divide and conquer */
        //***********************/
        plane.basicOpCount = 0;
        Coordinate[] xSort = new Coordinate[n];
        Coordinate[] ySort = new Coordinate[n];
        //Loop clones the arrays
        for(int i = 0; i < n; i++) {
            xSort[i] = plane.points[i];
            ySort[i] = plane.points[i];
        }

        System.out.println("\n");
        final long startDivC = System.currentTimeMillis(); //Starts timer

        //Sorts by x, then y
        plane.quicksort(xSort,0, xSort.length-1,true);
        plane.quicksort(ySort,0, xSort.length-1,false);

        Pair minDistance = plane.DivideConquer(xSort, ySort);
        final long endDivC = System.currentTimeMillis(); //Stops timer
        System.out.print("Closest pair: " +
                            minDistance.getC1().toString() +
                            ", " + minDistance.getC2().toString() +
                            "\nDistance: ");
        System.out.printf("%.4f", minDistance.getDistance());
        System.out.println("\nDivide and Conquer time: " + (endDivC - startDivC) + " milliseconds");
        System.out.println("Basic operations: " + plane.basicOpCount);
    }

    /* This method executes the basic exhaustive search algorithm to find the closest pair.
     * Its strategy is to compare every coordinate with every other coordinate, finding the distance,
     * then determining the closest pair.
     * The expected time complexity is O(n^2)
     * Returns:
     *     Indices of the closest pair
     */
    private int[] ExhaustiveSearch() {
        int[] pairIndex = new int[2];
        double closestDistance = 0;
        //Brute force checks every coordinate pair to see if they are the closest
        for(int i = 0; i < points.length; i++) {
            for(int j = 0; j < points.length; j++) {
                double distance = 0;
                //If indexes aren't the same point, check the distance
                if(i != j) {
                    distance = distance(points[i], points[j]); //Checks the distance
                    //System.out.println("points " + points[i].toString() + " " + points[j].toString() + ": " + distance); //Prints for testing

                    basicOpCount++; //Counts each distance comparison as a basic operation
                    //If distance is smaller set it as the smallest point or if distance is 0 (first check)
                    if(distance < closestDistance || closestDistance == 0) {
                        closestDistance = distance; //Sets new closest distance
                        pairIndex[0] = i; //Index of first close point
                        pairIndex[1] = j; //Index of second close point
                    }
                }

            }
        }
        return pairIndex;
    }

    /* This method implements a divide-and-conquer algorithm to find the closest pair
     * Returns:
     *     Indices of the closest pair
     */
    private Pair DivideConquer(Coordinate[] xSort, Coordinate[] ySort) {
        //if n<=3 do brute force
        if(xSort.length <= 3) { //Does brute force, find better implementation
            Pair minDist = new Pair(Integer.MAX_VALUE);
            for(int i = 1; i < xSort.length; i++) {
                double newDist = distance(xSort[i - 1], xSort[i]); //Finds distance of new pair
                if(newDist < minDist.getDistance()) { //Compares to existing pair, replaces if distance is smaller
                    minDist = new Pair(xSort[i - 1], xSort[i], newDist);
                }
            }
            return minDist; //Returns the closest pair
        }


        int newSize1 = xSort.length/2 + xSort.length%2; //divided by two rounded up
        Coordinate[] pL = new Coordinate[newSize1]; //Array will contain first half of points sorted by x
        Coordinate[] qL = new Coordinate[newSize1]; //Array will contain same points, but sorted by y
        for(int i = 0; i < newSize1; i++) { //Copies first n/2 (ceiling) points of P (xSort) to array PL
            basicOpCount++;
            pL[i] = xSort[i]; //Add coordinate to the left array
            xSort[i].setInLeft(true); //Flag set that the coordinate is in the left
        }
        for(int i = 0, j = 0; i < ySort.length; i++) { //Copies same points from Q to Q1
            basicOpCount++;
            if(ySort[i].isInLeft()) {
                qL[j] = ySort[i]; //Add coordinate to the left array
                j++; //Increments qL counter
            }
        }

        int newSizeR = xSort.length/2; //divided by two rounded down (truncated)
        Coordinate[] pR = new Coordinate[newSizeR];
        Coordinate[] qR = new Coordinate[newSizeR];
        for(int i = newSize1, j = 0; i < xSort.length; i++, j++) { //Copies remaining half points of P into Pr
            basicOpCount++;
            pR[j] = xSort[i]; //Add coordinate to the right array
        }
        for(int i = 0, j = 0; i < ySort.length; i++) { //Copies same points from Q to QR
            basicOpCount++;
            if(!ySort[i].isInLeft()) {
                qR[j] = ySort[i]; //Add coordinate to the left array
                j++; //Increments qR counter
            } else { //Resets flag for next recursive call
                ySort[i].setInLeft(false);
            }
        }

        Pair dL = DivideConquer(pL, qL); //Recursive call
        Pair dR = DivideConquer(pR, qR); //Recursive call
        Pair minD;
        if(dL.getDistance() <= dR.getDistance()) { //Finds minimum distance for box
            minD = dL;
        } else {
            minD = dR;
        }

        int median = xSort[(int) Math.ceil( xSort.length / 2.0) - 1].getX(); //Finds the median x point

        Coordinate[] S = new Coordinate[ySort.length];
        int sLength = 0;
        for(int i = 0; i < ySort.length; i++) { //Copies all the points for with |x - m| < d into array S
            if(Math.abs(ySort[i].getX() - median) < minD.getDistance()) {
                S[sLength] = ySort[i];
                sLength++;
            }
        }

        Pair distMinSq = new Pair(minD.getC1(), minD.getC2(), Math.pow(minD.getDistance() , 2)); //stores square of the minimum distance

        for(int i = 0; i < ySort.length - 1; i++) {
            int k = i + 1;
            while(k < (sLength) && Math.pow((S[k].getY() - S[i].getY()), 2) < distMinSq.getDistance()) {
                if((Math.pow((S[k].getX() - S[i].getX()),2) + Math.pow((S[k].getY() - S[i].getY()),2)) < distMinSq.getDistance()) { //Finds minimum distance for box
                    distMinSq = new Pair(S[k], S[i], (Math.pow((S[k].getX() - S[i].getX()),2) + Math.pow((S[k].getY() - S[i].getY()),2)));
                }
                k++;
            }
        }

        distMinSq.setDistance(Math.sqrt(distMinSq.getDistance()));
        return distMinSq; //Returns closest coordinate pair and distance
    }

    //This method uses the distance formula to find the distance between two coordinates
    private double distance(Coordinate first, Coordinate second) {
        return Math.sqrt(Math.pow((second.getX() - first.getX()),2) + Math.pow((second.getY() - first.getY()),2));
    }

    /*
    QUICKSORT method to create sorted arrays for x and y
    initially: start is 0, end is data.length - 1
    */
    public void quicksort(Coordinate[] data, int start, int end, boolean isX ) {
        if (start >= end) {
            return;
        }
        Coordinate pivot;
        int front = start;
        int back = end;
        if (isX) { //Sorts by x values
            pivot = data[start];
            while (true) {
                while ((front <= end) && (data[front].getX() <= pivot.getX())) {
                    front++;
                }
                while ((back >= start) && (data[back].getX() > pivot.getX())) {
                    back--;
                }
                if (back < front) break;
                Coordinate swap = data[front];
                data[front] = data[back];
                data[back] = swap;
            }

            data[start] = data[back];
            data[back] = pivot;
            quicksort(data, start, back - 1, true);
            quicksort(data, back + 1, end, true);
        } else { //Sorts by y values
            pivot = data[start];
            while (true) {
                while ((front <= end) && (data[front].getY() <= pivot.getY())) {
                    front++;
                }
                while ((back >= start) && (data[back].getY() > pivot.getY())) {
                    back--;
                }
                if (back < front) break;
                Coordinate swap = data[front];
                data[front] = data[back];
                data[back] = swap;
            }

            data[start] = data[back];
            data[back] = pivot;
            quicksort(data, start, back - 1, false);
            quicksort(data, back + 1, end, false);
        }
    }

}

/*
 *  This class is used to create the object for coordinates
 */
class Coordinate {
    private int x; // X-axis location
    private int y; // Y-axis location
    private boolean inLeft = false;

    //Default constructor
    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Coordinate(int x, int y, boolean inLeft) {
        this.x = x;
        this.y = y;
        this.inLeft = inLeft;
    }

    // Getter methods for x and y coordinates
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isInLeft() {
        return inLeft;
    }

    //Setter methods for x and y coordinates
    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setInLeft(boolean inLeft) {
        this.inLeft = inLeft;
    }

    //@Override toString method
    @Override
    public String toString() {
        return "( " + x + ", " + y + " )";
    }

}

/*
 *  This class is used to pair coordinates together with their respective distances
 */
class Pair {
    private Coordinate c1; //First coordinate
    private Coordinate c2; //Second coordinate
    private double distance; //distance between two coordinates

    public Pair(Coordinate c1, Coordinate c2, double distance) {
        this.c1 = c1;
        this.c2 = c2;
        this.distance = distance;
    }

    public Pair(double distance) {
        this.distance = distance;
    }

    public Coordinate getC1() {
        return c1;
    }

    public Coordinate getC2() {
        return c2;
    }

    public double getDistance() {
        return distance;
    }

    public void setC1(Coordinate c1) {
        this.c1 = c1;
    }

    public void setC2(Coordinate c2) {
        this.c2 = c2;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}