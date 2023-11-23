class Symmetric {
    static int basicOp = 0;
    public static void main(String[] args) {
         
        int[][] array =  {  
                            new int[] { 0, 1, 1, 1, 5, 1},
                            new int[] { 1, 0, 1, 2, 1, 1},
                            new int[] { 1, 1, 0, 1, 6, 1},
                            new int[] { 1, 2, 1, 0, 1, 1},
                            new int[] { 5, 1, 6, 1, 0, 1},
                            new int[] { 1, 1, 1, 1, 1, 0}
                         };
                         
                        /* 
        int[][] array =  {  
            new int[] { 0, 1, 1 },
            new int[] { 1, 0, 2 },
            new int[] { 1, 1, 0 },
            };   */             
        boolean testRtn = isSymmetric(array);
        System.out.println(testRtn + "\n" + basicOp);

        basicOp = 0; //resets basic operation count
        testRtn = isSymmetric(array, 0, 1 );
        System.out.println(testRtn + "\n" + basicOp);
    }

    // Input: 2D square matrix A of size NxN.
    public static boolean isSymmetric( int[][] A ) {
        for( int i = 0; i < A.length-1; i++ ) {
            for( int j = i+1; j < A.length; j++ ) {
                basicOp++;
                if( A[i][j] != A[j][i] ) { return false; }
            }
        }
        return true;
    }
   /* 
    public static boolean isSymmetric( int[][] A, int i, int j) {
        if(i >= A.length || j >= A.length) { //Checks if out of bounds
            return true; //Went all the way through without asymmetry
        } 
        else if (A[i][j] != A[j][i]) {
            return false;
        }
        else if (j >= A.length) {

        }
        return isSymmetric(A, i + 1, i + 2);
    }   
    */
    
    public static boolean isSymmetric( int[][] A, int i, int j) {
        if(i == (A.length - 1) && j == (A.length - 1)) { //Base case, checks if at end
            return true; //Went all the way through without asymmetry
        } 
        basicOp++;
        if (A[i][j] != A[j][i]) { //Compares elements
            return false;
        } else if ( i == j ) { //Resets row, increases column
            return isSymmetric(A, 0, j + 1);  
        } else { //Iterate through row, keeps same column
            return isSymmetric(A, i + 1, j );  
        }
    }   
   
}