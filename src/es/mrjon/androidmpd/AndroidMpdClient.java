package es.mrjon.androidmpd;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import org.bff.javampd.MPD;
import org.bff.javampd.exception.MPDConnectionException;
import org.bff.javampd.objects.MPDSong;

import java.util.ArrayList;
import java.util.List;

public class AndroidMpdClient extends Activity {
  /** Called when the activity is first created. */
  private MPD mpd;
  
  private static final int RECOGNIZER_REQUEST_CODE = 12345;

  private ListView playListView;

  private void updatePlayList() {
    List<String> playList = new ArrayList<String>();

    for (MPDSong song : mpd.getMPDPlaylist().getSongList()) {
      String rowContents = String.format("%s - %s", song.getArtist(), song.getTitle());
      playList.add(rowContents);
      Log.v("AndroidMpdClient", "Appening playlist item: " + rowContents);
    }

    playListView.setAdapter(
      new ArrayAdapter<String>(this, R.layout.playlist_row, playList));
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    try {
      mpd = new MPD("192.168.1.100", 6600);
      Log.v("AndroidMpdClient", "Version:" + mpd.getVersion());
      Log.v("AndroidMpdClient", "Uptime:" + mpd.getUptime());

      playListView = (ListView) findViewById(R.id.play_list_view);

      updatePlayList();
    } catch(Exception e) {
      Log.e("AndroidMpdClient - EXCEPTION", "Error Connecting: " + e.getMessage());
    }

    initializeListeners();
  }

  private void initializeListeners() {
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
    // Button stopButton = (Button) findViewById(R.id.stop_button);
    // stopButton.setOnClickListener(new OnClickListener() {
    //     public void onClick(View v) {
    //       try {
    //         mpd.getMPDPlayer().stop();
    //       } catch(Exception e) {
    //         Log.e("AndroidMpdClient - EXCEPTION", "Error Connecting: " + e.getMessage());
    //       }
    //     }
    //   });
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
    Button backButton = (Button) findViewById(R.id.back_button);
    backButton.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
          try {
            mpd.getMPDPlayer().playPrev();
          } catch(Exception e) {
            Log.e("AndroidMpdClient - EXCEPTION", "Error Connecting: " + e.getMessage());
          }
        }
      });
    Button forwardButton = (Button) findViewById(R.id.forward_button);
    forwardButton.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
          try {
            mpd.getMPDPlayer().playNext();
          } catch(Exception e) {
            Log.e("AndroidMpdClient - EXCEPTION", "Error Connecting: " + e.getMessage());
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
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == RECOGNIZER_REQUEST_CODE && resultCode == RESULT_OK) {
      List<String> matches = data.getStringArrayListExtra(
        RecognizerIntent.EXTRA_RESULTS);
      // List<Float> scores = data.getFloatArrayListExtra(
      //  RecognizerIntent.EXTRA_CONFIDENCE_SCORES);

//      int i = 0;
      for (String match : matches) {
        Log.v("AndroidMpdClient - Matches ***** ", match);
//        Log.v("AndroidMpdClient - SCORE: ", scores.get(i++));
      }

      try {
        TextView statusText = (TextView) findViewById(R.id.status_text);
        statusText.setText("Searching: " + matches.get(0));


        List<MPDSong> songs = new ArrayList<MPDSong>(
          mpd.getMPDDatabase().searchArtist(matches.get(0)));

        if (songs.size() == 0) {
          statusText.setText("No matches found for: " + matches.get(0));
        } else {
          Log.v("AndroidMpdClient", "PLAYING SONG: " + songs.get(0));
//          mpd.getMPDPlayer().playId(songs.get(0));
          mpd.getMPDPlaylist().clearPlaylist();
          mpd.getMPDPlaylist().addSongs(songs);
          mpd.getMPDPlayer().play();
        }
      } catch (Exception e) {
        Log.e("AndroidMpdClient - EXCEPTION", "Error Connecting: " + e.getMessage());
      }
      // wordsList.setAdapter(
      //   new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
      //                      matches));
    }

    updatePlayList();

    super.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  public void onDestroy() {
    try {
      mpd.close();
    } catch(Exception e) {
      Log.e("AndroidMpdClient - EXCEPTION", "Error Connecting: " + e.getMessage());
    }
    super.onDestroy();
  }
}
