package co.wtty.remember;

import java.util.Random;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class WidgetActivity extends AppWidgetProvider {
	SoundManager snd;
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
	
	private static final String ACTION_CLICK = "ACTION_CLICK";
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		snd = new SoundManager(context);
		snd.setVolume(1.0f);
		snd.setBalance(1.0f);
		snd.setSpeed(1.0f);
		
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
		
		// Get all ids
	    ComponentName thisWidget = new ComponentName(context, WidgetActivity.class);
	    int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
	    
	    for (int widgetId : allWidgetIds) {
	      // Create some random data
	      int number = (new Random().nextInt(100));

	      RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_daily);
	      Log.w("TRACE", alphabet[_current_letter]);
	      // Set the text
	      remoteViews.setTextViewText(R.id.morse_letter, alphabet[_current_letter]);
	      _current_letter = _rng.nextInt(26);
	      snd.play(_letter_sounds[_current_letter]);

	      // Register an onClickListener
	      Intent intent = new Intent(context, WidgetActivity.class);

	      intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
	      intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

	      PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
	          0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	      remoteViews.setOnClickPendingIntent(R.id.play_button, pendingIntent);
	      appWidgetManager.updateAppWidget(widgetId, remoteViews);
	    }
	  }
}
