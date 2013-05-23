/**
 * 
 */
package ru.spb.osll.GDS;

import ru.spb.osll.json.Errno;
import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

/**
 * @author Mark Zaslavskiy
 *
 */
public class BasicActivity extends Activity {
	
	public void handleError(int errno) {
		if (errno < 0) {
			if (GDSUtil.DEBUG) {
				Log.v(GDSUtil.LOG, "bad response received");
			}
			Toast.makeText(this, "Server error (corrupted response)",
					Toast.LENGTH_LONG).show();
		} else if ( Errno.getErrorByCode(errno) == null) {
			if (GDSUtil.DEBUG) {
				Log.v(GDSUtil.LOG, "unknown error");
			}
			Toast.makeText(this, "Unknown server error",
					Toast.LENGTH_LONG).show();
		} else if (errno > 0) {
			String error = Errno.getErrorByCode(errno);
			if (GDSUtil.DEBUG) {
				Log.v(GDSUtil.LOG, "error: " + error);
			}
			Toast.makeText(this, "Error: " + error,
					Toast.LENGTH_LONG).show();
		}
	}
	
	//protected int 

}
