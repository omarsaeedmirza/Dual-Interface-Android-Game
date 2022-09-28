package game.stuff.eecs.omar.project;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends Activity implements View.OnTouchListener
{
    final static String MYDEBUG = "MYDEBUG"; // for Log.i messages
    final static Float MOVE_OFFSET = 10f; // movement offset
    final int DELAY = 50; // ms

    PaintPanel pp;
    // parameters from the Setup dialog
    String directionOfControl, gain;
    int numberOfLaps, current, direction;
    double totalTime;

    boolean pressed;

    ImageButton ub, db, lb, rb;

    CountDownTimer timer;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Log.i(MYDEBUG, "Got here - Main Activity");
        // Media player will be used to play a tada song from raw folder
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.tada);

        // get parameters selected by user from setup dialog
        Bundle b = getIntent().getExtras();
        directionOfControl = b.getString("directionOfControl");
        gain = b.getString("gain");
        numberOfLaps = b.getInt("numberOfLaps");
        current = b.getInt("current");
        totalTime = b.getDouble("totalTime");

        // configure paint panel, as per setup parameters
        pp = (PaintPanel)findViewById(R.id.paintpanel);
        // configure the paint panel
        pp.configure(directionOfControl, gain, numberOfLaps, current, totalTime, mp, this);

        ub = (ImageButton) findViewById(R.id.arrowupbutton);
        db = (ImageButton) findViewById(R.id.arrowdownbutton);
        lb = (ImageButton) findViewById(R.id.arrowleftbutton);
        rb = (ImageButton) findViewById(R.id.arrowrightbutton);
        ub.setOnTouchListener(this);
        db.setOnTouchListener(this);
        lb.setOnTouchListener(this);
        rb.setOnTouchListener(this);

        direction = -1;
        pressed = false;

        timer = new CountDownTimer(DELAY, DELAY)
        {
            public void onTick(long millisUntilFinished)
            {
            }

            public void onFinish()
            {
                doMovement(direction);
            }
        };
    }

    @Override
    public boolean onTouch(View v, MotionEvent me)
    {
        if (v == ub) {
            switch(me.getAction() & MotionEvent.ACTION_MASK)
            {
                case MotionEvent.ACTION_DOWN:
                    Log.i(MYDEBUG, "Action DOWN");
                    pressed = true;
                    direction = 0; // moving upwards
                    timer.start();
                    break;

                case MotionEvent.ACTION_UP:
                    Log.i(MYDEBUG, "Action UP");
                    pressed = false;
            }
            return false;
        }
        else if (v == db) {
            switch(me.getAction() & MotionEvent.ACTION_MASK)
            {
                case MotionEvent.ACTION_DOWN:
                    Log.i(MYDEBUG, "Action DOWN");
                    pressed = true;
                    direction = 1; // moving downwards
                    timer.start();
                    break;

                case MotionEvent.ACTION_UP:
                    Log.i(MYDEBUG, "Action UP");
                    pressed = false;
            }
            return false;
        }
        else if (v == lb) {
            switch(me.getAction() & MotionEvent.ACTION_MASK)
            {
                case MotionEvent.ACTION_DOWN:
                    Log.i(MYDEBUG, "Action DOWN");
                    pressed = true;
                    direction = 2; // moving leftwards
                    timer.start();
                    break;

                case MotionEvent.ACTION_UP:
                    Log.i(MYDEBUG, "Action UP");
                    pressed = false;
            }
            return false;
        }
        else if (v == rb) {
            switch(me.getAction() & MotionEvent.ACTION_MASK)
            {
                case MotionEvent.ACTION_DOWN:
                    Log.i(MYDEBUG, "Action DOWN");
                    pressed = true;
                    direction = 3; // moving rightwards
                    timer.start();
                    break;

                case MotionEvent.ACTION_UP:
                    Log.i(MYDEBUG, "Action UP");
                    pressed = false;
            }
            return false;
        }

        return true;
    }

    private void doMovement(int direction)
    {
        if(pressed) {
            pp.move(direction, MOVE_OFFSET);
            timer.start();
        }
        else {
            timer.cancel();
        }
    }

}