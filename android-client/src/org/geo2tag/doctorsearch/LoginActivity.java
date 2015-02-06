/*
 * Copyright 2012  Ivan Bezyazychnyy  ivan.bezyazychnyy@gmail.com
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * 3. The name of the author may not be used to endorse or promote
 *    products derived from this software without specific prior written
 *    permission.
 *
 * The advertising clause requiring mention in adverts must never be included.
 */

package org.geo2tag.doctorsearch;


import org.geo2tag.doctorsearch.exception.ExceptionHandler;
import org.geo2tag.doctorsearch.preferences.Settings;
import org.geo2tag.doctorsearch.preferences.SettingsActivity;
import org.geo2tag.doctorsearch.R;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends BasicActivity {

    public static final int SETTINGS_ID = Menu.FIRST;
	public static final int ABOUT_ID = Menu.FIRST + 2;


    private EditText m_loginEdit;
    private EditText m_passwordEdit;
    private CheckBox m_rememberCheck;
    private Settings m_settings = new Settings(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_view);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));


        m_loginEdit = (EditText) findViewById(R.id.edit_login);
        m_passwordEdit = (EditText) findViewById(R.id.edit_password);
        m_rememberCheck = (CheckBox) findViewById(R.id.remember_me_checkbox);

        initViews();
        initButtons();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        GDSUtil.log("LoginActivity onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        GDSUtil.log("LoginActivity onResume");
        initViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.add(0, SETTINGS_ID, 0, R.string.menu_settings);
        menu.add(0, ABOUT_ID, 0, R.string.menu_about);

        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case SETTINGS_ID:
                showSettings();
                return true;
            case ABOUT_ID:
                showAbout();
                return true;    
        }

        return super.onOptionsItemSelected(item);
    }
	private void showAbout() {
		// TODO Auto-generated method stub
		startActivity(new Intent(this, AboutActivity.class));

	}

    private void initViews() {
        if (m_settings.isRememberMe()) {
            m_loginEdit.setText(m_settings.getLogin());
            m_passwordEdit.setText(m_settings.getPassword());
            m_rememberCheck.setChecked(true);
        } else {
            m_loginEdit.setText("");
            m_passwordEdit.setText("");
            m_rememberCheck.setChecked(false);
        }
    }

    private void initButtons() {
        final Button signInBtn = (Button) findViewById(R.id.sign_in_button);
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.this.signIn();
            }
        });

        final Button createAccountBtn = (Button) findViewById(R.id.create_account_button);
        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	createAccount();
            }
        });
    }

    private void signIn() {
        GDSUtil.log("signing in");

        String login = m_loginEdit.getText().toString();
        String password = m_passwordEdit.getText().toString();
        String channel = login;
        String serverUrl = m_settings.getServerUrl();

        if (login.length()==0 || password.length()==0){
        	GDSUtil.log("login or password are empty");
            Toast.makeText(this, "Credentials can not be empty!", Toast.LENGTH_LONG).show();
            return;
        }
        
        
        RequestAsyncTask asyncTask = new RequestAsyncTask(login, password, channel, 
    			serverUrl, this);
        asyncTask.addSuccessListener(m_onRequestAsyncTaskSuccess);
        
        asyncTask.execute(null);       

    }

    private OnRequestAsyncTaskSuccessListener m_onRequestAsyncTaskSuccess = new OnRequestAsyncTaskSuccessListener(){

		@Override
		public void onRequestAsyncTaskSuccessListener(RequestAsyncTask task) {
			// TODO Auto-generated method stub
			String login = task.getLogin();
			String password = task.getPassword();
			String authToken = task.getAuthToken();
			String channel = task.getChannel();
			
			m_settings.setLogin(login);
			m_settings.setPassword(password);
			m_settings.setAuthToken(authToken);
			if (m_rememberCheck.isChecked()) {
				m_settings.setRememberMe(true);
			} else {
				m_settings.setRememberMe(false);
			}

			Intent i = new Intent(task.getContext(), MainActivity.class);
			i.putExtra(GDSUtil.AUTH_TOKEN, authToken);
			i.putExtra(GDSUtil.LOGIN, login);
			i.putExtra(GDSUtil.CHANNEL, channel);
			startActivity(i);
		}
    	
    };

    

	private void createAccount() {
    	Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(GDSUtil.REGISTER_URL));
		startActivity(browserIntent);
    }

    private void showSettings() {
        GDSUtil.log("opening settings");
        startActivity(new Intent(this, SettingsActivity.class));
    }
    
    

}
