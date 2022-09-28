package game.stuff.eecs.omar.project;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;

public class MainActivity2 extends Activity{
    // Activity for the joystick input mode

    final static String MYDEBUG = "MYDEBUG"; // for Log.i messages

    PaintPanel2 pp2;

    // parameters from the Setup dialog
    String directionOfControl, gain;
    int numberOfLaps, current;
    double totalTime;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main2);
        Log.i(MYDEBUG, "Got here - Main Activity 2");
        // Media player will be used to play a tada song from raw folder
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.tada);

        // get parameters selected by user from setup dialog
        Bundle b = getIntent().getExtras();
        directionOfControl = b.getString("directionOfControl");
        gain = b.getString("gain");
        numberOfLaps = b.getInt("numberOfLaps");
        current = b.getInt("current");
        totalTime = b.getDouble("totalTime");

        pp2 = (PaintPanel2)findViewById(R.id.paintpanel2);
        // configure the paint panel
        pp2.configure(directionOfControl, gain, numberOfLaps, current, totalTime, mp, this);
    }
}

