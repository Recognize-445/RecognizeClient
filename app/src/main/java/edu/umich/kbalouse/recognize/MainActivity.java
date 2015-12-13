package edu.umich.kbalouse.recognize;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;


public class MainActivity extends Activity {

    static final int TAKE_PHOTO_REQUEST = 1;

    private View mView;

    @Override
    public boolean onKeyDown(int keycode, KeyEvent event) {
        if (keycode == KeyEvent.KEYCODE_DPAD_CENTER) {
            AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            am.playSoundEffect(Sounds.SUCCESS);
            Intent intent = new Intent(this, PhotoActivity.class);
            startActivityForResult(intent, TAKE_PHOTO_REQUEST);
            return true;
        }
        return super.onKeyDown(keycode, event);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mView = new CardBuilder(this, CardBuilder.Layout.MENU)
                .setText(R.string.help_text)
                .getView();
        setContentView(mView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAKE_PHOTO_REQUEST) {
            CardBuilder card = new CardBuilder(this, CardBuilder.Layout.MENU);
            if (data != null) {
                String result = data.getStringExtra("prediction");
                if (result.equals("")) {
                    result = "Error: Connection lost.";
                }
                card.setText(result);
            } else {
                card.setText("Error: Connection lost.");
            }
            card.setFootnote("Tap to recognize someone!");

            mView = card.getView();
            setContentView(mView);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}