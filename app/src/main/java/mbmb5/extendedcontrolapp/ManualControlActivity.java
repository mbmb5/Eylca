/*
 *     Copyright 2016 mbmb5
 *
 *     This file is part of Eylca.
 *
 *     Eylca is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Eylca is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Eylca.  If not, see <http://www.gnu.org/licenses/>.
 */

package mbmb5.extendedcontrolapp;

import android.app.Activity;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

public class ManualControlActivity extends AppCompatActivity {

    private Button photoShot, videoShot;
    private static boolean isVideoStarted = false;
    public static WebView myWebView;
    private static final String ip = "192.168.54.1";
    public static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.activity_manual_control);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!NetworkManaging.forceWifiUse(this.getApplicationContext())) {
                Toast toast = Toast.makeText(this.getApplicationContext(),
                        "Please connect to the wifi network of your camera and restart the app.",
                        Toast.LENGTH_LONG);
                toast.show();
            }
        } else {
            if (NetworkManaging.isMobileDataOn(this.getApplicationContext())) {
                Toast toast = Toast.makeText(this.getApplicationContext(),
                        "Please disconnect mobile data, connect to the wifi of your camera and restart the app.",
                        Toast.LENGTH_LONG);
                toast.show();
            }
        }

        myWebView = (WebView) findViewById(R.id.webview);
        switchToRecMode();

        photoShot = (Button)findViewById(R.id.photoShot);
        photoShot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shotPicture();
            }
        });
        videoShot = (Button)findViewById(R.id.movieShot);
        videoShot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isVideoStarted) {
                    stopMovie();
                    videoShot.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    Toast toast = Toast.makeText(getApplicationContext(), "Video recorded successfully", Toast.LENGTH_SHORT);
                    toast.show();
                    isVideoStarted = false;
                } else {
                    startMovie();
                    videoShot.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    isVideoStarted = true;
                }
            }
        });

    }

    public static void loadCmd(String cmd) {
        myWebView.loadUrl("http://"+ip+"/cam.cgi"+cmd);
    }

    public static void switchToRecMode() {
        loadCmd("?mode=camcmd&value=recmode");
    }

    public static void startStream() {
        loadCmd("?mode=startstream&value=49199");
    }

    public static void shotPicture() {
        loadCmd("?mode=camcmd&value=capture");
    }

    public static void startMovie() {
        loadCmd("?mode=camcmd&value=video_recstart");
    }

    public static void stopMovie() {
        loadCmd("?mode=camcmd&value=video_recstop");
    }
}
