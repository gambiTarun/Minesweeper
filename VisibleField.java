
/**
 VisibleField class
 This is the data that's being displayed at any one point in the game (i.e., visible field, because it's what the
 user can see about the minefield). Client can call getStatus(row, col) for any square.
 It actually has data about the whole current state of the game, including
 the underlying mine field (getMineField()).  Other accessors related to game status: numMinesLeft(), isGameOver().
 It also has mutators related to actions the player could do (resetGameDisplay(), cycleGuess(), uncover()),
 and changes the game state accordingly.

 It, along with the MineField (accessible in mineField instance variable), forms
 the Model for the game application, whereas GameBoardPanel is the View and Controller, in the MVC design pattern.
 It contains the MineField that it's partially displaying.  That MineField can be accessed (or modified) from
 outside this class via the getMineField accessor.
 */
public class VisibleField {
   // ----------------------------------------------------------
   // The following public constants (plus numbers mentioned in comments below) are the possible states of one
   // location (a "square") in the visible field (all are values that can be returned by public method
   // getStatus(row, col)).

   // The following are the covered states (all negative values):
   public static final int COVERED = -1;   // initial value of all squares
   public static final int MINE_GUESS = -2;
   public static final int QUESTION = -3;

   // The following are the uncovered states (all non-negative values):

   // values in the range [0,8] corresponds to number of mines adjacent to this square

   public static final int MINE = 9;      // this loc is a mine that hasn't been guessed already (end of losing game)
   public static final int INCORRECT_GUESS = 10;  // is displayed a specific way at the end of losing game
   public static final int EXPLODED_MINE = 11;   // the one you uncovered by mistake (that caused you to lose)
   // ----------------------------------------------------------

   /**
    Representation invariant:

    // visField must be of the same size as minefield. That is, same number of rows and columns
    // minefield is fixed and non-mutable after getting initialized in the constructor.

    */

   private int[][] visField;
   private final MineField mineField;

   /**
    Create a visible field that has the given underlying mineField.
    The initial state will have all the mines covered up, no mines guessed, and the game
    not over.
    @param mineField  the minefield to use for this VisibleField
    */
   public VisibleField(MineField mineField) {

      this.mineField = mineField;
      resetGameDisplay();
   }


   /**
    Reset the object to its initial state (see constructor comments), using the same underlying
    MineField.
    */
   public void resetGameDisplay() {
      int rows = mineField.numRows();
      int cols = mineField.numCols();
      visField = new int[rows][cols];
      for(int i=0;i<rows;i++){
         for(int j=0;j<cols;j++){
            visField[i][j] = COVERED;
         }
      }
   }


   /**
    Returns a reference to the mineField that this VisibleField "covers"
    @return the minefield
    */
   public MineField getMineField() {

      return mineField;
   }


   /**
    Returns the visible status of the square indicated.
    @param row  row of the square
    @param col  col of the square
    @return the status of the square at location (row, col).  See the public constants at the beginning of the class
    for the possible values that may be returned, and their meanings.
    PRE: getMineField().inRange(row, col)
    */
   public int getStatus(int row, int col) {
      assert getMineField().inRange(row,col);
      return visField[row][col];
   }


   /**
    Returns the the number of mines left to guess.  This has nothing to do with whether the mines guessed are correct
    or not.  Just gives the user an indication of how many more mines the user might want to guess.  This value can
    be negative, if they have guessed more than the number of mines in the minefield.
    @return the number of mines left to guess.
    */
   public int numMinesLeft() {
      int guesses=0;
      int rows = mineField.numRows();
      int cols = mineField.numCols();
      for(int i=0;i<rows;i++){
         for(int j=0;j<cols;j++){
            if(visField[i][j]==MINE_GUESS){
               guesses++;
            }
         }
      }
      return getMineField().numMines()-guesses;
   }


   /**
    Cycles through covered states for a square, updating number of guesses as necessary.  Call on a COVERED square
    changes its status to MINE_GUESS; call on a MINE_GUESS square changes it to QUESTION;  call on a QUESTION square
    changes it to COVERED again; call on an uncovered square has no effect.
    @param row  row of the square
    @param col  col of the square
    PRE: getMineField().inRange(row, col)
    */
   public void cycleGuess(int row, int col) {
      assert getMineField().inRange(row,col);
      if(getStatus(row,col)==COVERED) {
         visField[row][col] = MINE_GUESS;
      }
      else if(getStatus(row,col)==MINE_GUESS){
         visField[row][col] = QUESTION;
      }
      else{
         visField[row][col] = COVERED;
      }
   }

