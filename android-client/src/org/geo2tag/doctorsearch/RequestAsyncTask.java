/**
 * 
 */
package org.geo2tag.doctorsearch;

import java.util.ArrayList;
import java.util.List;

import org.geo2tag.doctorsearch.tracking.RequestSenderWrapper;

import ru.spb.osll.json.RequestException;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

/**
 * This class does common request routines for LoginActivity and CreateAccountActivity
 * If email field is set (setEmail) CreateAccountActivity routines will be used, 
 * else LoginActivity will be used
 * @author Mark Zaslavskiy
 *
 */
public class RequestAsyncTask extends AsyncTask<Void, Void, Void> {

	private String m_authToken;
	private String m_login;
	private String m_channel;
	private String m_serverUrl;
	private String m_password;
	private String m_email = null;
	
	private Context m_context;
	
	/**
	 * @return the m_context
	 */
	public Context getContext() {
		return m_context;
	}

	private boolean m_result;
	private String m_error;
	
	private List<OnRequestAsyncTaskSuccessListener> m_listeners = new ArrayList<OnRequestAsyncTaskSuccessListener>(); 
	
	public void setEmail(String email){
		m_email = email;
	}
	
	public RequestAsyncTask(String login, String password, String channel, 
			String serverUrl, Context context)
	{
		m_login = login;
		m_password = password;
		m_channel = channel;
		m_serverUrl = serverUrl;
		m_context = context;
	}
	
	private boolean isCreatingAccount(){
		return m_email != null;
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub
		
		
        try{
        	if (isCreatingAccount()){
        		RequestSenderWrapper.addUser(m_login, m_password, m_email, m_serverUrl);
        	}
        	
        	m_authToken = RequestSenderWrapper.login(m_login, m_password, m_serverUrl);
        	
        	RequestSenderWrapper.addChannel(m_authToken, GDSUtil.EVENTS_CHANNEL, m_serverUrl);
        	RequestSenderWrapper.subscribeChannel(m_authToken, GDSUtil.EVENTS_CHANNEL, m_serverUrl);
        	
        	RequestSenderWrapper.addChannel(m_authToken, m_channel, m_serverUrl);
        	RequestSenderWrapper.subscribeChannel(m_authToken, m_channel, m_serverUrl);

        	GDSUtil.log( "Requests of RequestAsyncTask completed successfuly" );
    	
            m_result = true;
        }catch (RequestException e){
        	m_error = e.getMessage();
        	GDSUtil.log( m_error );
        	m_result = false;
        }
		
		return null;
	}
	
	@Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        if (m_result){
        	doSuccessActions();
        }else {
        	Toast.makeText(m_context, m_error, Toast.LENGTH_LONG).show();
            
        }
	}

	public void addSuccessListener(OnRequestAsyncTaskSuccessListener listener){
		m_listeners.add(listener);
	}
	
	
	
	private void doSuccessActions() {
		// TODO Auto-generated method stub
		for (OnRequestAsyncTaskSuccessListener l : m_listeners){
			l.onRequestAsyncTaskSuccessListener(this);	
		}
	}

	/**
	 * @return the m_authToken
	 */
	public String getAuthToken() {
		return m_authToken;
	}

	/**
	 * @return the m_login
	 */
	public String getLogin() {
		return m_login;
	}

	/**
	 * @return the m_channel
	 */
	public String getChannel() {
		return m_channel;
	}

	/**
	 * @return the m_password
	 */
	public String getPassword() {
		return m_password;
	}

	/**
	 * @return the m_email
	 */
	public String getEmail() {
		return m_email;
	}
	
}
