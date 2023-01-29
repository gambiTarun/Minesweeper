
import javax.swing.JFrame;

public class MineSweeperFixed {
   
   // You can  modify this program so it uses a different one of the hardcoded mines below, 
   // or add a new one, for testing purposes:
   
   private static boolean[][] smallMineField =
         {{false, false, false, false, false},
               {true, false, false, false, false},
               {false, false, false, false, true},
               {false, true, false, true, false}};
   
   private static boolean[][] emptyMineField = 
      {{false, false, false, false}, 
       {false, false, false, false}, 
       {false, false, false, false},
       {false, false, false, false}};
   
   private static boolean[][] almostEmptyMineField = 
      {{false, false, false, false}, 
       {false, false, false, false}, 
       {false, false, false, false},
       {false, true, false, false}};

   private static boolean[][] testMine1 =
         {{true,true}};



   private static final int FRAME_WIDTH = 400;
   private static final int FRAME_HEIGHT = 425;
      

   public static void main(String[] args) {

      JFrame frame = new JFrame();

      frame.setTitle("Minesweeper");

      frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);

      GameBoardPanel gameBoard = new GameBoardPanel(new VisibleField(new MineField(testMine1)));

      frame.add(gameBoard);

      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      frame.setVisible(true);

   }

}

