package org.geo2tag.doctorsearch.events;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.geo2tag.doctorsearch.GDSUtil;
import org.geo2tag.doctorsearch.LocationService;
import org.geo2tag.doctorsearch.exception.ExceptionHandler;
import org.geo2tag.doctorsearch.preferences.Settings;
import org.geo2tag.doctorsearch.tracking.RequestSenderWrapper;

import org.geo2tag.doctorsearch.R;
import ru.spb.osll.json.Errno;
import ru.spb.osll.json.RequestException;
import ru.spb.osll.objects.Channel;
import ru.spb.osll.objects.Mark;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

public class EventsService extends Service {
	
	private String m_authToken = null;
	private Thread m_eventsThread;
	private InternalReceiver m_internalReceiver = new InternalReceiver();
	private Settings m_settings;
	private MediaPlayer m_sirenPlayer;
	private Set<Integer> m_events_ids = new HashSet<Integer>();
	private Channel m_savedEvents;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onCreate() {
		if (GDSUtil.DEBUG) {
			Log.v(EventsManager.LOG, "EventsService create");
		}
		super.onCreate();
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

		m_settings = new Settings(this);
		m_sirenPlayer = MediaPlayer.create(this, R.raw.siren);
		registerReceiver(m_internalReceiver, new IntentFilter(InternalReceiver.ACTION));
		LocationService.getLocation(this);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		if (GDSUtil.DEBUG) {
			Log.v(EventsManager.LOG, "EventsService start");
		}
		super.onStart(intent, startId);
		
		Bundle extras =intent.getExtras();
		if (extras != null) {
		    m_authToken = extras.getString(GDSUtil.AUTH_TOKEN);
		}
		if (m_authToken == null) {
			if (GDSUtil.DEBUG) {
				Log.v(EventsManager.LOG, "problem with extracting data");
			}
			broadcastError("Failed to start tracking");
			stopSelf();
			return;
		}
		
		startEventsThread();
	}

	@Override
	public void onDestroy() {
		if (GDSUtil.DEBUG) {
			Log.v(EventsManager.LOG, "EventsService destroy");
		}
		super.onDestroy();
		stopEventsTread();
		unregisterReceiver(m_internalReceiver);
		m_sirenPlayer.release();
	}
	
	protected void stopEventsTread(){
		if (m_eventsThread != null){
			m_eventsThread.interrupt();
		}
	}
	
