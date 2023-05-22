import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Circle; 
import com.badlogic.gdx.graphics.*;

public class CheckLogic 
{
    private int ctr;

    public CheckLogic()
    {
        ctr = 0;
    }

    

    public boolean whiteHasPieces(Pieces[][] white)
    {
        for(int r = 0; r<white.length; r++)
            for(int c = 0; c<white[0].length; c++)
                if(white[r][c]!=null)
                    return true;

        return false;            
    }

    public boolean blackHasPieces(Pieces[][] black)
    {
        for(int r = 0; r<black.length; r++)
            for(int c = 0; c<black[0].length; c++)
                if(black[r][c]!=null)
                    return true;

        return false;            
    }

    public Pieces getBPiece(Pieces[][] black,int r, int c)
    {
        return black[r][c];
    }

    public Pieces getWPiece(Pieces[][] white, int r, int c)
    {
        return white[r][c];
    }

   
}