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

package ru.spb.osll.GDS;


import ru.spb.osll.GDS.exception.ExceptionHandler;
import ru.spb.osll.GDS.preferences.Settings;
import ru.spb.osll.GDS.preferences.SettingsActivity;
import ru.spb.osll.GDS.tracking.RequestSenderWrapper;

import ru.spb.osll.json.RequestException;
import android.content.Intent;
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

    private EditText m_loginEdit;
    private EditText m_passwordEdit;
    private CheckBox m_rememberCheck;

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
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case SETTINGS_ID:
                showSettings();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        Settings settings = new Settings(this);
        if (settings.isRememberMe()) {
            m_loginEdit.setText(settings.getLogin());
            m_passwordEdit.setText(settings.getPassword());
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
                LoginActivity.this.createAccount();
            }
        });
    }

    private void signIn() {
        GDSUtil.log("signing in");

        String login = m_loginEdit.getText().toString();
        String password = m_passwordEdit.getText().toString();
        String channel = login;
        Settings settings = new Settings(this);
        String serverUrl = settings.getServerUrl();
        //String serverUrl = new Settings(this).getPreferences().getString(
        //		IGDSSettings.SERVER_URL, "");
        String authToken = "";

        if (login.length()==0 || password.length()==0){
        	GDSUtil.log("login or password are empty");
            Toast.makeText(this, "Credentials can not be empty!", Toast.LENGTH_LONG).show();
            return;
        }
        
        
        try{
        	authToken = RequestSenderWrapper.login(login, password, serverUrl);
        	
        	RequestSenderWrapper.addChannel(authToken, GDSUtil.EVENTS_CHANNEL, serverUrl);
        	RequestSenderWrapper.subscribeChannel(authToken, GDSUtil.EVENTS_CHANNEL, serverUrl);
        	
        	RequestSenderWrapper.addChannel(authToken, channel, serverUrl);
        	RequestSenderWrapper.subscribeChannel(authToken, channel, serverUrl);

        	GDSUtil.log( "Success sign in!" );

            settings.setLogin(m_loginEdit.getText().toString());
            settings.setPassword(m_passwordEdit.getText().toString());
            settings.setAuthToken(authToken);
            if (m_rememberCheck.isChecked()) {
                settings.setRememberMe(true);
            } else {
                settings.setRememberMe(false);
            }

            Intent i = new Intent(this, MainActivity.class);
            i.putExtra(GDSUtil.AUTH_TOKEN, authToken);
            i.putExtra(GDSUtil.LOGIN, login);
            i.putExtra(GDSUtil.CHANNEL, channel);
            startActivity(i);
        	
            
        }catch (RequestException e){
        	Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            GDSUtil.log( e.getMessage() );
        }
      
        

    }



	private void createAccount() {
        GDSUtil.log("creating account");
        startActivity(new Intent(this, CreateAccountActivity.class));
    }

    private void showSettings() {
        GDSUtil.log("opening settings");
        startActivity(new Intent(this, SettingsActivity.class));
    }
}
