package org.geo2tag.doctorsearch;


import org.geo2tag.doctorsearch.preferences.Settings;

import org.geo2tag.doctorsearch.R;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class WelcomeActivity extends Activity {

	private Button m_buttonNext;
	private CheckBox m_checkBox;

	private OnClickListener m_nextClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			changeToLoginActivity(); 
		}
	};
	
	private OnClickListener m_checkOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			boolean checked = ((CheckBox) v).isChecked();
			setWelcomeActivityDisabled(checked);
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		
        Settings settings = new Settings(this);
        if (settings.isSettingsEmpty()) {
            settings.setDefaultSettings();
        }
		
		m_buttonNext = (Button) findViewById(R.id.welcome_activity_next);
		m_buttonNext.setOnClickListener(m_nextClickListener);
		
		m_checkBox = (CheckBox) findViewById(R.id.welcome_checkbox);
		m_checkBox.setOnClickListener(m_checkOnClickListener);
		
		TextView welcomeText = (TextView) findViewById(R.id.welcome_text);
		String welcomeTextStr = getString(R.string.welcome_activity_message) ;
		welcomeText.setText(Html.fromHtml(welcomeTextStr));
		
		initViews();
	}

	@Override
	protected void onResume() {
		super.onResume();
		initViews();
	}
	
	private void initViews(){
		if (isDisabled()) {
			m_checkBox.setChecked(true);
			changeToLoginActivity();
		} else {
			m_checkBox.setChecked(false);
		}
	}
	
	/**
	 * Checks if this activity locked by preferences
	 * 
	 * @return isDisabled
	 */
	private boolean isDisabled() {
		Settings settings = new Settings(this);
		return settings.getWelcomeActivityDisabled();
	}
	
	private void changeToLoginActivity(){
		finish();
		startActivity(new Intent(this, LoginActivity.class));
	}
	
	/**
	 * Sets this activity disabled/enabled
	 * 
	 * @param value
	 */
	private void setWelcomeActivityDisabled(boolean value) {
		Settings settings = new Settings(this);
		settings.setWelcomeActivityDisabled(value);
	}

}