   /**
    Uncovers this square and returns false iff you uncover a mine here.
    If the square wasn't a mine or adjacent to a mine it also uncovers all the squares in
    the neighboring area that are also not next to any mines, possibly uncovering a large region.
    Any mine-adjacent squares you reach will also be uncovered, and form
    (possibly along with parts of the edge of the whole field) the boundary of this region.
    Does not uncover, or keep searching through, squares that have the status MINE_GUESS.
    Note: this action may cause the game to end: either in a win (opened all the non-mine squares)
    or a loss (opened a mine).
    @param row  of the square
    @param col  of the square
    @return false   iff you uncover a mine at (row, col)
    PRE: getMineField().inRange(row, col)
    */
   public boolean uncover(int row, int col) {
      if(!getMineField().inRange(row, col) || isUncovered(row,col) || visField[row][col]==MINE_GUESS) {
         return true;
      }
      if(getMineField().hasMine(row,col)){
         gameLostVisible();
         visField[row][col] = EXPLODED_MINE;
         return false;
      }
      visField[row][col] = getMineField().numAdjacentMines(row, col);
      if((mineField.numCols()*mineField.numRows())-mineField.numMines()==numMinesUncovered()){
         gameWinVisible();
      }
      if(visField[row][col]>0) {
         return true;
      }
      uncover(row+1,col);
      uncover(row-1,col);
      uncover(row,col+1);
      uncover(row,col-1);
      uncover(row+1,col+1);
      uncover(row-1,col-1);
      uncover(row+1,col-1);
      uncover(row-1,col+1);
      return true;
   }


   /**
    Returns whether the game is over.
    (Note: This is not a mutator.)
    @return whether game over
    */
   public boolean isGameOver() {
      // Game Won condition: Total number of uncovered locations is equal to the number of non-mine locations.
      if((mineField.numCols()*mineField.numRows())-mineField.numMines()==numMinesUncovered()){
         return true;
      }
      // Game Lost condition: If there is any exploded mine in the field.
      int rows = mineField.numRows();
      int cols = mineField.numCols();
      for(int i=0;i<rows;i++){
         for(int j=0;j<cols;j++){
            if(visField[i][j]==EXPLODED_MINE){
               return true;
            }
         }
      }
      return false;
   }


   /**
    Returns whether this square has been uncovered.  (i.e., is in any one of the uncovered states,
    vs. any one of the covered states).
    @param row of the square
    @param col of the square
    @return whether the square is uncovered
    PRE: getMineField().inRange(row, col)
    */
   public boolean isUncovered(int row, int col) {
      assert getMineField().inRange(row,col);
      return !(visField[row][col]==COVERED || visField[row][col]==MINE_GUESS || visField[row][col]==QUESTION);
   }

   // <put private methods here>

   /**
    * This function updates the visField to the required state when the player wins the game.
    *
    */
   private void gameWinVisible(){
      int rows = mineField.numRows();
      int cols = mineField.numCols();
      for(int i=0;i<rows;i++){
         for(int j=0;j<cols;j++){
            if(!isUncovered(i,j) && mineField.hasMine(i,j))
               visField[i][j] = MINE_GUESS;
         }
      }
   }

   /**
    * This function updates the visField to the required state when the player loses the game.
    *
    */
   private void gameLostVisible(){
      int rows = mineField.numRows();
      int cols = mineField.numCols();
      for(int i=0;i<rows;i++){
         for(int j=0;j<cols;j++){
            if(visField[i][j]==MINE_GUESS && !mineField.hasMine(i,j))
               visField[i][j] = INCORRECT_GUESS;
            if(visField[i][j]!=MINE_GUESS && mineField.hasMine(i,j))
               visField[i][j] = MINE;
         }
      }
   }

   /**
    * This function returns the number of uncovered mine locations at the moment when it is called.
    *
    */
   private int numMinesUncovered(){
      int numUncover = 0;
      int rows = mineField.numRows();
      int cols = mineField.numCols();
      for(int i=0;i<rows;i++){
         for(int j=0;j<cols;j++){
            if(isUncovered(i,j)){
               numUncover++;
            }
         }
      }
      return numUncover;
   }
}
