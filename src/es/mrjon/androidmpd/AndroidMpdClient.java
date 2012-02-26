package es.mrjon.androidmpd;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import org.bff.javampd.MPD;
import org.bff.javampd.exception.MPDConnectionException;

public class AndroidMpdClient extends Activity {
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    try {
      MPD mpd = new MPD("192.168.1.100", 6600);
      Log.v("AndroidMpdClient", "Version:" + mpd.getVersion());
      Log.v("AndroidMpdClient", "Uptime:" + mpd.getUptime());
      mpd.close();
//    } catch(MPDConnectionException e) {
    } catch(Exception e) {
      Log.e("Android Mpd Client", "Error Connecting: " + e.getMessage());
    }
  }
}
