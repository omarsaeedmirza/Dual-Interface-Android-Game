package game.stuff.eecs.omar.project;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

public class PaintPanel2 extends View implements View.OnTouchListener {

    final static String MYDEBUG = "MYDEBUG"; // for Log.i messages
    final static String TOTAL_TIME = "Total_time";
    final static int NUM_OF_VERSIONS = 4;

    // Create a static MediaPlayer object
    static MediaPlayer mp;

    final static int DEFAULT_LABEL_TEXT_SIZE = 20; // tweak as necessary
    final static float BALL_DIAMETER_ADJUST_FACTOR = 30; // the ball diameter will be min(width, height) / this_value
    final static int BOARD_SIZE = 5; // a 5 * 5 maze board
    final static int BOARD_LENGTH = 150; // each cell is 150 long.
    final static int RECT_GAP = 3;

    Bitmap ball, decodedBallBitmap;
    int ballDiameter, boardMargin, numOfPoints, current, wallHits, stepCount;
    joystick joyStick;

    RectF ballNow, startingRect, exitRect;

    float width, height;
    float xBall, yBall; // top-left of the ball (for painting)
    float xBallCenter, yBallCenter; // center of the ball
    float alpha;
    double velocityX, velocityY, totalTime;
    String directionOfControl, gain;
    int numberOfLaps;
    double experimentStartTime = -1;

    float xCenter, yCenter; // the center of the screen
    Paint labelPaint, backgroundPaint;

    boolean drawFlag;
    Point[][] cell;
    ArrayList<PaintPanel.Wall> walls;
    Vibrator vib;
    Activity activity;

    public PaintPanel2(Context contextArg)
    {
        super(contextArg);
        initialize(contextArg);
    }

    public PaintPanel2(Context contextArg, AttributeSet attrs)
    {
        super(contextArg, attrs);
        initialize(contextArg);
    }

    public PaintPanel2(Context contextArg, AttributeSet attrs, int defStyle)
    {
        super(contextArg, attrs, defStyle);
        initialize(contextArg);
    }

    // things that can be initialized from within this View
    private void initialize(Context c)
    {
        Log.i(MYDEBUG, "Inside Paint panel 2");

        numOfPoints = BOARD_SIZE + 1;
        wallHits = 0;
        stepCount = 0;

        joyStick = new joystick(100, 100, 80, 50);

        labelPaint = new Paint();
        labelPaint.setColor(Color.BLACK);
        labelPaint.setTextSize(DEFAULT_LABEL_TEXT_SIZE);
        labelPaint.setAntiAlias(true);

        backgroundPaint = new Paint();
        backgroundPaint.setColor(getResources().getColor(R.color.colorMaze));
        backgroundPaint.setStyle(Paint.Style.FILL);

        ballNow = new RectF();
        startingRect = new RectF();
        exitRect = new RectF();

        walls = new ArrayList<>();

        // NOTE: we'll create the actual bitmap in onWindowFocusChanged
        decodedBallBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pacman);

