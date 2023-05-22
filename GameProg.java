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
    private Circle temp; //find intersection of mouse and rect

    public Pieces[][] whitePieces; //2D array for white pieces
    public Circle[][] whiteCircles; //corresponding array

    public Pieces[][] blackPieces; //2D array for black pieces
    public Circle[][] blackCircles; //corresponding array

    private boolean isCalled;

    private SpriteBatch batch;
    private GlyphLayout layout; 
    private ColoredRect [][] board;
    public static final float WORLD_WIDTH = 640; 
    public static final float WORLD_HEIGHT = 640;

    @Override//called once when we start the game
    public void create(){
        camera = new OrthographicCamera(); 
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera); 
        renderer = new ShapeRenderer();
        board = new ColoredRect[8][8];
        batch = new SpriteBatch();
        layout = new GlyphLayout();
        font = new BitmapFont(); 
        images = new ArrayList<Texture>();
        images.add(new Texture("PlayButtonSelected.gif"));
        images.add(new Texture("PlayButtonUnselected.png"));
        images.add(new Texture("MenuBG.gif"));
        images.add(new Texture("RulesButtonUnselected.png"));
        images.add(new Texture("RulesHighlighted.gif"));
        images.add(new Texture("Checkers.png"));
        images.add(new Texture("Rules.png"));
        images.add(new Texture("WhiteChecker.png"));
        images.add(new Texture("BlackChecker.png"));
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
        temp = new Circle(0,0,.1f); //find intersection of mouse and rect 

        whitePieces = new Pieces[8][8];
        whiteCircles = new Circle[8][8];
        blackPieces = new Pieces[8][8];
        blackCircles = new Circle[8][8];

        //CheckLogic class contains all logic for checkers
        gamestate = GameState.MENU;        
    }

    @Override//called 60 times a second
    public void render(){
        //these 2 lines clear the screen and set the background color every FRAME. 
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(viewport.getCamera().combined);           
        CheckLogic game = new CheckLogic();

        if(gamestate == GameState.GAME)
        {
            System.out.println("Calling Methods");

            whiteLogic();
        }

        if(Gdx.input.isKeyJustPressed(Keys.ESCAPE))
            gamestate=GameState.MENU;
        if(gamestate == GameState.MENU)
        {
            updateMouseLoc();

            if(Intersector.overlaps(temp,button1) && Gdx.input.justTouched())
            {
                gamestate = GameState.JUSTSTARTED; 
            }
            if(Intersector.overlaps(temp,button2) && Gdx.input.justTouched())
            {
                gamestate = GameState.INSTRUCTIONS;
            }
        }

        if(gamestate == GameState.JUSTSTARTED)
        {
            fillBoard();
            gamestate = GameState.GAME;
        }
        if(gamestate==GameState.MENU)
            drawMenu();
        if(gamestate == GameState.INSTRUCTIONS)
            drawInstructions();
        if(game.whiteHasPieces(whitePieces) && game.blackHasPieces(blackPieces))
        {   
            drawBoard();//draws checkerboard
        }
    }

    public void updateMouseLoc()
    {
        mouseVector.set(Gdx.input.getX(), Gdx.input.getY());//doesn't match world coords
        mouseX=viewport.unproject(mouseVector).x;
        mouseVector.set(Gdx.input.getX(), Gdx.input.getY());//doesn't match world coords

        mouseY=viewport.unproject(mouseVector).y;
        temp.setX(mouseX);
        temp.setY(mouseY);
        // System.out.println("x: " + temp.x + "\ty: " + temp.y + "\tbx: " + temp.x + "\ty:" + temp.y);
    }

    private void drawMenu()
    {
        batch.begin();
        updateMouseLoc();

        batch.draw(images.get(2),0,0,WORLD_WIDTH,WORLD_HEIGHT);//Background

        batch.draw(images.get(5),WORLD_WIDTH/2-images.get(5).getWidth()/2, WORLD_HEIGHT/2, 
            images.get(5).getWidth(), images.get(5).getHeight());//"Checkers"

        if(Intersector.overlaps(temp, button1))
        {
            batch.draw(images.get(0),button1.x, button1.y-50, button1.width,button1.height);           
        }
        else
        {
            batch.draw(images.get(1),button1.x, button1.y-50, button1.width,button1.height);           
        }

        if(Intersector.overlaps(temp, button2))
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
                if(board[r][c].getColor().equals(Color.BLACK) && r<2)
                {
                    whitePieces[r][c] = new Pieces(15+80*c, //x location depends on the column
                        WORLD_HEIGHT - 65 - r*80,images.get(7));
                    whiteCircles[r][c] = new Circle(15+80*c, WORLD_HEIGHT - 65 - r*80, 80);
                }

                if(board[r][c].getColor().equals(Color.BLACK) && r>=6)
                {
                    blackPieces[r][c] = new Pieces(15+80*c, //x location depends on the column
                        WORLD_HEIGHT - 65 - r*80,images.get(8));
                    blackCircles[r][c] = new Circle(15+80*c, WORLD_HEIGHT - 65 - r*80, 80);
                }
            }
        }
        System.out.println("Board Filled");
    }

    public void whiteLogic()
    {
        int timer;
        updateMouseLoc();
        System.out.println("WhiteLogic Called");
        Pieces clicked = null;
        int r = 0;
        int c = 0;

        for(timer=0; timer<1800; timer++) 
        {
            for(r = 0; r<whitePieces.length; r++)
                for(c = 0; c<whitePieces[0].length; c++)
                    if(whiteCircles[r][c]!=null && Intersector.overlaps(temp, whiteCircles[r][c]) && Gdx.input.isTouched())
                    {
                        clicked = whitePieces[r][c];
                        clicked.set(mouseX, mouseY, 85);

                        if(r+2<board.length && c+2<board[0].length &&
                        blackPieces[r+1][c+1]!=null && whitePieces[r+2][c+2]==null)
                        {
                            board[r+2][c+2].changeColor(Color.YELLOW);
                        }
                        if(r+1<board.length && c+1<board[0].length &&
                        blackPieces[r+1][c+1]==null && whitePieces[r+1][c+1]==null)
                        {
                            board[r+1][c+1].changeColor(Color.YELLOW);
                        }
                        if(r-2>board.length && c+2<board[0].length &&
                        blackPieces[r-1][c+1]!=null && whitePieces[r+2][c+2]==null)
                        {
                            board[r-2][c+2].changeColor(Color.YELLOW);
                        }
                        if(r-1>board.length && c+1<board[0].length &&
                        blackPieces[r-1][c+1]==null && whitePieces[r-1][c+1]==null)
                        {
                            board[r-1][c+1].changeColor(Color.YELLOW);
                        }
                    }
        }
        if((!Gdx.input.isTouched()) && board[(int)mouseX/80][(int)mouseY/80].equals(Color.YELLOW))
        {
            updateMouseLoc();
            Circle temp = whiteCircles[r][c];
            whitePieces[r][c] = null;
            whiteCircles[r][c] = null;
            whitePieces[(int)mouseY/80][(int)mouseX/80] = clicked;
            whiteCircles[(int)mouseY/80][(int)mouseX/80] = temp;
            clicked.set(15+80*mouseX,WORLD_HEIGHT - 65 - mouseY*80,50);
            if(blackPieces[(int)mouseX/80-1][(int)mouseY/80-1]!=null)
            {
                blackPieces[r-1][c-1]=null;
                blackCircles[r-1][c-1]=null;
            }
            else if(blackPieces[(int)mouseX/80+1][(int)mouseY/80-1]!=null)
            {
                blackPieces[r-1][c-1]=null;
                blackCircles[r-1][c-1]=null;
            }
        }
        blackLogic();      
    }

    public void blackLogic()
    {
        int timer;
        updateMouseLoc();
        System.out.println("BlackLogic Called");
        Pieces clicked = null;
        int r = 0;
        int c = 0;
        for(timer = 0; timer<1800; timer++)
        {
            for(r = 0; r<blackPieces.length; r++)
                for(c = 0; c<blackPieces[0].length; c++)
                    if(blackCircles[r][c]!=null && Intersector.overlaps(temp, blackCircles[r][c]) && Gdx.input.isTouched())
                    {
                        clicked = blackPieces[r][c];
                        clicked.set(mouseX, mouseY, 85);

                        if(r+2<board.length && c+2<board[0].length &&
                        whitePieces[r+1][c+1]!=null && blackPieces[r+2][c+2]==null)
                        {
                            board[r+2][c+2].changeColor(Color.YELLOW);
                        }
                        if(r+1<board.length && c+1<board[0].length &&
                        whitePieces[r+1][c+1]==null && blackPieces[r+1][c+1]==null)
                        {
                            board[r+1][c+1].changeColor(Color.YELLOW);
                        }
                        if(r-2>board.length && c+2<board[0].length &&
                        whitePieces[r-1][c+1]!=null && blackPieces[r+2][c+2]==null)
                        {
                            board[r-2][c+2].changeColor(Color.YELLOW);
                        }
                        if(r-1>board.length && c+1<board[0].length &&
                        whitePieces[r-1][c+1]==null && blackPieces[r-1][c+1]==null)
                        {
                            board[r-1][c+1].changeColor(Color.YELLOW);
                        }
                    }
        }

        if((!Gdx.input.isTouched()) && board[(int)mouseX/80][(int)mouseY/80].equals(Color.YELLOW))
        {
            updateMouseLoc();
            Circle temp = blackCircles[r][c];
            blackPieces[r][c] = null;
            blackCircles[r][c] = null;
            blackPieces[(int)mouseY/80][(int)mouseX/80] = clicked;
            blackCircles[(int)mouseY/80][(int)mouseX/80] = temp;
            clicked.set(15+80*mouseX,WORLD_HEIGHT - 65 - mouseY*80,50);
            if(whitePieces[(int)mouseX/80-1][(int)mouseY/80-1]!=null)
            {
                whitePieces[r-1][c-1]=null;
                whiteCircles[r-1][c-1]=null;
            }
            else if(whitePieces[(int)mouseX/80+1][(int)mouseY/80-1]!=null)
            {
                whitePieces[r-1][c-1]=null;
                whiteCircles[r-1][c-1]=null;
            }
        }

        whiteLogic();
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
                    Circle tempCircle = whiteCircles[r][c];
                    batch.draw(tempPiece.getTexture(),
                        tempPiece.getX(), //x location depends on the column
                        tempPiece.getY(), //y location depends on the row
                        50, 50);
                }

                else if(blackPieces[r][c]!=null)
                {
                    Pieces tempPiece = blackPieces[r][c];
                    Circle tempCircle = blackCircles[r][c];
                    batch.draw(tempPiece.getTexture(),
                        tempPiece.getX(), //x location depends on the column
                        tempPiece.getY(), //y location depends on the row
                        50, 50);
                }
            }
        }
        batch.end();
    }

    @Override
    public void resize(int width, int height)
    {  
        viewport.update(width, height, true);//API Link - https://javadoc.io/doc/com.badlogicgames.gdx/gdx/latest/index.html
    }
}

