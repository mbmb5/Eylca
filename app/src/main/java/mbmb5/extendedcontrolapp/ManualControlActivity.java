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
import android.content.Intent;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

public class ManualControlActivity extends AppCompatActivity {

    private Button photoShot, videoShot, zoomIn, zoomOut, focusIn, focusOut;
    private static boolean isVideoStarted = false;
    public static WebView myWebView;
    public static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.activity_manual_control);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

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
        zoomIn = (Button)findViewById(R.id.zoomIn);
        zoomIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zoomIn();
                zoomIn.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        zoomStop();
                    }
                }, 200);
            }
        });
        zoomOut = (Button)findViewById(R.id.zoomOut);
        zoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zoomOut();
                zoomOut.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        zoomStop();
                    }
                }, 200);
            }
        });
        focusIn = (Button)findViewById(R.id.focusIn);
        focusIn.setOnClickListener(new View.OnClickListener() {
            boolean isFirstClick = true;
            @Override
            public void onClick(View view) {
                if (isFirstClick) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Warning : focus change is only possible when the camera's focus is in manual mode", Toast.LENGTH_LONG);
                    toast.show();
                    isFirstClick = false;
                }
                focusIn();

            }
        });
        focusOut = (Button)findViewById(R.id.focusOut);
        focusOut.setOnClickListener(new View.OnClickListener() {
            boolean isFirstClick = true;
            @Override
            public void onClick(View view) {
                if (isFirstClick) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Warning : focus change is only possible when the camera's focus is in manual mode", Toast.LENGTH_LONG);
                    toast.show();
                    isFirstClick = false;
                }
                focusOut();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            case R.id.action_help:
                Toast toast = Toast.makeText(getApplicationContext(), "Feature not available yet", Toast.LENGTH_SHORT);
                toast.show();
                //TODO call activity to show help
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static void loadCmd(String cmd) {
        myWebView.loadUrl("http://" +
                PreferenceManager
                        .getDefaultSharedPreferences(activity.getApplicationContext())
                        .getString("camera_ip", "192.168.54.1")+"/cam.cgi"
                + cmd);
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

    public static void zoomIn() {
        loadCmd("?mode=camcmd&value=tele-fast");
    }

    public static void zoomOut() {
        loadCmd("?mode=camcmd&value=wide-fast");
    }

    public static void zoomStop() {
        loadCmd("?mode=camcmd&value=zoomstop");
    }

    public static void focusIn() {
        loadCmd("?mode=camctrl&type=focus&value=tele-fast");
    }

    public static void focusOut() {
        loadCmd("?mode=camctrl&type=focus&value=wide-fast");
    }
}
