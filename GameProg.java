//API Link - https://javadoc.io/doc/com.badlogicgames.gdx/gdx/latest/index.html
//API Link - https://javadoc.io/doc/com.badlogicgames.gdx/gdx/latest/index.html
import com.badlogic.gdx.ApplicationAdapter; 
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer; 
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle; 
import com.badlogic.gdx.math.Circle; 
import com.badlogic.gdx.Input.Keys; 
import com.badlogic.gdx.math.Vector2; 
import com.badlogic.gdx.math.MathUtils; 
import com.badlogic.gdx.math.Intersector; 
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.*; 
import com.badlogic.gdx.graphics.*; 
import java.util.*;

//NOTE: Always reset the JVM before compiling (it is the small loop arrow in the
//bottom right corner of the project window)!! 

public class GameProg
extends ApplicationAdapter 
{
    private OrthographicCamera camera; //the camera to our world
    private Viewport viewport; //maintains the ratios of your world
    private ShapeRenderer renderer; //used to draw textures and fonts 
    private GameState gamestate;
    private BitmapFont font; 
    private ArrayList<Texture> images;
    private Rectangle button1;
    private Rectangle button2;

    //variables to get mouse loc
    private Vector2 mouseVector;
    private float mouseX;
    private float mouseY;
    private Circle mouseLoc; //find intersection of mouse and rect

    private Pieces[][] whitePieces; //2D array for white pieces

    private Pieces[][] blackPieces; //2D array for black pieces

    private int renderCtr;
    private int playerToggle; 
    private boolean isFinished;

    private Pieces clicked;

    private SpriteBatch batch;
    private GlyphLayout layout; 
    private ColoredRect [][] board;
    public static final float WORLD_WIDTH = 640; 
    public static final float WORLD_HEIGHT = 640;

    @Override//called once when we start the game
    public void create(){
        // idk what this is but it seems damaging please comment out if im wrong
        //Gdx.graphics.setContinuousRendering(false);

        camera = new OrthographicCamera(); 
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera); 
        renderer = new ShapeRenderer();
        board = new ColoredRect[8][8];
        batch = new SpriteBatch();
        layout = new GlyphLayout();
        font = new BitmapFont(); 
        images = new ArrayList<Texture>();
        images.add(new Texture("PlayButtonSelected.gif"));//0
        images.add(new Texture("PlayButtonUnselected.png"));//1
        images.add(new Texture("MenuBG.gif"));//2
        images.add(new Texture("RulesButtonUnselected.png"));//3
        images.add(new Texture("RulesHighlighted.gif"));//4
        images.add(new Texture("Checkers.png"));//5
        images.add(new Texture("Rules.png"));//6
        images.add(new Texture("WhiteChecker.png"));//7
        images.add(new Texture("BlackChecker.png"));//8
        images.add(new Texture("BlackWin.png"));//9
        images.add(new Texture("WhiteWin.png"));//10
        button1 = new Rectangle(WORLD_WIDTH/2 -images.get(0).getWidth()/2,//x loc
            (WORLD_HEIGHT/10)*6,//y loc
            images.get(0).getWidth(), images.get(0).getHeight());
        button2  = new Rectangle(WORLD_WIDTH/2 -images.get(3).getWidth()/2,//x loc
            WORLD_HEIGHT/4,//y loc
            images.get(3).getWidth(), images.get(3).getHeight());

        //variables to get mouse loc
        mouseVector = new Vector2();
        mouseX = 0;
        mouseY = 0;
        mouseLoc = new Circle(0,0,.1f); //find intersection of mouse and rect 

        whitePieces = new Pieces[8][8];
        blackPieces = new Pieces[8][8];

        renderCtr=0;
        playerToggle = 0;
        isFinished =false;

        gamestate = GameState.MENU; 
        clicked = null;
    }

    @Override//called 60 times a second
    public void render(){
        //these 2 lines clear the screen and set the background color every FRAME. 
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(gamestate == GameState.GAME)
        {
            drawBoard();//draws checkerboard
            if(playerToggle%2==0)
            {     
                whiteLogic();//logic for moving white pieces
            }
            else
            {
                blackLogic();//logic for moving black pieces
            }
            if(checkWhitePieces())
                gamestate=GameState.BLACKWIN;
            if(checkBlackPieces())
                gamestate=GameState.WHITEWIN;
        }

        if(Gdx.input.isKeyJustPressed(Keys.ESCAPE))
            gamestate=GameState.MENU;

        if(gamestate == GameState.MENU)
        {
            drawMenu();

            updateMouseLoc();

            if(Intersector.overlaps(mouseLoc,button1) && Gdx.input.justTouched())
            {
                gamestate = GameState.JUSTSTARTED; 
            }
            if(Intersector.overlaps(mouseLoc,button2) && Gdx.input.justTouched())
            {
                gamestate = GameState.INSTRUCTIONS;
            }
        }

        if(gamestate == GameState.JUSTSTARTED)
        {
            if(renderCtr<1)
            {
                fillBoard();
                renderCtr++;//prevents board being constructed >1x
            }
            if(!Gdx.input.isTouched())
                gamestate = GameState.GAME;
        }

        if(gamestate == GameState.INSTRUCTIONS)
            drawInstructions();

        if(gamestate == GameState.BLACKWIN)
            drawBlackWin();
        if(gamestate == GameState.WHITEWIN)
            drawWhiteWin();
    }

    public void updateMouseLoc()
    {
        mouseVector.set(Gdx.input.getX(), Gdx.input.getY());//doesn't match world coords
        mouseX=viewport.unproject(mouseVector).x;
        mouseVector.set(Gdx.input.getX(), Gdx.input.getY());//doesn't match world coords

        mouseY=viewport.unproject(mouseVector).y;
        mouseLoc.setX(mouseX);
        mouseLoc.setY(mouseY);
        // System.out.println("x: " + mouseLoc.x + "\ty: " + mouseLoc.y + "\tbx: " + mouseLoc.x + "\ty:" + mouseLoc.y);
    }

    private void drawMenu()
    {
        batch.begin();
        updateMouseLoc();

        batch.draw(images.get(2),0,0,WORLD_WIDTH,WORLD_HEIGHT);//Background

        batch.draw(images.get(5),WORLD_WIDTH/2-images.get(5).getWidth()/2, WORLD_HEIGHT/2, 
            images.get(5).getWidth(), images.get(5).getHeight());//"Checkers"

        if(Intersector.overlaps(mouseLoc, button1))
        {
            batch.draw(images.get(0),button1.x, button1.y-50, button1.width,button1.height);           
        }
        else
        {
            batch.draw(images.get(1),button1.x, button1.y-50, button1.width,button1.height);           
        }

        if(Intersector.overlaps(mouseLoc, button2))
        {
            batch.draw(images.get(4),button2.x, button2.y, button2.width,button2.height);
        }
        else
        {
            batch.draw(images.get(3),button2.x, button2.y, button2.width,button2.height);
        }   

        batch.end();
    }

    private void drawInstructions()
    {
        batch.begin();

        batch.draw(images.get(6),WORLD_WIDTH/2-images.get(6).getWidth()/2, 
            WORLD_HEIGHT/2, images.get(6).getWidth(),images.get(6).getHeight());
        font.setColor(1f, 0f, 0f, 1f);
        layout.setText(font,"The game alternates between the white and black pieces\n");
        font.draw(batch,
            layout, 
            WORLD_WIDTH / 2 - layout.width / 2, 
            WORLD_HEIGHT/2-20 + layout.height/2); font.setColor(1f, 0f, 0f, 1f);
        layout.setText(font,"Each player starts with 12 pieces, and the goal of the game is to capture all of your opponent's pieces\n");
        font.draw(batch,
            layout, 
            WORLD_WIDTH / 2 - layout.width / 2, 
            WORLD_HEIGHT/2-50 + layout.height/2); font.setColor(1f, 0f, 0f, 1f);
        layout.setText(font,"To do this, each player will move diagonally along the black spaces\n");
        font.draw(batch,
            layout, 
            WORLD_WIDTH / 2 - layout.width / 2, 
            WORLD_HEIGHT/2-80+ layout.height/2); font.setColor(1f, 0f, 0f, 1f);
        layout.setText(font,"If a player encounters an enemy piece diagonal to them, with a vacant spot afterward");
        font.draw(batch,
            layout, 
            WORLD_WIDTH / 2 - layout.width / 2, 
            WORLD_HEIGHT/2-110 + layout.height-10); font.setColor(1f, 0f, 0f, 1f);
        layout.setText(font,"the player will \"jump\" the enemy piece and capture it");
        font.draw(batch,
            layout, 
            WORLD_WIDTH / 2 - layout.width / 2, 
            WORLD_HEIGHT/2-135 + layout.height/2); font.setColor(1f, 0f, 0f, 1f);

        batch.end();
    }

    private void fillBoard()
    {
        int ctr = 0;
        for(int r = 0; r<board.length; r++)
        {
            for(int c = 0; c<board[0].length; c++)
            {
                if(ctr%2==0)
                    board[r][c] = new ColoredRect(r,c,80, new Color(Color.WHITE));
                else
                    board[r][c] = new ColoredRect(r,c,80,new Color(Color.BLACK));
                ctr++;
            }
            ctr++;
        }
        for(int r = 0; r<8; r++)
        {
            for(int c = 0; c<8; c++)
            {             
                if(board[r][c].getColor().equals(Color.BLACK) && r<3)
                {
                    whitePieces[r][c] = new Pieces(15+80*c, //x location depends on the column
                        WORLD_HEIGHT - 65 - r*80,images.get(7));
                    // System.out.println("White Piece at " + r +" "+ c +" constructed");
                }

                if(board[r][c].getColor().equals(Color.BLACK) && r>=5)
                {
                    blackPieces[r][c] = new Pieces(15+80*c, //x location depends on the column
                        WORLD_HEIGHT - 65 - r*80,images.get(8));
                    // System.out.println("Black Piece at " +r+" "+c+" constructed");
                }
            }
        }
    }

    public void whiteLogic()
    {
        System.out.println("WhiteLogic Called");
        if(isFinished==false)
        {
            clicked = null;
        }
        int oldR = -1;
        System.out.println("isFinished: "+isFinished);
        updateMouseLoc();
        if(!isFinished) 
            for(int r=0; r<board.length; r++)
            {
                for(int c = 0; c<board[0].length; c++)
                {
                    if(whitePieces[r][c]!=null && Gdx.input.isTouched() &&
                    Intersector.overlaps(mouseLoc,whitePieces[r][c]))
                    {
                        System.out.println("If statement called");
                        isFinished = true;
                        System.out.println("Clicked");
                        clicked = whitePieces[r][c];
                        oldR = r;

                        break;
                    } 
                }
                System.out.println();
                if(isFinished)
                {
                    System.out.println("If statement finished");
                    break;
                }
            }

        System.out.println("isFinished: "+isFinished);
        if(isFinished)
        {
            System.out.println("Finishing");

            if(Gdx.input.isTouched() && clicked!=null)
            {
                updateMouseLoc();
                System.out.println("Moving");
                clicked.set(mouseX,mouseY,50);
            }
            else
            {
                if(clicked!=null)
                {
                    updateMouseLoc();
                    whitePieces[(int)(640-mouseY)/80][(int)mouseX/80]=clicked;
                    clicked.set(((int)mouseX/80) * 80 + 15, (board.length - 1) * 80 - 80 * ((int)(640-mouseY)/80) + 15, 50);
                    System.out.println("Set at " +(int)(640-mouseY)/80 + " " + (int)mouseX/80);
                    isFinished = false;
                    playerToggle++;
                    checkWhitesCapture((int)(640-mouseY)/80, (int)mouseX/80, oldR);

                }
                if(clicked == null) {
                    // should not be called in an ideal situation
                    clicked = new Pieces(mouseX,mouseY,images.get(7));
                }
            }
        }

    }

    public void blackLogic()
    {
        System.out.println("BlackLogic Called");
        if(isFinished==false)
        {
            clicked = null;
        }

        updateMouseLoc();
        int oldR = 0;
        if(!isFinished) 

            for(int r=0; r<board.length; r++)
            {
                for(int c = 0; c<board[0].length; c++)
                {
                    if(blackPieces[r][c]!=null && Gdx.input.isTouched() 
                    && Intersector.overlaps(mouseLoc,blackPieces[r][c]))
                    {
                        System.out.println("If statement called");
                        isFinished = true;
                        System.out.println("Clicked");
                        clicked = blackPieces[r][c];
                        oldR = r;
                        break;
                    } 
                }
                if(isFinished)
                    break;
            }

        System.out.println("isFinished: "+isFinished);
        if(isFinished)
        {
            System.out.println("Finishing");
            if(Gdx.input.isTouched() && clicked!=null)
            {
                updateMouseLoc();
                System.out.println("Moving");
                clicked.set(mouseX,mouseY,50);
            }
            else
            {
                if(clicked!=null)
                {
                    updateMouseLoc();
                    blackPieces[(int)(640-mouseY)/80][(int)mouseX/80]=clicked;
                    clicked.set(((int)mouseX/80) * 80 + 15, (board.length - 1) * 80 - 80 * ((int)(640-mouseY)/80) + 15, 50);
                    System.out.println("Set at " +(int)(640-mouseY)/80 + " " + (int)mouseX/80);
                    isFinished = false;
                    playerToggle++;
                    checkBlacksCapture((int)(640-mouseY)/80, (int)mouseX/80,oldR);

                }
                if(clicked ==null)
                    clicked = new Pieces(mouseX,mouseY,images.get(8));
            }
        }
    }

    private void checkWhitesCapture(int r, int c, int r0)
    {
        if(r0-1>=0 && r-r0<0 && blackPieces[r-1][c+1]!=null)
            blackPieces[r-1][c+1]=null;
        else if(r+1<board.length && r-r0>0 && blackPieces[r+1][c+1]!=null)
            blackPieces[r+1][c+1]=null;
    }

    private void checkBlacksCapture(int r, int c, int r0)
    {
        if(r0-1>=0 && r-r0<0 && whitePieces[r-1][c-1]!=null)
            whitePieces[r-1][c-1]=null;
        else if(r+1<board.length && r-r0>0 && whitePieces[r+1][c-1]!=null)
            whitePieces[r+1][c-1]=null;
    }

    private void drawBoard()
    {
        renderer.begin(ShapeType.Filled);
        for(int r = 0; r < board.length; r++)
        {
            for(int c = 0; c < board[0].length; c++)
            {
                ColoredRect temp = board[r][c];
                renderer.setColor(((ColoredRect)temp).getColor());
                renderer.rect(c *80, //x location depends on the column
                    (board.length - 1) * 80 - 80 * r, //y location depends on the row
                    temp.width, temp.height);
            }
        }
        renderer.end();
        batch.begin();
        for(int r = 0; r<board.length; r++)
        {
            for(int c = 0; c<board[0].length; c++)
            {
                if(whitePieces[r][c]!=null)
                {
                    Pieces tempPiece = whitePieces[r][c];
                    batch.draw(tempPiece.getTexture(),
                        tempPiece.getX(), //x location depends on the column
                        tempPiece.getY(), //y location depends on the row
                        50, 50);
                }

                else if(blackPieces[r][c]!=null)
                {
                    Pieces tempPiece = blackPieces[r][c];

                    batch.draw(tempPiece.getTexture(),
                        tempPiece.getX(), //x location depends on the column
                        tempPiece.getY(), //y location depends on the row
                        50, 50);
                }
            }
        }
        batch.end();
    }

    private boolean checkWhitePieces()
    {
        for(int r = 0; r<whitePieces.length; r++)
            for(int c = 0; c<whitePieces[r].length; c++)
                if(whitePieces[r][c]!=null)
                    return false;

        return true;
    }

    private boolean checkBlackPieces()
    {
        for(int r = 0; r<blackPieces.length; r++)
            for(int c = 0; c<blackPieces[r].length; c++)
                if(blackPieces[r][c]!=null)
                    return false;

        return true;
    }

    private void drawBlackWin()
    {
        batch.begin();
        batch.draw(images.get(9),0,0,640,640);
        batch.end();
    }

    private void drawWhiteWin()
    {
        batch.begin();
        batch.draw(images.get(10),0,0,640,640);
        batch.end();
    }

    @Override
    public void resize(int width, int height)
    {  
        viewport.update(width, height, true);//API Link - https://javadoc.io/doc/com.badlogicgames.gdx/gdx/latest/index.html
    }
}

