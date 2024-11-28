package androidsamples.java.tictactoe;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
    @Test
    public void win_check(){
        GameFragment gm=new GameFragment();
        String[] inputText = new String[]{"X", "X", "X", "O", "O", "", "", "", ""};
        gm.setBoard(inputText);
        assertEquals(gm.checkWin(),1);
    }
    @Test
    public void draw_check(){
        GameFragment gm=new GameFragment();
        String[] inputText = new String[]{"O", "O", "X", "X", "X", "O", "O", "X", "O"};
        gm.setBoard(inputText);
        assertEquals(gm.checkWin(),0);
    }
    @Test
    public void diagonal_win_check(){
        GameFragment gm=new GameFragment();
        String[] inputText = new String[]{"X", "O", "X", "O", "X", "0", "X", "O", "X"};
        gm.setBoard(inputText);
        assertEquals(gm.checkWin(),1);
    }
    @Test
    public void loss_check(){
        GameFragment gm=new GameFragment();
        String[] inputText = new String[]{"X", "X", "", "O", "O", "O", "", "", "X"};
        gm.setBoard(inputText);
        assertEquals(gm.checkWin(),-1);
    }

}