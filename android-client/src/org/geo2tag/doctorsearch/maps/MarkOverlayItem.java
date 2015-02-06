package org.geo2tag.doctorsearch.maps;

import ru.spb.osll.objects.Mark;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class MarkOverlayItem extends OverlayItem {
	
	private Mark m_mark;

	public MarkOverlayItem(Mark mark, String title, String snippet) {
		super(new GeoPoint((int) Math.round(mark.getLatitude() * 1E6),
						   (int) Math.round(mark.getLongitude() * 1E6)),
			  title, snippet);
		
		m_mark = mark;

	}
	
	
	
	public Mark getMark() {
		return m_mark;
	}

}


/*

  NOTE: Development of this application was partly supported by Karelia ENPI CBC programme grant KA432 
	“Journey planner service for disabled people (Social Navigator)”, co-funded by the European Union, 
	the Russian Federation and the Republic of Finland.

*/	
