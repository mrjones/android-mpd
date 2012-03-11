package es.mrjon.androidmpd;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SelectServerActivity extends Activity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.select_server);

    Button saveButton = (Button) findViewById(R.id.save_settings_button);
    final SelectServerActivity thisActivity = this;
    saveButton.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
          Intent intent = thisActivity.getIntent();
          thisActivity.finish();
        }
      });

    
  }
}
