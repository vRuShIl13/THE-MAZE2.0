import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;


public class Maze {
  
  public static void main(String[] args) throws Exception {
    /* Reads in a maze from the file TestMaze.txt. The first line
     * must contain n - the size of the maze (which must be square).
     * Then there must be n lines of n characters each, where X is
     * a valid place to go, and any other character is a "wall".
     * The starting point is always (0,0) and the goal point is
     * always (n-1,n-1). It draws and solves the maze graphically.
     */

    // Set up maze variables
    boolean[][] theMaze;
    Scanner inputFile = new Scanner(new File("TestMaze.txt"));
    int n = Integer.parseInt(inputFile.nextLine());
    theMaze = new boolean[n][n];
    // read in the maze Characters and translate them into boolean values. 
    for(int row=0; row<n; row++){
      String line = inputFile.nextLine();
      for(int col=0; col<n; col++) {
        if(line.charAt(col)=='X') {
          theMaze[row][col] =  true;
        }else{
          theMaze[row][col] =  false;
        }
      }
    }//for row
    
    // create a base solution array
    ArrayList<Coord> solution = solve(new ArrayList<Coord>(),
                                      new Coord(0,0),
                                      new Coord(n-1,n-1),
                                      theMaze);
    if(solution==null)
      System.out.println("You can't get there from here.");
    else
      System.out.println("The path is:\n"+solution);
  }//main
  
  
  //---------- a global variable to define the ways you can move ---------
  
  //The four directions that you can try to move in the maze
  private static Coord[] directions = {new Coord(-1,0),
                                       new Coord(1,0),
                                       new Coord(0,-1),
                                       new Coord(0,1)};
  
  
  //------------ the maze solver ---------------------------
  
  public static ArrayList<Coord> solve(ArrayList<Coord> path,
                                       Coord currentSpot,
                                       Coord goal,
                                       boolean[][] maze){
    /* Attempts to solve the given maze by getting to a goal spot,
     * from a currentSpot, which has been reached by the given path.
     * It will return the complete path to the goal, if one is found,
     * or null if there is no path to the goal from here.
     */
    
    //Make a copy of the path, because...
    ArrayList<Coord> newPath = (ArrayList<Coord>)(path.clone());
    newPath.add(currentSpot); //...you shouldn't touch the original parameter!
    
    // Draw graphics 
    displayProgress(path,currentSpot,goal,maze);
    
    if(currentSpot.equals(goal)){
      return newPath; //We made it!!
    }
    else {
      // Try each possible move ( all directions )
      for(int i=0; i<directions.length; i++){ 
         // add the next attempted direction to the current position to get the next location
        Coord nextSpot = currentSpot.addTo(directions[i]); //Where you get to
        if(nextSpot.validSpot(maze)) //Can we even go there?
          if(!newPath.contains(nextSpot)){ //Have we been there already?
              
            //The recursive call 
            ArrayList<Coord> solution = solve(newPath,nextSpot,goal,maze);
            
            // If we've found a solution, the recursion has returned and we have a sequence
            // that finds its way through the maze
            if(solution != null)
              return solution; //We're all done.
            else
              //Redraw the graphics again so you can see the "backtracking"
              displayProgress(path,currentSpot,goal,maze);
        }//ifs
      }//for
      return null;
    }//else (recursive case)
  }//solve
  
  
  //----- displayProgress --------------------------------
  // (Displays the current progress of the search graphically)
  public static void displayProgress(ArrayList<Coord> path,
                                     Coord currentSpot,
                                     Coord goal,
                                     boolean[][] maze){
    int nRows = maze.length; //  get Rows from base map
    int nCols = maze[0].length; // get cols from base map
    double rowStep = 1.0/nRows; // 
    double colStep = 1.0/nCols;
    // initialize drawing settings
    StdDraw.show(0);
    StdDraw.clear();
    StdDraw.rectangle(0.5,0.5,0.5,0.5);
    // for each tile in the maze, 
    for(int row=0; row<nRows; row++)
      for(int col=0; col<nCols; col++){
        // if maze value is false
        if(!maze[row][col])
          StdDraw.setPenColor(StdDraw.BLACK);
        
        // Current Location 
        else if(currentSpot.equals(row,col))
          StdDraw.setPenColor(StdDraw.RED);
        
        // If Goal Location
        else if(goal.equals(row,col))
          StdDraw.setPenColor(StdDraw.GREEN);
        
        // if on path  
        else if(path.contains(new Coord(row,col)))
          StdDraw.setPenColor(StdDraw.YELLOW);

        // Activated tile that is not a wall or special case
        else
          StdDraw.setPenColor(StdDraw.WHITE);
        
        // ? What are we drawing here? 
        double xPosition = (col+0.5) * rowStep;
        double yPosition = ((nRows-row-1)+0.5)*colStep;
        double rectWidth = rowStep/2.0;
        double rectHeight = colStep/2.0;
        
        StdDraw.filledRectangle(xPosition, yPosition, rectWidth, rectHeight);
        }
        
    // step slow
    StdDraw.show(200);
    
    // if mouse is held, pause execution
    while(StdDraw.mousePressed())
      StdDraw.show(20);
  }//displayProgress
  
}//class Maze




//This smaller non-public class will be very handy to
//store (row,col) pairs of co-ordinates in the maze.

class Coord {
  private int row;
  private int col;
  public Coord(int r, int c){
    row = r;
    col = c;
  }//constructor
  
  /* There is a subtle point here - the parameter of .equals
   * MUST always be Object or else it won't over-ride the superclass's
   * .equals method (the signatures will be different). And the
   * Object .equals method just compares references - not useful!
   */
  public boolean equals(Object other){
    return (row==((Coord)other).row)&&(col==((Coord)other).col);
  }//equals

  //This is a different kind of equals - only used in the display method
  public boolean equals(int r, int c){
    return (row==r)&&(col==c);
  }
  
  //This allows a coordinate to "move"
  public Coord addTo(Coord move){
    return new Coord(row+move.row,col+move.col);
  }//addTo
  
  //Is this coordinate somewhere you can go in this maze?
  public boolean validSpot(boolean[][] maze){
    if(row<0 || row>=maze.length || col<0 || col>=maze[0].length)
      return false; //It's "off the edge" in some direction
    else
      return maze[row][col];
  }//validSpot
  
  public String toString(){
    return "("+row+","+col+")";
  }
  
}//Coord class