        vib = (Vibrator)c.getSystemService(Context.VIBRATOR_SERVICE);
    }

    /**
     * Called when the window hosting this view gains or looses focus.  Here we initialize things that depend on the
     * view's width and height.
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        Log.i(MYDEBUG, "onWindowFocusChanged - Paint Panel 2");

        if (!hasFocus)
            return;

        drawFlag = true;

        width = this.getWidth();
        height = this.getHeight();

        joyStick = new joystick((int)(width / 2), (int)(height * 0.75), 80, 50);

        // the ball diameter is nominally 1/30th the smaller of the view's width or height
        ballDiameter = width < height ? (int)(width / BALL_DIAMETER_ADJUST_FACTOR)
                : (int)(height / BALL_DIAMETER_ADJUST_FACTOR);

        // now that we know the ball's diameter, get a bitmap for the ball
        ball = Bitmap.createScaledBitmap(decodedBallBitmap, 60, 30, true);

        initCell();
        initGame((new Random()).nextInt(NUM_OF_VERSIONS)); // initialize the game by randomly picked number from 0 - 3

        // center of the view
        xCenter = width / 2f;
        yCenter = height / 2f;

        // center of the ball
        xBallCenter = xBall + ballDiameter / 2f;
        yBallCenter = yBall + ballDiameter / 2f;

        updateBallPosition(xBall, yBall);
        experimentStartTime = System.currentTimeMillis(); // start the experiment timer
    }

    private void initCell()
    {
        Log.i(MYDEBUG, "initializing cell");
        boardMargin = Math.round((width - BOARD_LENGTH * BOARD_SIZE) / 2);
        Log.i(MYDEBUG, String.valueOf(width));
        Log.i(MYDEBUG, String.valueOf(height));
        Log.i(MYDEBUG, String.valueOf(boardMargin));

        cell = new Point[numOfPoints][numOfPoints];
        for (int i = 0; i < numOfPoints; i++)
        {
            for (int j = 0; j < numOfPoints; j++)
            {
                cell[i][j] = new Point(boardMargin + j * BOARD_LENGTH, boardMargin + i * BOARD_LENGTH);
            }
        }
    }
    // initialize maze walls
    private void initGame(int version) {
        if (version == 0) {
            for (int i = 0; i < BOARD_SIZE; i++) {
                addWall(0, i, 0, i + 1);
            } // the top of the maze board
            for (int i = 0; i < BOARD_SIZE; i++) {
                addWall(5, i, 5, i + 1);
            } // the bottom of the maze board

            // draw all the horizontal walls
            addWall(1, 4, 1, 5);
            addWall(2, 1, 2, 2);
            addWall(2, 3, 2, 4);
            addWall(3, 0, 3, 1);
            addWall(3, 2, 3, 3);
            addWall(3, 4, 3, 5);
            addWall(4, 2, 4, 3);
            addWall(4, 3, 4, 4);
            addWall(5, 1, 5, 4);

            // draw all the vertical walls
            addWall(0, 1, 1, 1);
            addWall(0, 3, 1, 3);
            addWall(1, 2, 3, 2);
            addWall(2, 3, 4, 3);
            addWall(3, 1, 4, 1);
            addWall(4, 4, 5, 4);
            addWall(0, 0, 3, 0);
            addWall(4, 0, 5, 0);
            addWall(0, 5, 3, 5);
            addWall(4, 5, 5, 5);

            // starting point of the game
            xBall = boardMargin + BOARD_LENGTH / 2f - ballDiameter / 2f;
            yBall = (cell[3][0].y + cell[4][0].y) / 2f - ballDiameter / 2f;
            startingRect = getRectByTwoPoints(new PaintPanel.Wall(cell[3][0], cell[4][0]));

            // ending point of the game
            exitRect = getRectByTwoPoints(new PaintPanel.Wall(cell[3][5], cell[4][5]));
        } else if (version == 1) {
            for (int i = 0; i < BOARD_SIZE; i++) {
                addWall(0, i, 0, i + 1);
            } // the top of the maze board
            for (int i = 0; i < BOARD_SIZE; i++) {
                addWall(i, 0, i + 1, 0);
            } // the left margin of the board
            for (int i = 0; i < BOARD_SIZE; i++) {
                addWall(i, 5, i + 1, 5);
            } // the right margin of the board

            // draw all the horizontal walls
            addWall(1, 0, 1, 1);
            addWall(1, 3, 1, 5);
            addWall(2, 3, 2, 4);
            addWall(3, 0, 3, 1);
            addWall(3, 2, 3, 3);
            addWall(4, 2, 4, 4);
            addWall(5, 1, 5, 4);

            // draw all the vertical walls
            addWall(0, 2, 1, 2);
            addWall(2, 1, 3, 1);
            addWall(2, 2, 3, 2);
            addWall(2, 4, 4, 4);
            addWall(4, 1, 5, 1);
            addWall(3, 3, 5, 3);

            // starting point of the game
            xBall = boardMargin + BOARD_LENGTH / 2f - ballDiameter / 2f;
            yBall = (cell[5][0].y) - BOARD_LENGTH / 2f - ballDiameter / 2f;
            startingRect = getRectByTwoPoints(new PaintPanel.Wall(cell[5][0], cell[5][1]));

            // ending point of the game
            exitRect = getRectByTwoPoints(new PaintPanel.Wall(cell[5][4], cell[5][5]));
        } else if(version == 2) {
            for (int i = 0; i < BOARD_SIZE; i++) {
                addWall(i, 0, i + 1, 0);
            } // the left margin of the board
            for (int i = 0; i < BOARD_SIZE; i++) {
                addWall(i, 5, i + 1, 5);
            } // the right margin of the board

            // draw all the horizontal walls
            addWall(0, 0, 0, 2);
            addWall(0, 3, 0, 5);
            addWall(1, 0, 1, 2);
            addWall(1, 3, 1, 4);
            addWall(2, 2, 2, 4);
            addWall(3, 0, 3, 1);
            addWall(3, 2, 3, 3);
            addWall(4, 1, 4, 4);
            addWall(5, 0, 5, 2);
            addWall(5, 3, 5, 5);

            // draw all the vertical walls
            addWall(0, 3, 1, 3);
            addWall(1, 2, 2, 2);
            addWall(2, 1, 3, 1);
            addWall(3, 2, 4, 2);
            addWall(3, 4, 4, 4);
            addWall(4, 3, 5, 3);

            // starting point of the game
            xBall = (cell[0][2].x + cell[0][3].x) / 2f - ballDiameter / 2f;
            yBall = boardMargin + BOARD_LENGTH / 2f - ballDiameter / 2f;
            startingRect = getRectByTwoPoints(new PaintPanel.Wall(cell[0][2], cell[0][3]));

            // ending point of the game
            exitRect = getRectByTwoPoints(new PaintPanel.Wall(cell[5][2], cell[5][3]));
        } else if(version == 3) {
            for (int i = 0; i < BOARD_SIZE; i++) {
                addWall(i, 0, i + 1, 0);
            } // the left margin of the board
            for (int i = 0; i < BOARD_SIZE; i++) {
                addWall(i, 5, i + 1, 5);
            } // the right margin of the board

            // draw all the horizontal walls
            addWall(0, 0, 0, 4);
            addWall(1, 1, 1, 3);
            addWall(1, 4, 1, 5);
            addWall(2, 1, 2, 2);
            addWall(3, 3, 3, 4);
            addWall(4, 0, 4, 1);
            addWall(4, 2, 4, 4);
            addWall(5, 0, 5, 3);
            addWall(5, 4, 5, 5);

            // draw all the vertical walls
            addWall(0, 1, 1, 1);
            addWall(1, 3, 2, 3);
            addWall(1, 4, 3, 4);
            addWall(2, 1, 4, 1);
            addWall(3, 2, 4, 2);
            addWall(4, 4, 5, 4);

            // starting point of the game
            xBall = (cell[0][4].x + cell[0][5].x) / 2f - ballDiameter / 2f;
            yBall = boardMargin + BOARD_LENGTH / 2f - ballDiameter / 2f;
            startingRect = getRectByTwoPoints(new PaintPanel.Wall(cell[0][4], cell[0][5]));

            // ending point of the game
            exitRect = getRectByTwoPoints(new PaintPanel.Wall(cell[5][3], cell[5][4]));
        }

    }
    private void addWall(int row1, int col1, int row2, int col2) {
        walls.add(new PaintPanel.Wall(cell[row1][col1], cell[row2][col2]));
    }

    /*
     * Update the ball position based on the xPosition and yPosition
     */
    public void updateBallPosition(float xPosition, float yPosition)
    {
        Log.i(MYDEBUG, "updateBallPosition - Paint Panel 2");

        xBall = xPosition;
        yBall = yPosition;

        // make an adjustment, if necessary, to keep the ball visible (also, restore if NaN)
        if (Float.isNaN(xBall) || xBall < 0)
            xBall = 0;
        else if (xBall > width - ballDiameter)
            xBall = width - ballDiameter;
        if (Float.isNaN(yBall) || yBall < 0)
            yBall = 0;
        else if (yBall > height - ballDiameter)
            yBall = height - ballDiameter;

        // oh yea, don't forget to update the coordinate of the center of the ball (needed to determine wall hits)
        xBallCenter = xBall + ballDiameter / 2f;
        yBallCenter = yBall + ballDiameter / 2f;

        invalidate(); // force onDraw to redraw the screen with the ball in its new position
    }


    protected void onDraw(Canvas canvas) {
        joyStick.draw(canvas);

        Log.i(MYDEBUG, "onDraw - Paint Panel 2");
        if (drawFlag) {
            // draw the walls
            for (int i = 0; i < walls.size(); i++) {
                canvas.drawRect(getRectByTwoPoints(walls.get(i)), backgroundPaint);
            }

            // draw the ball in its new location
            canvas.drawBitmap(ball, xBall, yBall, null);
        }
    }

    /*
     * Configure the rolling ball panel according to setup parameters
     */
    public void configure(String directionOfControl, String gain, int numOfLaps, int current, double totalTime, MediaPlayer mediaPlayer, Activity parent)
    {
        this.directionOfControl = directionOfControl;
        this.gain = gain;
        this.numberOfLaps = numOfLaps;
        this.current = current;
        this.totalTime = totalTime;

        if(gain.equals("Very low")) {
            alpha = 3f;
        } else if(gain.equals("Low")) {
            alpha = 4f;
        } else if(gain.equals("Medium")) {
            alpha = 5f;
        } else if(gain.equals("High")) {
            alpha = 7f;
        } else if(gain.equals("Very high")) {
            alpha = 8f;
        }

        mp = mediaPlayer;
        activity = parent;
    }

    // start the activity ResultsActivity
    public void loadResultsView() {
        // calculate the total experiment time
        double totalTime = this.totalTime + (System.currentTimeMillis() - experimentStartTime) / 1000; // cumulative total time in s

        // bundle up parameters to pass on to activity
        Bundle b = new Bundle();
        b.putString("directionOfControl", directionOfControl);
        b.putString("gain", gain);
        b.putInt("numberOfLaps", numberOfLaps);
        b.putInt("current", current);
        b.putInt("numOfWallHits", wallHits);
        b.putDouble("totalTime", Math.round(totalTime * 100.0) / 100.0);
        b.putDouble("wallHitsRatio", Math.round(((wallHits * 100f) / stepCount) * 100.0) / 100.0);

        // start the Results Activity
        Intent i = new Intent(getContext(), ResultsActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtras(b);
        getContext().startActivity(i);
        activity.finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(joyStick.isPressed((double) event.getX() ,(double) event.getY()))
                {
                    Log.i(MYDEBUG, "On touch works in PAINT PANEL - Action DOWN");
                    joyStick.setIsPressed(true);
                    update(joyStick);
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                if(joyStick.getIsPressed())
                {
                    Log.i(MYDEBUG, "On touch works in PAINT PANEL - Action MOVE");
                    joyStick.setActuator(event.getX(), event.getY());
                    update(joyStick);
                }
                return true;
            case MotionEvent.ACTION_UP:
                Log.i(MYDEBUG, "On touch works in PAINT PANEL - Action UP");
                joyStick.setIsPressed(false);
                joyStick.resetActuator();
                update(joyStick);
                return true;
        }
        super.onTouchEvent(event);
        return true;
    }

    public boolean ballTouchExitLine()
    {
        // get the ball coordinates
        ballNow.left = xBall;
        ballNow.top = yBall;
        ballNow.right = xBall + ballDiameter;
        ballNow.bottom = yBall + ballDiameter;

        return RectF.intersects(ballNow, exitRect);
    }

    public boolean ballTouchLine()
    {
        // get the ball coordinates
        ballNow.left = xBall;
        ballNow.top = yBall;
        ballNow.right = xBall + ballDiameter;
        ballNow.bottom = yBall + ballDiameter;

        // cheat check: prevent the user from getting out of the starting point
        if(RectF.intersects(ballNow, startingRect)) {
            return true;
        }

        for (int i = 0; i < walls.size(); i++) {
            if (RectF.intersects(ballNow, getRectByTwoPoints(walls.get(i)))) {
                wallHits++;
                return true;
            }
        }

        return false;
    }

    private RectF getRectByTwoPoints(PaintPanel.Wall wall)
    {
        Point p1 = wall.p1;
        Point p2 = wall.p2;
        RectF lineRect = new RectF();

        // draw a vertical line
        if (p1.x == p2.x && p1.y != p2.y) {
            lineRect.left = p1.x - RECT_GAP;
            lineRect.right = p1.x + RECT_GAP;
            lineRect.top = p1.y;
            lineRect.bottom = p2.y;
        }
        // draw a horizontal line
        else if (p1.y == p2.y && p1.x != p2.x) {
            lineRect.left = p1.x;
            lineRect.right = p2.x;
            lineRect.top = p1.y - RECT_GAP;
            lineRect.bottom = p1.y + RECT_GAP;
        }
        else {
            Log.i(MYDEBUG, "Invalid points!");
        }

        return lineRect;
    }

    // move the ball using the joystick
    public void update(joystick joystick1)
    {
        Log.i(MYDEBUG, "Update called - Paint panel 2");
        stepCount++;

        velocityX = joystick1.getActuatorX() * alpha;
        velocityY = joystick1.getActuatorY() * alpha;
        yBall += velocityY;
        xBall += velocityX;

        if(ballTouchExitLine()) {
            yBall -= velocityY;
            xBall -= velocityX;
            vib.vibrate(50); // 50 ms vibrotactile pulse
            loadResultsView();
        }

        if(ballTouchLine()) {
            yBall -= velocityY;
            xBall -= velocityX;
        }
        else {
            invalidate();
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }


}
