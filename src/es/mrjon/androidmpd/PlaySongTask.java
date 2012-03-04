package es.mrjon.androidmpd;

import android.os.AsyncTask;
import android.util.Log;
import org.bff.javampd.MPD;
import org.bff.javampd.exception.MPDException;
import org.bff.javampd.objects.MPDSong;

public class PlaySongTask extends AsyncTask<MPDSong, Void, Void> {
  private final MPD mpd;
  private final StatusDisplay status;

  public PlaySongTask(MPD mpd, StatusDisplay status) {
    this.mpd = mpd;
    this.status = status;
  }

  public Void doInBackground(MPDSong... songs) {
    if (songs == null || songs.length == 0) {
      Log.e("PlaySongTask", "No songs passed in!");
    } else {
      try {
        mpd.getMPDPlayer().playId(songs[0]);
      } catch (MPDException e) {
        Log.e("PlaySongTask", "mpd.playId", e);
      }
    }

    return null;
  }
}
