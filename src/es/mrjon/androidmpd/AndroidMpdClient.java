package es.mrjon.androidmpd;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import org.bff.javampd.MPD;
import org.bff.javampd.exception.MPDConnectionException;

public class AndroidMpdClient extends Activity {
  /** Called when the activity is first created. */
  private MPD mpd;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    try {
      mpd = new MPD("192.168.1.100", 6600);
      Log.v("AndroidMpdClient", "Version:" + mpd.getVersion());
      Log.v("AndroidMpdClient", "Uptime:" + mpd.getUptime());
    } catch(Exception e) {
      Log.e("AndroidMpdClient - EXCEPTION", "Error Connecting: " + e.getMessage());
    }


    Button playButton = (Button) findViewById(R.id.play_button);
    playButton.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
          try {
            mpd.getMPDPlayer().play();
          } catch(Exception e) {
            Log.e("AndroidMpdClient - EXCEPTION", "Error Connecting: " + e.getMessage());
          }
        }
      });
    Button stopButton = (Button) findViewById(R.id.stop_button);
    stopButton.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
          try {
            mpd.getMPDPlayer().stop();
          } catch(Exception e) {
            Log.e("AndroidMpdClient - EXCEPTION", "Error Connecting: " + e.getMessage());
          }
        }
      });
    Button pauseButton = (Button) findViewById(R.id.pause_button);
    pauseButton.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
          try {
            mpd.getMPDPlayer().pause();
          } catch(Exception e) {
            Log.e("AndroidMpdClient - EXCEPTION", "Error Connecting: " + e.getMessage());
          }
        }
      });
//    } catch(MPDConnectionException e) {
  }

  @Override
  public void onDestroy() {
    try {
      mpd.close();
    } catch(Exception e) {
      Log.e("AndroidMpdClient - EXCEPTION", "Error Connecting: " + e.getMessage());
    }
  }
}
