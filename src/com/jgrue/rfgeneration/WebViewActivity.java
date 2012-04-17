package com.jgrue.rfgeneration;

import com.jgrue.rfgeneration.constants.Constants;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

public class WebViewActivity extends FragmentActivity {
	private static final String TAG = "WebViewActivity";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);
		
		Log.i(TAG, "Loading URL: " + getIntent().getStringExtra(Constants.INTENT_WEB_URL));
		
		WebView webView = (WebView)findViewById(R.id.webview);
		String webUrl = getIntent().getStringExtra(Constants.INTENT_WEB_URL);
		
		if(webUrl.endsWith(".jpg"))
			webView.loadData("<img src=\"" + getIntent().getStringExtra(Constants.INTENT_WEB_URL) + 
				"\" width=\"100&#37;\">", "text/html", "utf-8");
		else
			webView.loadUrl(webUrl);
		
		webView.getSettings().setBuiltInZoomControls(true);
		webView.getSettings().setUseWideViewPort(true);
		
		((TextView)findViewById(R.id.webview_header)).setText(getIntent().getStringExtra(Constants.INTENT_WEB_TITLE));
		findViewById(R.id.webview_progress).setVisibility(View.GONE);
	}
}
