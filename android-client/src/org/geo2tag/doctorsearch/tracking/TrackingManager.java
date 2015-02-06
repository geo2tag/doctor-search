package org.geo2tag.doctorsearch.tracking;

import org.geo2tag.doctorsearch.GDSUtil;

import android.content.Context;
import android.content.Intent;

public class TrackingManager {
	
	public static final String LOG = "GDS_Tracking_service";
	private String m_authToken;
	private String m_channel;
	
	public TrackingManager() {
		m_authToken = "";
		m_channel = "";
	}
	
	public TrackingManager(String authToken, String channel) {
		m_authToken = authToken;
		m_channel = channel;
	}
	
	public void setData(String authToken, String channel) {
		m_authToken = authToken;
		m_channel = channel;
	}
	
	public void startTracking(Context c) {
		Intent i = new Intent(c, TrackingService.class);
		i.putExtra(GDSUtil.AUTH_TOKEN, m_authToken);
		i.putExtra(GDSUtil.CHANNEL, m_channel);		
		c.startService(i);
	}
	
	public void stopTracking(Context c) {
		c.stopService(new Intent(c, TrackingService.class));
	}
	
	public boolean isTracking(Context c) {
		return GDSUtil.isServiceRunning(c, TrackingService.class.getName());
	}

}


/*

  NOTE: Development of this application was partly supported by Karelia ENPI CBC programme grant KA432 
	“Journey planner service for disabled people (Social Navigator)”, co-funded by the European Union, 
	the Russian Federation and the Republic of Finland.

*/	
