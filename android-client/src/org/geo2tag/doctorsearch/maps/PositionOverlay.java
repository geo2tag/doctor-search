/**
 * 
 */
package org.geo2tag.doctorsearch.maps;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

/**
 * @author Mark Zaslavskiy
 *
 */
public class PositionOverlay extends ItemizedOverlay<OverlayItem> {

	
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Context mContext;
	
	public PositionOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
		// TODO Auto-generated constructor stub
	}

	public PositionOverlay(Drawable defaultMarker, Context context) {
		  super(boundCenterBottom(defaultMarker));
		  mContext = context;
	}
	
	
	@Override
	protected OverlayItem createItem(int i) {
		// TODO Auto-generated method stub
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return mOverlays.size();
	}
	
	public void addOverlay(OverlayItem overlay) {
		mOverlays.clear();
	    mOverlays.add(overlay);
	    populate();
	}

	
	@Override
	protected boolean onTap(int index) {
	  OverlayItem item = mOverlays.get(index);
	  AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
	  dialog.setTitle(item.getTitle());
	  dialog.setMessage(item.getSnippet());
	  dialog.show();
	  return true;
	}

	public void updatePosition(Location location) {
		int lat = (int) (location.getLatitude() * 1E6);
		int lng = (int) (location.getLongitude() * 1E6);
		GeoPoint point = new GeoPoint(lat, lng);
		OverlayItem overlayitem = new OverlayItem(point, "User current position","" );
		
	
		addOverlay(overlayitem);
	}
	
	
	
}


/*

  NOTE: Development of this application was partly supported by Karelia ENPI CBC programme grant KA432 
	“Journey planner service for disabled people (Social Navigator)”, co-funded by the European Union, 
	the Russian Federation and the Republic of Finland.

*/	
