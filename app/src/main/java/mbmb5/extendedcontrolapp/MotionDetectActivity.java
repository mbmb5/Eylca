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

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

public class MotionDetectActivity extends ControlActivity {
    private MotionDetectCore core;
    static public TextView statusTextView;
    private Handler uiHandler = new Handler() {
            @Override
            public void handleMessage(Message msg){
                switch (msg.what) {
                    case (MOTION_DETECTED):
                        motionDetected();
                        break;
                    case (NO_MOTION):
                        noMotion();
                        break;
                    default:
                        super.handleMessage(msg);
                }
            }
        };
    public static final int MOTION_DETECTED = 0;
    public static final int NO_MOTION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.activity_motion_detect);
        setUp();

        statusTextView = (TextView) findViewById(R.id.status);

        core = new MotionDetectCore(actionHandler, uiHandler);
        core.start();
    }

    public void motionDetected() {
        statusTextView.setText("Motion detected");
        statusTextView.setBackgroundColor(activity.getResources().getColor(R.color.colorAccent));
    }

    public void noMotion() {
        statusTextView.setText("No motion");
        statusTextView.setBackgroundColor(activity.getResources().getColor(R.color.colorPrimaryDark));
    }
}
