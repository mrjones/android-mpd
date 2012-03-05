package es.mrjon.androidmpd;

import android.os.AsyncTask;
import android.util.Log;
import org.bff.javampd.MPD;
import org.bff.javampd.exception.MPDException;
import org.bff.javampd.objects.MPDSong;

public class PlaySongTask extends AsyncTask<MPDSongListItem, Void, MPDSongListItem> {
  private final MPD mpd;
  private final StatusDisplay status;

  public PlaySongTask(MPD mpd, StatusDisplay status) {
    if (mpd == null) { throw new IllegalArgumentException("mpd cannot be null"); }
    if (status == null) { throw new IllegalArgumentException("status cannot be null"); }

    this.mpd = mpd;
    this.status = status;
  }

  public MPDSongListItem doInBackground(MPDSongListItem... songs) {
    if (songs == null || songs.length == 0 || songs[0] == null) {
      Log.e("PlaySongTask", "No songs passed in!");
    } else {
      try {
        mpd.getMPDPlayer().playId(songs[0].getSong());
        return songs[0];
      } catch (MPDException e) {
        Log.e("PlaySongTask", "mpd.playId", e);
      }
    }

    return null;
  }

  public void onPostExecute(MPDSongListItem playedSong) {
    if (playedSong != null) {
      status.display("Played: " + playedSong);
    } else {
      status.display("Error playing song.");
    }
  }
}
