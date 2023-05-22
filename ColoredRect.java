import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle; 

public class ColoredRect extends Rectangle
{
    private Color c;

    public ColoredRect(int x, int y, int d, Color theColor) 
    {
        super(x,y,d,d);
        c = theColor;
    }

    public void changeColor(Color newColor)
    {
        c = newColor;
    }
    
    public Color getColor()
    {
        return c;
    }
}
