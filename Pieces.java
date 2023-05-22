import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Circle; 
import com.badlogic.gdx.graphics.*; 

public class Pieces extends Circle
{
    Texture color;
    public Pieces(float x,float y, Texture c)
    {
        super(x,y,(float)70);
        
            color = c; 
    }

    public Texture getTexture()
    {
        return color;
    }

    public float getX()
    {
        return this.x;
    }
    
    public float getY()
    {
        return this.y;
    }
    
}