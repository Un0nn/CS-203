import java.util.Random;
import java.util.Arrays;
/*
 * N-Queens Problem by Adam Hayen
 * Id: 702047114
 * CS-203
 */
public class SolverNQueens {
    public static void main(String[] args) {

        int n = 11; //Initializes size n queens
        
        final long startTime1 = System.currentTimeMillis(); //Starts timer

        ExhaustiveSearch dumb = new ExhaustiveSearch(n); //Creates object to run the class, passes in n as the size
        dumb.PopulateBoard(); //Populates the board
        dumb.ArrangeBoard(); //Solves the board

        final long endTime1 = System.currentTimeMillis(); //Stops timer
        dumb.Print(); //Prints results

        System.out.println("\nExhaustive search time: " + (endTime1 - startTime1) + " milliseconds");


        //Iterative repair
        n = 25; //Set n to a new value
        final long startTime2 = System.currentTimeMillis(); //Starts the timer
        IterativeRepair smart;
        do {
            smart = new IterativeRepair(n); //Passes in n as the size
            smart.ArrangeBoard(); //Solves the board
        } while (smart.totalCollisions() != 0); //Double checks that a valid solution has been found to avoid incorrect boards caused by randomness
        final long endTime2 = System.currentTimeMillis(); //Stops the timer
        smart.Print(); //prints interative repair results


        System.out.println("\nIterative repair time: " + (endTime2 - startTime2) + " milliseconds");
        
    }
}


class ExhaustiveSearch {
    /*
        This array stores the positions of queens on the board.
        Queens will never leave their columns, so the column does not need to be cleared.
        The index of the queen is the column number
     */
    int[] Queens; 
    long operations = 0; //Stores the basic operation count

    //Default Constructor
    public ExhaustiveSearch (){
        Queens = new int [4]; //Default size is 4
    }

    //Constructor with size of the chess board
    public ExhaustiveSearch (int size){
        Queens = new int [size]; //Sets to user input size
    }

    //Places a queen in each column
    public void PopulateBoard() {
        //Fills every column
        Arrays.fill(Queens, 0);
    }

    public void ArrangeBoard() {
        RecursiveChange(0); //Starts at first column of queens, calls recursive function
    }

    private void RecursiveChange(int queen) { //Passes in column of queen
        if(queen == Queens.length) { //Base case, we've gone too far, go back
            return;
        }
        RecursiveChange(queen+1); //Recursively moves down the array.

        if(ValidBoard()) { return; } //Checks for valid board to see if problem is solved

        while(Queens[queen] < Queens.length-1) { //This loop moves the queens up a row
            Queens[queen]++; //Moves the queen up one row
            RecursiveChange(queen+1);//Recursively moves down to check again
            if(ValidBoard()) { return; } //Checks for valid board to see if problem is solved
        }
        Queens[queen] = 0; //Resets queen if all loops don't work
    }

    public boolean ValidBoard() { //This function checks for a valid board by looking at all of the queens
        for(int i = 0; i < Queens.length; i++) {
            if (!ValidPosition(i)) {
                return false;
            }
        }
        return true;
    }

    public boolean ValidPosition(int queen) { //Looks at the horizontal and diagonal positions
        return (RowClear(Queens[queen]) && DiagonalClear(Queens[queen], queen));
    }

    //This method checks a given row for existing queens, returns true if there is only one queen in the row
    public boolean RowClear(int row) {
        int queenCount = 0;
        for (int i = 0; i < Queens.length; i++) {
            if (Queens[i] == row) {
                queenCount++;
            }
            operations++;
        }
        return (queenCount == 1);
    }

    //This method checks a diagonals for existing queens
    public boolean DiagonalClear(int row, int column) {
        int r = row+1;
        int c = column-1;
        //Up, left
        while(r <= Queens.length && c >= 0 ) {
            if(PositionNotClear(r, c)) {
                return false; //Queen is in the way
            }
            r++;
            c--;
        }

        r = row-1;
        c = column+1;
        //down, right
        while(r >= 0 && c < Queens.length ) {
            if(PositionNotClear(r, c)) {
                return false; //Queen is in the way
            }
            r--;
            c++;
        }

        r = row+1;
        c = column+1;
        //Up, right
        while(r < Queens.length && c < Queens.length ) {
            if(PositionNotClear(r, c)) {
                return false; //Queen is in the way
            }
            r++;
            c++;
        }

        r = row-1;
        c = column-1;
        //Down, left
        while(r >= 0 && c >= 0) {
            if(PositionNotClear(r, c)) {
                return false; //Queen is in the way
            }
            r--;
            c--;
        }
        return true; //Diagonals are clear
    }

    //Checks exact spot for existing queen, used for diagonal checks
    public boolean PositionNotClear(int row, int column) {
        operations++;
        return Queens[column] == row;
    }

    public void Print() { //Prints the results by column, row
        System.out.println("Exhaustive search (Column, row): ");
        for(int i = 0; i < Queens.length; i++) {
            System.out.println(i + ", " + Queens[i]);
        }
        System.out.println("Basic operations: " + operations);
    }
}


