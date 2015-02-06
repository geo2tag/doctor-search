package org.geo2tag.doctorsearch;

import android.os.Bundle;
import android.app.Activity;
import android.text.Html;
import android.view.Menu;
import android.widget.TextView;

public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		TextView welcomeText = (TextView) findViewById(R.id.about_text);
		String welcomeTextStr = getString(R.string.about_text) ;
		welcomeText.setText(Html.fromHtml(welcomeTextStr));
	}



}
