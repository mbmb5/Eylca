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

    private Button photoShot;
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

    }

    public static void loadUrl(String url) {
        myWebView.loadUrl(url);
    }

    public static void switchToRecMode() {
        loadUrl("http://"+ip+"/cam.cgi?mode=camcmd&value=recmode");
    }

    public static void startStream() {
        loadUrl("http://"+ip+"/cam.cgi?mode=startstream&value=49199");
    }

    public static void shotPicture() {
        loadUrl("http://"+ip+"/cam.cgi?mode=camcmd&value=capture");
    }
}
