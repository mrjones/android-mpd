package es.mrjon.androidmpd;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import org.bff.javampd.MPD;
import org.bff.javampd.exception.MPDException;
import org.bff.javampd.objects.MPDSong;

public class SearchTask extends AsyncTask<String, String, List<MPDSong> > {
  private final MPD mpd;
  private final MetadataCache metadataCache;
  private final AsyncTask<Void, ?, ?> successCallback;

  private final TextView statusText;
  private final ListView playListView;

  public SearchTask(
    MPD mpd,
    MetadataCache metadataCache,
    TextView statusText,
    ListView playListView,
    /*@Nullable*/ AsyncTask<Void, ?, ?> successCallback) {

    this.mpd = mpd;
    this.metadataCache = metadataCache;
    this.statusText = statusText;
    this.playListView = playListView;
    this.successCallback = successCallback;
  }

  public void onPreExecute() {
    statusText.setText("Searching...");
  }

  public List<MPDSong> doInBackground(String... searchTerms) {
    List<String> userSearches = new ArrayList<String>();
    for (int i = 0; i < searchTerms.length; i++) {
      userSearches.add(searchTerms[i]);
    }

    List<String> mpdSearches = metadataCache.getSearchTerms(userSearches);
    List<MPDSong> results = new ArrayList<MPDSong>();
    try {
      for (String mpdSearch : mpdSearches) {
        if (isCancelled()) { return null; }
        publishProgress(mpdSearch);
        results.addAll(mpd.getMPDDatabase().searchArtist(mpdSearch));
      }

      mpd.getMPDPlaylist().clearPlaylist();
      mpd.getMPDPlaylist().addSongs(results);
      mpd.getMPDPlayer().play();
    } catch (MPDException e) {
      Log.e("es.mrjon.SearchTask", e.toString());
    }

    return results;
  }

  public void onProgressUpdate(String... updates) {
    if (updates != null && updates.length > 0) {
      statusText.setText("Searching '" + updates[0] + "'...");
    }
  }

  public void onPostExecute(List<MPDSong> r) {
    if (r == null) {
      statusText.setText("Error performing search.");
    } else {
      statusText.setText("Found " + r.size() + " songs.");
      if (successCallback != null) {
        successCallback.execute();
      }
    }
  }
}
