/*
 * Copyright 2010-2011  Vasily Romanikhin  bac1ca89@gmail.com
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

package org.geo2tag.doctorsearch.preferences;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.geo2tag.doctorsearch.GDSUtil;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import org.geo2tag.doctorsearch.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class Settings {

	public static final String GDS_SETTINGS = "gds_settings";
	private static final String SETTINGS_STATUS = "settings_status"; 
	
	/* From settings activity: */
	private static final String DESCRIPTION = "description";
	private static final String SERVER_URL = "server_url";
	private static final String RADIUS = "radius";
	private static final String TRACKING_PERIOD = "tracking_period";
	private static final String EVENTS_PERIOD = "events_period";
	// How old tags can be
	private static final String RELEVANT_PERIOD = "relevant_period";
	
	/* Application internal settings */
	private static final String AUTH_TOKEN = "auth_token";
	private static final String LOGIN = "login";
	private static final String PASSWORD = "password";
	private static final String REMEMBER = "remember";
	private static final String WELCOME_ACTIVITY_DISABLED = "welcome_activity_disabled";
	
	
	private Context m_context;
	
	public Settings(Context context){
		m_context = context;
	}
	
	public SharedPreferences getPreferences(){
		return m_context.getSharedPreferences(GDS_SETTINGS, 0);
	}
	
	public Editor getPreferencesEditor(){
		return getPreferences().edit();		
	}	
	
	public boolean isSettingsEmpty(){
		return getPreferences().getBoolean(SETTINGS_STATUS, true);
	}
	
	public void setDefaultSettings(){
		Editor editor = getPreferencesEditor();
		loadFromXMLFile(editor);
		editor.putBoolean(SETTINGS_STATUS, false);
		editor.commit();
	}
	
	private void loadFromXMLFile(Editor configEditor) {
		try {
			InputSource is = new InputSource(
					m_context.getResources().openRawResource(R.raw.gds_client));
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			XMLReader xmlreader = parser.getXMLReader();
			xmlreader.setContentHandler(new ConfigFeedParser(this));
			xmlreader.parse(is);
		} catch (Exception e) {
			if (GDSUtil.DEBUG) {
				Log.v(m_context.getString(R.string.app_name),
					"loadFromXMLFile - exception: " + e.getMessage());
			}
		}
	}
	
	public String getAuthToken() {
		SharedPreferences prefs = m_context.getSharedPreferences(GDS_SETTINGS, 0);
		return prefs.getString(AUTH_TOKEN, "");
	}
	
	public void setAuthToken(String authToken) {
		SharedPreferences prefs = m_context.getSharedPreferences(GDS_SETTINGS, 0);
	    prefs.edit().putString(AUTH_TOKEN, authToken).commit();
	}
	
	public String getLogin() {
		SharedPreferences prefs = m_context.getSharedPreferences(GDS_SETTINGS, 0);
	    return prefs.getString(LOGIN, "");
	}
	
	public void setLogin(String login) {
		SharedPreferences prefs = m_context.getSharedPreferences(GDS_SETTINGS, 0);
	    prefs.edit().putString(LOGIN, login).commit();
	}
	
	public String getPassword() {
		SharedPreferences prefs = m_context.getSharedPreferences(GDS_SETTINGS, 0);
	    return prefs.getString(PASSWORD, "");
	}
	
	public void setPassword(String password) {
		SharedPreferences prefs = m_context.getSharedPreferences(GDS_SETTINGS, 0);
	    prefs.edit().putString(PASSWORD, password).commit();
	}
	
	public boolean isRememberMe() {
		SharedPreferences prefs = m_context.getSharedPreferences(GDS_SETTINGS, 0);
	    return prefs.getBoolean(REMEMBER, false);
	}
	
	public void setRememberMe(boolean status) {
		SharedPreferences prefs = m_context.getSharedPreferences(GDS_SETTINGS, 0);
	    prefs.edit().putBoolean(REMEMBER, status).commit();
	}
	
	public String getDescription() {
		SharedPreferences prefs = m_context.getSharedPreferences(GDS_SETTINGS, 0);
	    return prefs.getString(DESCRIPTION, "");
	}
	
	public void setDescription(String description) {
		SharedPreferences prefs = m_context.getSharedPreferences(GDS_SETTINGS, 0);
	    prefs.edit().putString(DESCRIPTION, description).commit();
	}
	
	public String getServerUrl() {
		SharedPreferences prefs = m_context.getSharedPreferences(GDS_SETTINGS, 0);
	    return prefs.getString(SERVER_URL, "");
	}
	
	public void setServerUrl(String serverUrl) {
		SharedPreferences prefs = m_context.getSharedPreferences(GDS_SETTINGS, 0);
	    prefs.edit().putString(SERVER_URL, serverUrl).commit();
	}
	
	public int getRadius() {
		SharedPreferences prefs = m_context.getSharedPreferences(GDS_SETTINGS, 0);
		String period = prefs.getString(RADIUS, String.valueOf(GDSUtil.EVENTS_RADIUS));
	    return Integer.parseInt(period);

	}
	
	public void setRadius(int radius) {
		SharedPreferences prefs = m_context.getSharedPreferences(GDS_SETTINGS, 0);
	    prefs.edit().putString(RADIUS, String.valueOf(radius)).commit();
	}
	
	public int getTrackingPeriod() {
		SharedPreferences prefs = m_context.getSharedPreferences(GDS_SETTINGS, 0);
		String period = prefs.getString(TRACKING_PERIOD, String.valueOf(GDSUtil.TRACKING_INTERVAL));
	    return Integer.parseInt(period);
	}
	
	public void setTrackingPeriod(int trackingPeriod) {
		SharedPreferences prefs = m_context.getSharedPreferences(GDS_SETTINGS, 0);
	    prefs.edit().putString(TRACKING_PERIOD, String.valueOf(trackingPeriod)).commit();
	}

	public int getEventsPeriod() {
		SharedPreferences prefs = m_context.getSharedPreferences(GDS_SETTINGS, 0);
		String period = prefs.getString(EVENTS_PERIOD, String.valueOf(GDSUtil.EVENTS_INTERVAL));
	    return Integer.parseInt(period);
	}
	
	public void setEventsPeriod(int eventsPeriod) {
		SharedPreferences prefs = m_context.getSharedPreferences(GDS_SETTINGS, 0);
	    prefs.edit().putString(EVENTS_PERIOD, String.valueOf(eventsPeriod)).commit();
	}
	
	public int getRelevantPeriod() {
		SharedPreferences prefs = m_context.getSharedPreferences(GDS_SETTINGS, 0);
		String period = prefs.getString(RELEVANT_PERIOD, String.valueOf(GDSUtil.RELEVANT_PERIOD_IN_HOURS));
	    return Integer.parseInt(period);
	}
	
	public void setRelevantPeriod(int eventsPeriod) {
		SharedPreferences prefs = m_context.getSharedPreferences(GDS_SETTINGS, 0);
	    prefs.edit().putString(RELEVANT_PERIOD, String.valueOf(eventsPeriod)).commit();
	}
	
	
	
	public static SharedPreferences getPreferences(Context c){
		return new Settings(c).getPreferences();
	}

	public boolean getWelcomeActivityDisabled() {
		SharedPreferences prefs = m_context.getSharedPreferences(GDS_SETTINGS, 0);
	    return prefs.getBoolean(WELCOME_ACTIVITY_DISABLED, GDSUtil.WELCOME_ACTIVITY_DISABLED);
	}
	
	public void setWelcomeActivityDisabled(boolean enabled){
		SharedPreferences prefs = m_context.getSharedPreferences(GDS_SETTINGS, 0);
	    prefs.edit().putBoolean(WELCOME_ACTIVITY_DISABLED, enabled).commit();
	}
			
}

/*

  NOTE: Development of this application was partly supported by Karelia ENPI CBC programme grant KA432 
	“Journey planner service for disabled people (Social Navigator)”, co-funded by the European Union, 
	the Russian Federation and the Republic of Finland.

*/	
