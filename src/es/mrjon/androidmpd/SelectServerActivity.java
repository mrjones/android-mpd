package es.mrjon.androidmpd;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SelectServerActivity extends Activity {
  public static final String SERVER = "SERVER";
  public static final String PORT = "PORT";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.select_server);

    Button saveButton = (Button) findViewById(R.id.save_settings_button);
    final EditText serverText = (EditText) findViewById(R.id.mpd_server_input);
    final EditText portText = (EditText) findViewById(R.id.mpd_port_input);

    serverText.setText(getIntent().getStringExtra(SERVER));
    portText.setText("" + getIntent().getIntExtra(PORT, 6600));

    final SelectServerActivity thisActivity = this;

    saveButton.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
          int port = Integer.parseInt(portText.getText().toString());

          Intent intent = thisActivity.getIntent();
          intent.putExtra(SERVER, serverText.getText().toString());
          intent.putExtra(PORT, port);
          thisActivity.setResult(RESULT_OK, intent);
          thisActivity.finish();
        }
      });

    
  }
}
