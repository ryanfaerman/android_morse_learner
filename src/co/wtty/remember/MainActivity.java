package co.wtty.remember;

import java.util.Random;

import co.wtty.remember.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class MainActivity extends Activity implements SensorEventListener {
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = true;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

	/**
	 * If set, will toggle the system UI visibility upon interaction. Otherwise,
	 * will show the system UI visibility upon interaction.
	 */
	private static final boolean TOGGLE_ON_CLICK = true;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	private SystemUiHider mSystemUiHider;
	
	private SensorManager _sensorMgr;
	private Sensor _accelerometer;
	SoundManager snd;
	int _dit;
	int _dah;
	int _short_gap;
	int _medium_gap;
	int _long_gap;
	int dot = 200;      // Length of a Morse Code "dot" in milliseconds
	int dash = 500;     // Length of a Morse Code "dash" in milliseconds
	int short_gap = 200;    // Length of Gap Between dots/dashes
	int medium_gap = 500;   // Length of Gap Between Letters
	int long_gap = 1000;    // Length of Gap Between Words
	String[] alphabet = {
			"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", 
			"N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
		};
	String[] morse_alphabet = {
			".-", "-...", "-.-.", "-..", ".", "..-.", "--.", "....", "..", ".---", "-.-", ".-..", "--", 
			"-.", "---", ".--.", "--.-", ".-.", "...", "-", "..-", "...-", ".--", "-..-", "-.--", "--.."	
	};
	int[] _letter_sounds = new int[26];
	int _current_letter = 0;
	Random _rng = new Random();
	View contentView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

		final View controlsView = findViewById(R.id.fullscreen_content_controls);
		contentView = findViewById(R.id.fullscreen_content);

		// Set up an instance of SystemUiHider to control the system UI for
		// this activity.
		mSystemUiHider = SystemUiHider.getInstance(this, contentView,
				HIDER_FLAGS);
		mSystemUiHider.setup();
		mSystemUiHider
				.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
					// Cached values.
					int mControlsHeight;
					int mShortAnimTime;

					@Override
					@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
					public void onVisibilityChange(boolean visible) {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
							// If the ViewPropertyAnimator API is available
							// (Honeycomb MR2 and later), use it to animate the
							// in-layout UI controls at the bottom of the
							// screen.
							if (mControlsHeight == 0) {
								mControlsHeight = controlsView.getHeight();
							}
							if (mShortAnimTime == 0) {
								mShortAnimTime = getResources().getInteger(
										android.R.integer.config_shortAnimTime);
							}
							controlsView
									.animate()
									.translationY(visible ? 0 : mControlsHeight)
									.setDuration(mShortAnimTime);
						} else {
							// If the ViewPropertyAnimator APIs aren't
							// available, simply show or hide the in-layout UI
							// controls.
							controlsView.setVisibility(visible ? View.VISIBLE
									: View.GONE);
						}

						if (visible && AUTO_HIDE) {
							// Schedule a hide().
							delayedHide(AUTO_HIDE_DELAY_MILLIS);
						}
					}
				});

		// Set up the user interaction to manually show or hide the system UI.
		contentView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (TOGGLE_ON_CLICK) {
					mSystemUiHider.toggle();
				} else {
					mSystemUiHider.show();
				}
			}
		});

		// Upon interacting with UI controls, delay any scheduled hide()
		// operations to prevent the jarring behavior of controls going away
		// while interacting with the UI.
		findViewById(R.id.dummy_button).setOnTouchListener(
				mDelayHideTouchListener);
		
		_sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
		_accelerometer = _sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		_sensorMgr.registerListener(this, _accelerometer, SensorManager.SENSOR_DELAY_GAME);
		snd = new SoundManager(getApplicationContext());
		snd.setVolume(1.0f);
		snd.setBalance(1.0f);
		snd.setSpeed(1.0f);
		
		
		
		findViewById(R.id.dummy_button).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				
	
				Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				long[][] letter_vibes = {
						{ 0, dot, short_gap, dash, short_gap }, 
						{ 0, dash, short_gap, dot, short_gap, dot, short_gap, dot, short_gap }, 
						{ 0, dash, short_gap, dot, short_gap, dash, short_gap, dot, short_gap }, 
						{ 0, dash, short_gap, dot, short_gap, dot, short_gap }, { 0, dot, short_gap }, 
						{ 0, dot, short_gap, dot, short_gap, dash, short_gap, dot, short_gap }, 
						{ 0, dash, short_gap, dash, short_gap, dot, short_gap }, 
						{ 0, dot, short_gap, dot, short_gap, dot, short_gap, dot, short_gap }, 
						{ 0, dot, short_gap, dot, short_gap }, 
						{ 0, dot, short_gap, dash, short_gap, dash, short_gap, dash, short_gap }, 
						{ 0, dash, short_gap, dot, short_gap, dash, short_gap }, 
						{ 0, dot, short_gap, dash, short_gap, dot, short_gap, dot, short_gap }, 
						{ 0, dash, short_gap, dash, short_gap }, { 0, dash, short_gap, dot, short_gap }, 
						{ 0, dash, short_gap, dash, short_gap, dash, short_gap }, 
						{ 0, dot, short_gap, dash, short_gap, dash, short_gap, dot, short_gap }, 
						{ 0, dash, short_gap, dash, short_gap, dot, short_gap, dash, short_gap }, 
						{ 0, dot, short_gap, dash, short_gap, dot, short_gap }, 
						{ 0, dot, short_gap, dot, short_gap, dot, short_gap }, { 0, dash, short_gap }, 
						{ 0, dot, short_gap, dot, short_gap, dash, short_gap }, 
						{ 0, dot, short_gap, dot, short_gap, dot, short_gap, dash, short_gap }, 
						{ 0, dot, short_gap, dash, short_gap, dash, short_gap }, 
						{ 0, dash, short_gap, dot, short_gap, dot, short_gap, dash, short_gap }, 
						{ 0, dash, short_gap, dot, short_gap, dash, short_gap, dash, short_gap }, 
						{ 0, dash, short_gap, dash, short_gap, dot, short_gap, dot, short_gap }
				};


				v.vibrate(letter_vibes[_current_letter], -1);
				//snd.play(_dit);

				Log.i("TRACE", "THINGUS CLICKEDS");
				
			}
		});
		
		_letter_sounds[0] = snd.load(R.raw.a);
		_letter_sounds[1] = snd.load(R.raw.b);
		_letter_sounds[2] = snd.load(R.raw.c);
		_letter_sounds[3] = snd.load(R.raw.d);
		_letter_sounds[4] = snd.load(R.raw.e);
		_letter_sounds[5] = snd.load(R.raw.f);
		_letter_sounds[6] = snd.load(R.raw.g);
		_letter_sounds[7] = snd.load(R.raw.h);
		_letter_sounds[8] = snd.load(R.raw.i);
		_letter_sounds[9] = snd.load(R.raw.j);
		_letter_sounds[10] = snd.load(R.raw.k);
		_letter_sounds[11] = snd.load(R.raw.l);
		_letter_sounds[12] = snd.load(R.raw.m);
		_letter_sounds[13] = snd.load(R.raw.n);
		_letter_sounds[14] = snd.load(R.raw.o);
		_letter_sounds[15] = snd.load(R.raw.p);
		_letter_sounds[16] = snd.load(R.raw.q);
		_letter_sounds[17] = snd.load(R.raw.r);
		_letter_sounds[18] = snd.load(R.raw.s);
		_letter_sounds[19] = snd.load(R.raw.t);
		_letter_sounds[20] = snd.load(R.raw.u);
		_letter_sounds[21] = snd.load(R.raw.v);
		_letter_sounds[22] = snd.load(R.raw.w);
		_letter_sounds[23] = snd.load(R.raw.x);
		_letter_sounds[24] = snd.load(R.raw.y);
		_letter_sounds[25] = snd.load(R.raw.z);
		
		findViewById(R.id.dummy_button_two).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				
	
				Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				


				snd.play(_letter_sounds[_current_letter]);
				//snd.play(_dit);

				Log.i("TRACE", "THINGUS CLICKEDS");
				
			}
		});
		
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		delayedHide(100);
	}
	


	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (AUTO_HIDE) {
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			return false;
		}
	};

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mSystemUiHider.hide();
		}
	};

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
	
	private long lastUpdate = 0;
	private float last_x = 0f;
	private float last_y = 0f;
	private float last_z = 0f;
	private static final int SHAKE_THRESHOLD = 800;
	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor == _accelerometer) {
			
			long curTime = System.currentTimeMillis();
		    // only allow one update every 100ms.
		    if ((curTime - lastUpdate) > 100) {
		      long diffTime = (curTime - lastUpdate);
		      lastUpdate = curTime;

		      float x = event.values[0];
		      float y = event.values[1];
		      float z = event.values[2];

		      float speed = Math.abs(x+y+z-last_x-last_y-last_z) / diffTime * 10000;

		      if (speed > SHAKE_THRESHOLD) {
		        Log.d("sensor", "shake detected w/ speed: " + speed);
//		        Toast.makeText(this, "shake detected w/ speed: " + speed, Toast.LENGTH_SHORT).show();
		        _current_letter = _rng.nextInt(26);
		        String display = alphabet[_current_letter] + "\n" + morse_alphabet[_current_letter];
		        ((TextView) contentView).setText(display);
		      }
		      last_x = x;
		      last_y = y;
		      last_z = z;
		    }
		}
	}
}
