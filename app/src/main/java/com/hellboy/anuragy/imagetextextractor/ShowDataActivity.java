package com.hellboy.anuragy.imagetextextractor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by anurag.y on 23/06/17.
 */

public class ShowDataActivity extends Activity {
    private String response = "";
    private int deviceHeight;
    private int deviceWidth;
    private Context context;
    private TextView textView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        if (getIntent() != null) {
            Bundle extras = getIntent().getExtras();
            response = extras.getString("response");
        }
        textView = (TextView) findViewById(R.id.result);
        Button copyButton = (Button) this.findViewById(R.id.CopyButton);
        Button extractButton = (Button) this.findViewById(R.id.AgainButton);
        LinearLayout mainButtonContainer = (LinearLayout) this.findViewById(R.id.ShowButtonContainer);
        getDeviceHeightWidth();
        context = this.getApplicationContext();
        textView.getLayoutParams().height = (int) (deviceHeight * 0.89);
        textView.setText(response);

        mainButtonContainer.getLayoutParams().height = (deviceHeight - ((int) (deviceHeight * 0.9)));
        copyButton.getLayoutParams().height = (deviceHeight - ((int) (deviceHeight * 0.92)));
        copyButton.getLayoutParams().width = (int) (deviceWidth * 0.48);
        extractButton.getLayoutParams().height = (deviceHeight - ((int) (deviceHeight * 0.92)));
        extractButton.getLayoutParams().width = (int) (deviceWidth * 0.48);

        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                    android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(textView.getText());
                } else {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", textView.getText());
                    clipboard.setPrimaryClip(clip);
                }
                Toast.makeText(context, "Copied Text", Toast.LENGTH_SHORT).show();
            }
        });

        extractButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(context, MainActivity.class);
                startActivity(mainIntent);
            }
        });
    }

    public void getDeviceHeightWidth() {
        Display mDisplay = this.getWindowManager().getDefaultDisplay();
        deviceWidth = mDisplay.getWidth();
        deviceHeight = mDisplay.getHeight();
    }
}
