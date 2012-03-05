package es.mrjon.androidmpd;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import org.bff.javampd.MPD;
import org.bff.javampd.exception.MPDException;
import org.bff.javampd.objects.MPDSong;

public class UpdatePlaylistTask extends AsyncTask<Void, Void, List<MPDSongListItem> > {
  private final Context context;
  private final MPD mpd;
  private final StatusDisplay status;
  private final ListView playListView;

  public UpdatePlaylistTask(
    Context context, MPD mpd, StatusDisplay status, ListView playListView) {
    if (context == null) { throw new IllegalArgumentException("context cannot be null"); }
    if (mpd == null) { throw new IllegalArgumentException("mpd cannot be null"); }
    if (status == null) { throw new IllegalArgumentException("status cannot be null"); }
    if (playListView == null) { throw new IllegalArgumentException("playListView cannot be null"); }

    this.context = context;
    this.mpd = mpd;
    this.status = status;
    this.playListView = playListView;
  }

  public List<MPDSongListItem> doInBackground(Void... ignored) {
    if (!mpd.isConnected()) { return null; }

    List<MPDSongListItem> playList = new ArrayList<MPDSongListItem>();

    for (MPDSong song : mpd.getMPDPlaylist().getSongList()) {
      String rowContents =
        String.format("%s - %s", song.getArtist(), song.getTitle());
      playList.add(new MPDSongListItem(song));
      Log.v(Constants.LOG_TAG, "Appening playlist item: " + rowContents);
    }
    return playList;
  }

  public void onPostExecute(List<MPDSongListItem> playList) {
    if (playList == null) {
      // Error in "doInBackground"
      status.display("Unable to update playlist!");
    } else {
      status.display("Playlist updated");

      playListView.setAdapter(
        new ArrayAdapter<MPDSongListItem>(context, R.layout.playlist_row, playList));
    }
  }
}
