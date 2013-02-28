package co.wtty.remember;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.JavascriptInterface;

public class PlayerActivity extends Activity {
	
	WebView _webview;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_player);
		Log.i("TRACE", "A");
		_webview = (WebView) findViewById(R.id.the_webview);
		Log.i("TRACE", "B");
        WebSettings webSettings = _webview.getSettings();
        Log.i("TRACE", "C");
        webSettings.setJavaScriptEnabled(true);
        Log.i("TRACE", "D");
        _webview.loadUrl("file:///android_asset/index.html");
        Log.i("TRACE", "E");
        _webview.addJavascriptInterface(new Gonzo(), "Native");
        Log.i("TRACE", "F");
	}
	
	class Gonzo {
		@JavascriptInterface
		public void playMorse(String word) {
			Log.i("GONZO", "BIRD BIRD BIRD, THE "+word+" IS THE WORD");
		}
	}
}
