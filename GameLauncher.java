import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import javax.swing.*; 
public class GameLauncher
{
    public static void main(String[] args)
    {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration(); 
        config.width = (int)GameProg.WORLD_WIDTH;//set the width of your screen window
        config.height = (int)GameProg.WORLD_HEIGHT; //set the height of your screen window
        //keep these the same ratio as your WORLD UNITS!!!!!!!!!!

        //Create an instance of the class that extends the Game class
        LwjglApplication launcher = new LwjglApplication(new GameProg(), config);
    }
}
