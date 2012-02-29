package es.mrjon.androidmpd;

import android.util.Log;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.bff.javampd.MPD;
import org.bff.javampd.exception.MPDException;
import org.bff.javampd.objects.MPDArtist;

public class MetadataCache {
  private MPD mpd;

  private Collection<MPDArtist> artists;

  public MetadataCache(MPD mpd) throws MPDException {
    this.mpd = mpd;
  }

  public void update() throws MPDException{
    artists = mpd.getMPDDatabase().listAllArtists();
  }

  public List<String> getSearchTerms(List<String> recognizedPhrases) {
    List<String> searchTerms = new ArrayList<String>();
    try {
      if (artists == null) { update(); }
      Log.v("MetadataCache", "Considering " +artists.size() + " artists");
      for (String phrase : recognizedPhrases) {
        for (MPDArtist artist : artists) {
          if (artist.getName().toLowerCase().contains(phrase)) {
            searchTerms.add(artist.getName());
          }
        }
      }
    } catch (Exception e) {
      Log.e("MetadataCache ", e.toString());
    }

    if (searchTerms.size() == 0) {
      searchTerms.add(recognizedPhrases.get(0));
    }

    return searchTerms;
  }
}
