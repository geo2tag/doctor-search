package org.geo2tag.doctorsearch.maps;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.geo2tag.doctorsearch.GDSUtil;

import ru.spb.osll.objects.Mark;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class EventsItemizedOverlay extends ItemizedOverlay<OverlayItem> {
	
	private ArrayList<MarkOverlayItem> m_items = new ArrayList<MarkOverlayItem>();
	private Context m_context;
	
	public EventsItemizedOverlay(Drawable defaultMarker) {
		super(boundCenter(defaultMarker));
		populate();
	}
	
	public EventsItemizedOverlay(Drawable defaultMarker, Context context) {
		  super(boundCenter(defaultMarker));
		  m_context = context;
		  populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return m_items.get(i);
	}

	@Override
	public int size() {
		return m_items.size();
	}
	
	@Override
	protected boolean onTap(int index) {
		MarkOverlayItem item = m_items.get(index);
		AlertDialog.Builder dialog = new AlertDialog.Builder(m_context);
		dialog.setTitle(item.getMark().getTitle());
		dialog.setMessage(item.getMark().getUser() + " ("
				+ GDSUtil.getTimeFromUtcString(item.getMark().getTime()) + "):\n"
				+ item.getMark().getDescription());
		dialog.show();
		return true;
	}
	
	synchronized public void setEvents(List<Mark> marks) {
		m_items.clear();
		for (Mark mark : marks) {
			m_items.add(new MarkOverlayItem(mark, "Event", ""));
		}
		populate();
	}
	
	public void clear(){
		m_items.clear();
		populate();
		
	}
	
	public void removeOldMarks(int newRelevantPeriod) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.HOUR, - newRelevantPeriod);
		Date currentDate = calendar.getTime();
		
		ArrayList<Mark> marks = new  ArrayList<Mark>();
	
		for (MarkOverlayItem m : m_items){
			Mark mark = m.getMark();

			try {
				Date date = GDSUtil.getUtcDateFormat().parse(mark.getTime());
				GDSUtil.log(currentDate.toString()+" " + date.toString());
				if (date.before(currentDate)){
					GDSUtil.log("This mark need to be deleted");
					marks.add(mark);
				}
				
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		if (marks.size() != 0 ) setEvents(marks);
	}
	
	public void addOverlay(MarkOverlayItem overlay) {
	    m_items.add(overlay);
	    populate();
	}

}


/*

  NOTE: Development of this application was partly supported by Karelia ENPI CBC programme grant KA432 
	“Journey planner service for disabled people (Social Navigator)”, co-funded by the European Union, 
	the Russian Federation and the Republic of Finland.

*/	