class IterativeRepair {
    /*
        This array stores the positions of queens on the board.
        Queens will never leave their columns, so the column does not need to be cleared.
        The index of the queen is the column number
    */
    int[] Queens;
    long operations = 0; //Stores the basic operation count

    //Default Constructor
    public IterativeRepair(){
        Queens = new int [4]; //Default size is 4
        for(int i = 0; i < Queens.length; i++) {
            Queens[i] = i;
        }
    }

    //Constructor with size of the chess board
    public IterativeRepair(int size){
        Queens = new int [size]; //Sets to user input size
        for(int i = 0; i < size; i++) {
            Queens[i] = i;
        }
        Random rand = new Random();
        for(int i = 0; i < size; i++) { //Randomizes the board to improve efficiency
            int randIndex = rand.nextInt(size);
            int temp = Queens[i];
            Queens[i] = Queens[randIndex];
            Queens[randIndex] = temp;
        }
    }

    //This method arranges the queens into their proper positions
    public void ArrangeBoard() {
        int[] posDiag = new int[2 * Queens.length - 1]; //Contains the number of queens in positive diagonal
        int[] negDiag = new int[2 * Queens.length - 1]; //Contains the number of queens in negative diagonal
        for(int i = 0; i < Queens.length; i++) {
            posDiag[Queens[i] + i] += 1; //Finds each of the queens, and calculates its diagonal
            negDiag[Math.abs((Queens.length-1) - i) + Queens[i]] += 1;
        }


        boolean swapPerformed;
        do {
            swapPerformed = false;
            for(int i = 0; i < Queens.length; i++) {
                for(int j = i + 1; j < Queens.length; j++) { //Iterates through two elements of the board
                    int queen1Collisions = posDiag[Queens[i] + i] - 1 + negDiag[Math.abs((Queens.length-1) - i) + Queens[i]] - 1; //Stores collisions of queen 1
                    int queen2Collisions = posDiag[Queens[j] + j] - 1 + negDiag[Math.abs((Queens.length-1) - j) + Queens[j]] - 1; //Stores collisions of queen 2
                    //Checks if there are any collisions
                    if(queen1Collisions != 0 || queen2Collisions != 0) {
                        //Checks if new position results in less collisions
                        int newCollision1 = (posDiag[Queens[j] + i] + 1) - 1 + (negDiag[Math.abs((Queens.length-1) - i) + Queens[j]] + 1) - 1;
                        int newCollision2 = (posDiag[Queens[i] + j] + 1) - 1 + (negDiag[Math.abs((Queens.length-1) - j) + Queens[i]] + 1) - 1;
                        operations++;
                        if((newCollision1 + newCollision2) < (queen1Collisions + queen2Collisions)) { //If swap will result in a better board
                            //Remove old queen from position
                            posDiag[Queens[i] + i] -= 1;
                            negDiag[Math.abs((Queens.length-1) - i) + Queens[i]] -= 1;
                            posDiag[Queens[j] + j] -= 1;
                            negDiag[Math.abs((Queens.length-1) - j) + Queens[j]] -= 1;

                            //Swaps queens
                            int temp = Queens[i];
                            Queens[i] = Queens[j];
                            Queens[j] = temp;
                            swapPerformed = true;

                            //Add new queen to position
                            posDiag[Queens[i] + i] += 1;
                            negDiag[Math.abs((Queens.length-1) - i) + Queens[i]] += 1;
                            posDiag[Queens[j] + j] += 1;
                            negDiag[Math.abs((Queens.length-1) - j) + Queens[j]] += 1;
                        }
                    }
                }
            }
        } while(swapPerformed);
    }

    //This method is used as a check to know if the problem has been solved correctly
    public int totalCollisions() {
        int[] posDiag = new int[2 * Queens.length - 1]; //Contains the number of queens in positive diagonal
        int[] negDiag = new int[2 * Queens.length - 1]; //Contains the number of queens in negative diagonal
        for(int i = 0; i < Queens.length; i++) {
            posDiag[Queens[i] + i] += 1; //Finds each of the queens, and calculates its diagonal
            negDiag[Math.abs((Queens.length-1) - i) + Queens[i]] += 1;
        }

        int totalCollisions = 0;
        for(int i = 0; i < (2 * Queens.length - 1); i++) { //calculates collisions in the diagonal
            if(posDiag[i] > 1) {
                totalCollisions += posDiag[i] - 1;
            }
            if(negDiag[i] > 1) {
                totalCollisions += negDiag[i] - 1;
            }
        }
        return totalCollisions;
    }

    public void Print() { //This method prints the queens by column, row
        System.out.println("\nIterative Repair (Column, row):");
        for(int i = 0; i < Queens.length; i++) {
            System.out.println(i + ", " + Queens[i]);
        }
        System.out.println("Basic operations: " + operations);
    }
}
