package game.stuff.eecs.omar.project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ResultsActivity extends Activity {
    final static String MYDEBUG = "MYDEBUG"; // for Log.i messages

    double totalTime, wallHitsRatio;
    String directionOfControl, gain;
    int numOfLaps, current, wallHits;

    // called when the activity is first created
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results);

        Bundle b = getIntent().getExtras();
        totalTime = b.getDouble("totalTime");
        directionOfControl = b.getString("directionOfControl");
        gain = b.getString("gain");
        numOfLaps = b.getInt("numberOfLaps");
        current = b.getInt("current");
        wallHits = b.getInt("numOfWallHits");
        wallHitsRatio = b.getDouble("wallHitsRatio");

        Button continueButton = (Button) findViewById(R.id.Button01);

        if(current >= numOfLaps) {
            continueButton.setVisibility(View.GONE);
        }

        TextView directionOfControlText = (TextView) findViewById(R.id.paramDirectionOfControl);
        directionOfControlText.setText(new String(directionOfControlText.getText() + ": " + String.valueOf(directionOfControl)));

        TextView gainText = (TextView) findViewById(R.id.paramGain);
        gainText.setText(new String(gainText.getText() + ": " + String.valueOf(gain)));

        TextView numOfRoundsText = (TextView) findViewById(R.id.paramNumberOfRounds);
        numOfRoundsText.setText(new String(numOfRoundsText.getText() + ": " + String.valueOf(numOfLaps)));

        TextView currentText = (TextView) findViewById(R.id.paramCurrent);
        currentText.setText(new String(currentText.getText() + ": " + String.valueOf(current)));

        TextView totalTimeText = (TextView) findViewById(R.id.paramTotalTime);
        totalTimeText.setText(new String(totalTimeText.getText() + ": " + String.valueOf(totalTime) + "s"));

        TextView WallHitsRatioText = (TextView) findViewById(R.id.paramWallHitsRatio);
        WallHitsRatioText.setText(new String(WallHitsRatioText.getText() + ": " + String.valueOf(wallHitsRatio) + "%"));
    }

    // called when the "continue" button is pressed
    public void continueNextTrial(View view)
    {
        if(current < numOfLaps) {
            Bundle b = new Bundle();
            b.putString("directionOfControl", directionOfControl);
            b.putString("gain", gain);
            b.putInt("numberOfLaps", numOfLaps);
            b.putInt("current", current + 1);
            b.putDouble("totalTime", totalTime);

            Intent i;
            if(directionOfControl.equals("DPad")) {
                i = new Intent(getApplicationContext(), MainActivity.class);
            }
            else {
                i = new Intent(getApplicationContext(), MainActivity2.class);
            }


            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.putExtras(b);
            startActivity(i);
            finish();
        }
    }

    // called when the "Setup" button is pressed
    public void setup(View view)
    {
        // start the Setup dialog
        Intent i = new Intent(getApplicationContext(), Setup.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    // called when the "Exit" button is pressed
    public void exit(View view)
    {
        super.onDestroy(); // activity is closing down
        this.finish(); // activity is destroyed and returns to the home screen
    }
}