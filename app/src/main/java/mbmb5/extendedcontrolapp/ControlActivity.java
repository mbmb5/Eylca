/*
 *     Copyright 2017 mbmb5
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
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/* this activity allows to control the camera through a webview
 * the webview has to be set by activities which extend this one
 */
public class ControlActivity extends AppCompatActivity {
    public static WebView myWebView;
    public static Activity activity;

    public static ArrayList<LoopThread> threads = new ArrayList<LoopThread>();

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private String[] mActivitiesTitles;
    protected Handler actionHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                switch (msg.what) {
                    case (ACTION_FOCUS_IN):
                        focusIn();
                        break;
                    case (ACTION_FOCUS_OUT):
                        focusOut();
                        break;
                    case (ACTION_SHOT_PICTURE):
                        shotPicture();
                        break;
                    case (ACTION_START_MOVIE):
                        startMovie();
                        break;
                    case (ACTION_STOP_MOVIE):
                        stopMovie();
                        break;
                    case (ACTION_START_STREAM):
                        startStream();
                        break;
                    case (ACTION_SWITCH_TO_REC_MODE):
                        switchToRecMode();
                        break;
                    case (ACTION_ZOOM_IN):
                        zoomIn();
                        break;
                    case (ACTION_ZOOM_OUT):
                        zoomOut();
                        break;
                    case (ACTION_ZOOM_STOP):
                        zoomStop();
                        break;
                    default:
                        super.handleMessage(msg);
                }
            }
        };

    protected void setUp() {
        myWebView = (WebView) findViewById(R.id.webview);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        mActivitiesTitles = getResources().getStringArray(R.array.activities_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.open_drawer,  /* "open drawer" description */
                R.string.close_drawer  /* "close drawer" description */
                ) {};

        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mActivitiesTitles));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
    }

    public static void stopThreads() {
        while (!threads.isEmpty()) {
            LoopThread currentThread = threads.remove(0);
            assert(currentThread != null);
            currentThread.stopThread();
            try {
                currentThread.join();
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /** Swaps fragments in the main content view */
    private void selectItem(int position) {
        mDrawerLayout.closeDrawers();
        mDrawerLayout.setSelected(false);
        switch (position) {
            case 0:
                if (activity.getClass() == MotionDetectActivity.class) {
                    stopThreads();
                    Intent intent = new Intent(this, ManualControlActivity.class);
                    startActivity(intent);
                }
                break;
            case 1:
                if (activity.getClass() == ManualControlActivity.class) {
                    stopThreads();
                    Intent intent = new Intent(this, MotionDetectActivity.class);
                    startActivity(intent);
                }
                break;
        }
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
        if(mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.action_settings:
                stopThreads();
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            case R.id.action_help:
                stopThreads();
                Intent helpIntent = new Intent(this, HelpActivity.class);
                startActivity(helpIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        stopThreads();
        super.onBackPressed();
    }

    public static void loadCmd(String cmd) {
        myWebView.loadUrl("http://" +
                PreferenceManager
                        .getDefaultSharedPreferences(activity.getApplicationContext())
                        .getString("camera_ip", "192.168.54.1")+"/cam.cgi"
                + cmd);
    }

    public static final int ACTION_SWITCH_TO_REC_MODE = 0;
    public static final int ACTION_START_STREAM = 1;
    public static final int ACTION_SHOT_PICTURE = 2;
    public static final int ACTION_START_MOVIE = 3;
    public static final int ACTION_ZOOM_IN = 4;
    public static final int ACTION_ZOOM_OUT = 5;
    public static final int ACTION_ZOOM_STOP = 6;
    public static final int ACTION_FOCUS_IN = 7;
    public static final int ACTION_FOCUS_OUT = 8;
    public static final int ACTION_STOP_MOVIE = 9;

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
