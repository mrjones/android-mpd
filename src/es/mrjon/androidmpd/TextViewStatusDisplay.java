package es.mrjon.androidmpd;

import android.widget.TextView;

public class TextViewStatusDisplay implements StatusDisplay {
  private final TextView view;

  public TextViewStatusDisplay(TextView view) {
    this.view = view;
  }

  public void display(String status) {
    view.setText(status);
  }
}
