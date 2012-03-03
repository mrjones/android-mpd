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

public class UpdatePlaylistTask extends AsyncTask<Void, Void, List<String> > {
  private final Context context;
  private final MPD mpd;
  private final TextView statusText;
  private final ListView playListView;

  public UpdatePlaylistTask(
    Context context, MPD mpd, TextView statusText, ListView playListView) {
    this.context = context;
    this.mpd = mpd;
    this.statusText = statusText;
    this.playListView = playListView;
  }

  public List<String> doInBackground(Void... ignored) {
    List<String> playList = new ArrayList<String>();
//    try {
      for (MPDSong song : mpd.getMPDPlaylist().getSongList()) {
        String rowContents =
          String.format("%s - %s", song.getArtist(), song.getTitle());
        playList.add(rowContents);
        Log.v("AndroidMpdClient", "Appening playlist item: " + rowContents);
      }
//    } catch (MPDException e) {
//      Log.e("es.mrjon.UpdatePlaylisTask", "Error fetching playlist", e);
//      statusText.setText("Error updating playlist.");
//    }
    return playList;
  }

  public void onPostExecute(List<String> playList) {
    statusText.setText("Playlist updated");
    playListView.setAdapter(
      new ArrayAdapter<String>(context, R.layout.playlist_row, playList));
  }
}
