package game.stuff.eecs.omar.project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.util.Log;

public class Setup extends Activity
{
	final static String[] DIRECTION_OF_CONTROL = { "Joystick", "DPad" };
	final static String[] GAIN = { "Very low", "Low", "Medium", "High", "Very high" };
	// String array for number of rounds
	final static String[] NUMBER_OF_ROUNDS = { "1", "2", "3" };

	final static String MYDEBUG = "MYDEBUG";

	// spinNumberOfLaps was declared to create a spinner object
	Spinner spinDirectionOfControl, spinGain, spinNumberOfRounds;

	// called when the activity is first created
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		Log.i(MYDEBUG, "Got here! (Setup - onCreate)");

		setContentView(R.layout.setup);

		spinDirectionOfControl = (Spinner) findViewById(R.id.paramDirectionOfControl);
		ArrayAdapter<CharSequence> adapter0 = new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle, DIRECTION_OF_CONTROL);
		spinDirectionOfControl.setAdapter(adapter0);
		spinDirectionOfControl.setSelection(0); // Joystick

		spinGain = (Spinner) findViewById(R.id.paramGain);
		ArrayAdapter<CharSequence> adapter1 = new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle, GAIN);
		spinGain.setAdapter(adapter1);
		spinGain.setSelection(2); // "medium" default

		// Have the spinner object reference its View Id in the setup.xml
		spinNumberOfRounds = (Spinner) findViewById(R.id.paramNumberOfRounds);
		// Create a new array adapter, storing the values of the string array
		ArrayAdapter<CharSequence> adapter2 = new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle, NUMBER_OF_ROUNDS);
		// Set the array adapter to the spinner object
		spinNumberOfRounds.setAdapter(adapter2);
		spinNumberOfRounds.setSelection(0); // 1
	}

	// called when the "OK" button is tapped
	public void clickOK(View view)
	{
		// get user's choices...
		String directionOfControl = (String) spinDirectionOfControl.getSelectedItem();
		// get the gain
		String gain = spinGain.getSelectedItem().toString();
		// Retrieve the number of rounds from the spinner object by parsing the
		// string object into an integer object.
		int numberOfLaps = Integer.parseInt(spinNumberOfRounds.getSelectedItem().toString());

		// bundle up parameters to pass on to activity
		Bundle b = new Bundle();
		b.putString("directionOfControl", directionOfControl);
		b.putString("gain", gain);
		// Store the integer value of the numberOfRounds in the bundle
		b.putInt("numberOfLaps", numberOfLaps);
		b.putInt("current", 1); // tells the main activity this is the first experiment trial
		b.putDouble("totalTime", 0);

		// start experiment activity
		if(directionOfControl.equals("Joystick"))
		{
			Log.i(MYDEBUG, "JoyStick input found");
			Intent i = new Intent(getApplicationContext(), MainActivity2.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			i.putExtras(b);
			startActivity(i);
		}

		else
		{
			Log.i(MYDEBUG, "Dpad input found");
			Intent i = new Intent(getApplicationContext(), MainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			i.putExtras(b);
			startActivity(i);

		}

		finish();
	}

	/** Called when the "Exit" button is pressed. */
	public void clickExit(View view)
	{
		super.onDestroy(); // cleanup
		this.finish(); // terminate
	}
}
