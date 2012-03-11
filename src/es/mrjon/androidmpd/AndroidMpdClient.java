package es.mrjon.androidmpd;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import org.bff.javampd.MPD;
import org.bff.javampd.exception.MPDException;
import org.bff.javampd.objects.MPDSong;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class AndroidMpdClient extends Activity {
  private MPD mpd;
  
  private static final int RECOGNIZER_REQUEST_CODE = 1;
  private static final int SELECT_SERVER_CODE = 2;

  private ListView playListView;
  private StatusDisplay status;
  private MetadataCache metadataCache;

  private String hostname = "192.168.1.100";
  private int port = 6600;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    TextView statusTextView = (TextView) findViewById(R.id.status_text);
    status = new TextViewStatusDisplay(statusTextView);

    playListView = (ListView) findViewById(R.id.play_list_view);

    reconnect(hostname, port);

    UpdatePlaylistTask task = new UpdatePlaylistTask(
      this, mpd, status, playListView);
    task.execute();

    initializeListeners();
  }

  private void reconnect(String hostname, int port) {
    try {
      if (mpd != null && mpd.isConnected()) {
        mpd.close();
      }
      mpd = new MPD(hostname, port);
      Log.v(Constants.LOG_TAG, "Version:" + mpd.getVersion());
      Log.v(Constants.LOG_TAG, "Uptime:" + mpd.getUptime());
      
      metadataCache = new MetadataCache(mpd);
    } catch(MPDException e) {
      Log.e(Constants.LOG_TAG, "onCreate", e);
      status.display("Error connecting: " + e.toString());
      return;
    } catch(UnknownHostException e) {
      Log.e(Constants.LOG_TAG, "onCreate", e);
      status.display("Could not connect to: " + hostname + ":" + port);
      return;
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == RECOGNIZER_REQUEST_CODE) {
      if (resultCode == RESULT_OK) {
        handleSpeechRecognitionResult(data);
      } else {
        status.display("Error recognizing speech!");
      }
    } else if (requestCode == SELECT_SERVER_CODE) {
      if (resultCode == RESULT_OK) {
        handleSelectServerResult(data);
      } else {
        status.display("Error selecting server!");
      }
    }

    super.onActivityResult(requestCode, resultCode, data);
  }

  private void handleSelectServerResult(Intent data) {
    hostname = data.getStringExtra(SelectServerActivity.SERVER);
    port = data.getIntExtra(SelectServerActivity.PORT, port);
    status.display("Server: " + hostname + ":" + port);

    reconnect(hostname, port);
  }

  private void handleSpeechRecognitionResult(Intent data) {
    List<String> matches = data.getStringArrayListExtra(
      RecognizerIntent.EXTRA_RESULTS);
    // Only on ICS+
    // List<Float> scores = data.getFloatArrayListExtra(
    //  RecognizerIntent.EXTRA_CONFIDENCE_SCORES);

    for (String match : matches) {
      Log.v(Constants.LOG_TAG, "Voice Recognition Match: " + match);
    }

    UpdatePlaylistTask updatePlaylist = new UpdatePlaylistTask(
      this, mpd, status, playListView);

    SearchTask task = new SearchTask(
      mpd, metadataCache, status, playListView, updatePlaylist);
    task.execute(matches.toArray(new String[]{}));
  }

  @Override
  public void onDestroy() {
    try {
      if (mpd != null) {
        mpd.close();
      }
    } catch(MPDException e) {
      Log.e(Constants.LOG_TAG, "onDestroy", e);
    }

    super.onDestroy();
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    final int VOLUME_STEP = 5;
    if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
      try {
        mpd.getMPDPlayer().setVolume(
          mpd.getMPDPlayer().getVolume() + VOLUME_STEP);
      } catch (MPDException e) {
        Log.e(Constants.LOG_TAG, "Volumne Up", e);
      }
      return true;
    } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
      try {
        mpd.getMPDPlayer().setVolume(
          mpd.getMPDPlayer().getVolume() - VOLUME_STEP);
      } catch (MPDException e) {
        Log.e(Constants.LOG_TAG, "Volume Down", e);
      }
      return true;
    } else {
      return super.onKeyDown(keyCode, event);
    }
  }

  @Override
  public boolean onKeyUp(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_VOLUME_UP ||
        keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
      // Don't beep
      return true;
    }
    return super.onKeyDown(keyCode, event);
  }

  private void initializeListeners() {
    Button playButton = (Button) findViewById(R.id.play_button);
    playButton.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
          try {
            mpd.getMPDPlayer().play();
          } catch(MPDException e) {
            Log.e(Constants.LOG_TAG, "Play", e);
          }
        }
      });
    Button pauseButton = (Button) findViewById(R.id.pause_button);
    pauseButton.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
          try {
            mpd.getMPDPlayer().pause();
          } catch(MPDException e) {
            Log.e(Constants.LOG_TAG, "Pause", e);
          }
        }
      });
    Button backButton = (Button) findViewById(R.id.back_button);
    backButton.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
          try {
            mpd.getMPDPlayer().playPrev();
          } catch(MPDException e) {
            Log.e(Constants.LOG_TAG, "Back ", e);
          }
        }
      });
    Button forwardButton = (Button) findViewById(R.id.forward_button);
    forwardButton.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
          try {
            mpd.getMPDPlayer().playNext();
          } catch(MPDException e) {
            Log.e(Constants.LOG_TAG, "Click", e);
          }
        }
      });

    Button searchButton = (Button) findViewById(R.id.search_button);
    searchButton.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
          Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
          intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                          RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
          intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Search by artist:");
          startActivityForResult(intent, RECOGNIZER_REQUEST_CODE);
        }
      });

    Button settingsButton = (Button) findViewById(R.id.settings_button);
    final Context c = this;
    settingsButton.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
          Intent intent = new Intent(c, SelectServerActivity.class);
          intent.putExtra(SelectServerActivity.SERVER, hostname);
          intent.putExtra(SelectServerActivity.PORT, port);
          startActivityForResult(intent, SELECT_SERVER_CODE);
        }
      });

    playListView.setOnItemClickListener(new OnItemClickListener() {
        public void onItemClick(
          AdapterView<?> adapter, View view, int position, long id) {
          MPDSongListItem song = (MPDSongListItem) adapter.getItemAtPosition(position);
          PlaySongTask task = new PlaySongTask(mpd, status);
          task.execute(song);
        }
      });
  }
}
