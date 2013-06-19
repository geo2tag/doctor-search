package org.geo2tag.doctorsearch;

import java.util.List;

import org.geo2tag.doctorsearch.events.EventsManager;
import org.geo2tag.doctorsearch.events.EventsReceiver;
import org.geo2tag.doctorsearch.maps.EventsItemizedOverlay;
import org.geo2tag.doctorsearch.maps.PositionOverlay;
import org.geo2tag.doctorsearch.R;

//import ru.spb.osll.GDS.maps.PositionOverlay;
import ru.spb.osll.objects.Mark;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class MapTabActivity extends MapActivity {

	EventsManager m_eventsManager;
	String m_authToken;
	MapView m_mapView;
	EventsItemizedOverlay m_eventsOverlay;
	PositionOverlay m_positionOverlay;
	
	int m_oldRelevantPeriod = 0; 
	
	private LocationManager m_locationManager;
	private LocationListener m_locationListener = new LocationListener() {
		public void onLocationChanged(Location location0) {
			if (GDSUtil.DEBUG) {
				Log.v(GDSUtil.LOG, "MapTabActivity: locationChanged");
			}
			updatePosition();
		}
		public void onStatusChanged(String provider, int status, Bundle extras) {}
		public void onProviderEnabled(String provider) {}
		public void onProviderDisabled(String provider) {}
	};
	
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.map_tab_view);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		    m_authToken = extras.getString(GDSUtil.AUTH_TOKEN);
		}
		if (m_authToken == null) {
			if (GDSUtil.DEBUG) {
				Log.v(GDSUtil.LOG, "problem with extracting data in MapTabActivity");
			}
			Toast.makeText(this, "Can't create events tab", Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		
		registerReceiver(m_eventsReceiver, new IntentFilter(EventsReceiver.ACTION_EVENTS));
		
		m_mapView = (MapView) findViewById(R.id.mapview);
		//m_mapView.setVisibility(View.GONE);
		m_mapView.setBuiltInZoomControls(true);
		
		List<Overlay> mapOverlays = m_mapView.getOverlays();
		Drawable eventDrawable = this.getResources().getDrawable(
				R.drawable.event64);
		m_eventsOverlay = new EventsItemizedOverlay(eventDrawable, this);
		mapOverlays.add(m_eventsOverlay);
		
		m_locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		m_locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, m_locationListener);
		Drawable positionDrawable = this.getResources().getDrawable(
				R.drawable.position32);
		m_positionOverlay = new PositionOverlay(positionDrawable, this);
		mapOverlays.add(m_positionOverlay);
		updatePosition();
	    
	    m_eventsManager = new EventsManager();
	    m_eventsManager.setData(m_authToken);
	    m_eventsManager.startEventsService(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		
/*		Settings settings = new Settings(this);
		if (settings.getRelevantPeriod() != m_oldRelevantPeriod){
			GDSUtil.log("Relevant period changed! OLD = " + 
					m_oldRelevantPeriod + ", NEW = " +settings.getRelevantPeriod());
			m_oldRelevantPeriod = settings.getRelevantPeriod();
			
			m_eventsOverlay.clear();
			//m_eventsOverlay.removeOldMarks(m_oldRelevantPeriod);
		}*/
		
		updatePosition();
		m_eventsManager.requestEvents(this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		m_eventsManager.stopEventsService(this);
		unregisterReceiver(m_eventsReceiver);
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	@Override
	protected boolean isLocationDisplayed() {
		return true;
	}
	
	public void updatePosition() {
		Location location = LocationService.getLocation(MapTabActivity.this);
		if (location == null) {
			if (GDSUtil.DEBUG) {
				Log.v(EventsManager.LOG,
						"can't get location to update position on map");
			}
		} else {
			m_positionOverlay.updatePosition(location);
			m_mapView.invalidate();
			
			int lat = (int) (location.getLatitude() * 1E6);
			int lng = (int) (location.getLongitude() * 1E6);
			GeoPoint point = new GeoPoint(lat, lng);
			m_mapView.getController().animateTo(point);
		}
	}
	
	private EventsReceiver m_eventsReceiver = new EventsReceiver() {
		@Override
		public void onEvents(final List<Mark> marks ) {
			GDSUtil.log("Get "+ marks.size() + " events");
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					for (Mark mark : marks) {
						if (GDSUtil.DEBUG) {
							Log.v(GDSUtil.LOG, mark.toString());
						}
					}
					
					m_eventsOverlay.setEvents( marks);
					m_mapView.invalidate();
				}
			});
		}
		@Override
		public void onErrorOccured(String error) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
				}
			});
		}
	};
}