	protected void startEventsThread(){
		if (m_eventsThread != null){
			m_eventsThread.interrupt();
		}
		m_eventsThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (!Thread.currentThread().isInterrupted()){
					Location location = LocationService.getLocation(EventsService.this);
					if (location == null) {
						if (GDSUtil.DEBUG) {
							Log.v(EventsManager.LOG, "can't get location");
						}
					} else {
						if (GDSUtil.DEBUG) {
							Log.v(EventsManager.LOG, "coords: " + location.getLatitude()
									+ ", " + location.getLongitude());
						}
						requestEvents(location);
					}
					
					SystemClock.sleep(m_settings.getEventsPeriod() * 1000);
				}
			}
		});
		m_eventsThread.start();
	}
	
	
	private void requestEvents(Location location) {
		String serverUrl = m_settings.getServerUrl();
		
		
		try{
			List<Channel> channels = RequestSenderWrapper.filterCircleRequest(m_authToken,
					location.getLatitude(), location.getLongitude(), m_settings.getRadius(), 
					m_settings.getRelevantPeriod(), serverUrl);
			GDSUtil.log("Recieved "+ channels.size() +" channels");
			
			processChannels(channels);
			
		}catch (RequestException e){
			GDSUtil.log("Error during filterCircle request processing");
			broadcastError("Failed to send location");
		}

	}
	
	
	private void processChannels(List<Channel> channels){
		if (channels.size() == 0){
			broadcastEvents(new Channel());
			return;
		}
		
		for (Channel channel : channels) {
			GDSUtil.log("Processing "+channel.getName());
			if (channel.getName().compareTo(GDSUtil.EVENTS_CHANNEL) == 0) {	
				if (GDSUtil.NOT_RECEIVE_OWN_EVENTS) {
					Iterator<Mark> iter = channel.getMarks().iterator();
					String login = m_settings.getLogin();
					while (iter.hasNext()) {
						Mark mark = iter.next();
						if (login.compareTo(mark.getUser()) == 0) {
							iter.remove();
						}
					}
				}

				// are there new identificators?
				boolean new_ids = false;
				
				// are some identificators expired?
				boolean expired_ids = false;
				
				// Set of new identificators
				Set<Integer> events_ids_new = new HashSet<Integer>();
				
				for (Mark mark : channel.getMarks()) {
					GDSUtil.log("Current mark id " + mark.hashCode());
					events_ids_new.add(mark.hashCode());
					if (!m_events_ids.contains(mark.hashCode()))
						new_ids = true;
				}
				if (!events_ids_new.containsAll(m_events_ids))
					expired_ids = true;
				
				if (new_ids) {
					Thread alertThread = new Thread() {
						@Override
						public void run() {
							MediaPlayer player = MediaPlayer.create(EventsService.this,
									R.raw.siren);
							player.start();
							while (player.isPlaying()) {
								try {
									Thread.sleep(player.getDuration() + 500);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
							player.release();
							player = null;
						}
					};
					alertThread.start();
				}
				
				
				if (new_ids || expired_ids) {
					m_events_ids = events_ids_new;
					m_savedEvents = channel;
					broadcastEvents(channel);
				}
			}
		}
	}
	
	private void handleError(int errno) {
		if (errno < 0) {
			if (GDSUtil.DEBUG) {
				Log.v(EventsManager.LOG, "bad response received");
			}
		} else if (Errno.getErrorByCode(errno) == null) {
			if (GDSUtil.DEBUG) {
				Log.v(EventsManager.LOG, "unknown error");
			}
		} else if (errno > 0) {
			String error = Errno.getErrorByCode(errno);
			if (GDSUtil.DEBUG) {
				Log.v(EventsManager.LOG, "error: " + error);
			}
		}
		broadcastError("Failed to send location");
	}
	
	
	
	public class InternalReceiver extends BroadcastReceiver {
		public static final String ACTION 	= "osll.gds.events.internal";
		public static final String TYPE_SIGNAL	= "osll.gds.signal";

		public static final int SIGNAL_UPDATE_SETTINGS	= 0;
		public static final int SIGNAL_SEND_MARKS = 1;
		
		@Override
		public void onReceive(Context context, Intent intent) {
			int type = intent.getIntExtra(TYPE_SIGNAL, -1);
			switch (type) {
			case SIGNAL_UPDATE_SETTINGS:
				//refreshSCache();
				//onSettingUpdated();
				break;
			case SIGNAL_SEND_MARKS:
				if (GDSUtil.DEBUG) {
					Log.v(EventsManager.LOG, "saved events reqeusted");
				}
				if (m_savedEvents != null) {
					broadcastEvents(m_savedEvents);
				}
				break;
			}
			
		}
	}
	
	private void broadcastError(String error) {
		Intent intent = new Intent(EventsReceiver.ACTION_EVENTS);
		intent.putExtra(EventsReceiver.TYPE_OPERATION, EventsReceiver.TYPE_ERROR);
		intent.putExtra(EventsReceiver.ERROR, error);
		sendBroadcast(intent);
	}
	
	private void broadcastEvents(Channel eventsChannel) {
		GDSUtil.log("Broadcasting channel = " + eventsChannel.toString() + " !");
		
		Intent intent = new Intent(EventsReceiver.ACTION_EVENTS);
		intent.putExtra(EventsReceiver.TYPE_OPERATION, EventsReceiver.TYPE_EVENTS);
		intent.putExtra(EventsReceiver.EVENTS, (Serializable)eventsChannel.getMarks());
		sendBroadcast(intent);
	}

}