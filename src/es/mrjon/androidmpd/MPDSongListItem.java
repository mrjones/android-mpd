package es.mrjon.androidmpd;

import org.bff.javampd.objects.MPDSong;

public class MPDSongListItem {
  private final MPDSong song;

  public MPDSongListItem(MPDSong song) {
    this.song = song;
  }

  public String toString() {
    return String.format("%s - %s", song.getArtist(), song.getTitle());
  }

  public MPDSong getSong() {
    return song;
  }
}
