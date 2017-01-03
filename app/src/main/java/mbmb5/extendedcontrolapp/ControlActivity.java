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
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Locale;

/* this activity allows to control the camera through a webview
 * the webview has to be set by activities which extend this one
 */
public class ControlActivity extends AppCompatActivity {
    public static WebView myWebView;
    public static Activity activity;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private String[] mActivitiesTitles;

    protected void setUp() {
        myWebView = (WebView) findViewById(R.id.webview);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        mActivitiesTitles = getResources().getStringArray(R.array.activities_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mActivitiesTitles));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    public static class ActivityFragment extends Fragment {
        public static final String ARG_ACTIVITY_NUMBER = "activity_number";

        public ActivityFragment() {
            // Empty constructor required for fragment subclasses
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_activity, container, false);
            int i = getArguments().getInt(ARG_ACTIVITY_NUMBER);
            String planet = getResources().getStringArray(R.array.activities_array)[i];

            int imageId = getResources().getIdentifier(planet.toLowerCase(Locale.getDefault()),
                            "drawable", getActivity().getPackageName());
            ((ImageView) rootView.findViewById(R.id.image)).setImageResource(imageId);
            getActivity().setTitle(planet);
            return rootView;
        }
    }

    /** Swaps fragments in the main content view */
    private void selectItem(int position) {
        switch (position) {
            case 0:
                if (activity.getClass() == MotionDetectActivity.class) {
                    Intent intent = new Intent(this, ManualControlActivity.class);
                    startActivity(intent);
                } else {
                    mDrawerLayout.closeDrawers();
                }
                break;
            case 1:
                if (activity.getClass() == ManualControlActivity.class) {
                    Intent intent = new Intent(this, MotionDetectActivity.class);
                    startActivity(intent);
                } else {
                    mDrawerLayout.closeDrawers();
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
