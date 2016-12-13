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
import android.widget.TextView;
import android.widget.Toast;

public class ManualControlActivity extends AppCompatActivity {

    private View mView;
    public static WebView myWebView;
    public static StreamView streamView;
    public static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;

        setContentView(R.layout.activity_manual_control);
        mView = findViewById(R.id.shot);

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
        myWebView.loadUrl("http://192.168.54.1/cam.cgi?mode=camcmd&value=recmode");

        streamView = (StreamView) findViewById(R.id.surfaceView);

        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Click");
                myWebView.loadUrl("http://192.168.54.1/cam.cgi?mode=camcmd&value=capture");
            }
        });

        streamView.setOnClickListener(new View.OnClickListener() {
            int i = 0;
            @Override
            public void onClick(View view) {
                if (i == 0) {
                    //this just cleans the webview so that we can see the
                    // difference between the last url response and the new one
                    myWebView.loadUrl("");
                    i++;
                } else {
                    i = 0;
                    myWebView.loadUrl("http://192.168.54.1/cam.cgi?mode=startstream&value=49199");
                }
            }
        });
    }
}
