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
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CreateAccountActivity extends BasicActivity {

	private static final int SETTINGS_ID = Menu.FIRST;
	
	private EditText m_loginEdit;
	private EditText m_emailEdit;
	private EditText m_passwordEdit;
	private EditText m_rePasswordEdit;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_account_view);
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
		
		setTitle("Create a new account");
		
		m_loginEdit = (EditText) findViewById(R.id.edit_reg_login);
		m_emailEdit = (EditText) findViewById(R.id.edit_reg_email);
		m_passwordEdit = (EditText) findViewById(R.id.edit_reg_password);
		m_rePasswordEdit = (EditText) findViewById(R.id.edit_reg_re_password);

		initButtons();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
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
	
	private void initButtons() {
		final Button registerBtn= (Button) findViewById(R.id.register_button);
		registerBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CreateAccountActivity.this.register();
			}
		});
	}
	
	private void showSettings() {
		startActivity(new Intent(this, SettingsActivity.class));
	}
	
	private void register() {
		String login = m_loginEdit.getText().toString();
		String email = m_emailEdit.getText().toString();
		String password = m_passwordEdit.getText().toString();
		String re_password = m_rePasswordEdit.getText().toString();
		String serverUrl = new Settings(this).getServerUrl();
		String channel = login;
		
		// Check fields
		if (login.length() == 0) {
			Toast.makeText(this, "Login can't be empty", Toast.LENGTH_LONG).show();
			return;
		}
		if (email.length() == 0) {
			Toast.makeText(this, "Email can't be empty", Toast.LENGTH_LONG).show();
			return;
		}
		if (password.length() == 0) {
			Toast.makeText(this, "Password can't be empty", Toast.LENGTH_LONG).show();
			return;
		}
		if (password.compareTo(re_password) != 0) {
			Toast.makeText(this, "Passwords should match", Toast.LENGTH_LONG).show();
			return;
		}
		
		 RequestAsyncTask asyncTask = new RequestAsyncTask(login, password, channel, 
	    			serverUrl, this);
	     asyncTask.addSuccessListener(m_onRequestAsyncTaskSuccess);
	     asyncTask.setEmail(email);
	        
	     asyncTask.execute(null);    
		
	}
	

    private OnRequestAsyncTaskSuccessListener m_onRequestAsyncTaskSuccess = new OnRequestAsyncTaskSuccessListener(){

		@Override
		public void onRequestAsyncTaskSuccessListener(RequestAsyncTask task) {
			Toast.makeText(task.getContext(), "Account has been created!",
					Toast.LENGTH_LONG).show();
			finish();
		}
    	
    };


}
