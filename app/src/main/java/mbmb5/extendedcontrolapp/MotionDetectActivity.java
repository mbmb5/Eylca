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
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MotionDetectActivity extends ControlActivity {
    private MotionDetectCore core;
    static public TextView statusTextView;
    private boolean detectionStarted = false;
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

        final RadioGroup behaviourWhenDetect = (RadioGroup) findViewById(R.id.radio_buttons_behaviour);
        final Button startStopMotionDetect = (Button) findViewById(R.id.start_stop_motion_detect);
        startStopMotionDetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!detectionStarted) {
                    core = new MotionDetectCore(actionHandler, uiHandler);
                    switch (behaviourWhenDetect.getCheckedRadioButtonId()) {
                        case R.id.record_mode:
                            core.setBehavior(MotionDetectCore.RECORD);
                            break;
                        case R.id.burst_mode:
                            core.setBehavior(MotionDetectCore.SHOOT);
                            break;
                    }
                    core.start();
                    detectionStarted = true;
                    startStopMotionDetect.setText(R.string.disable_motion_detect);
                    statusTextView.setText(R.string.motion_status_no_motion);
                } else {
                    core.stopThread();
                    try {
                        core.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    core = null;
                    detectionStarted = false;
                    startStopMotionDetect.setText(R.string.enable_motion_detect);
                    statusTextView.setText(R.string.motion_status_not_started);
                }
            }
        });

    }

    public void motionDetected() {
        statusTextView.setText(R.string.motion_status_motion);
        statusTextView.setBackgroundColor(activity.getResources().getColor(R.color.colorAccent));
    }

    public void noMotion() {
        statusTextView.setText(R.string.motion_status_no_motion);
        statusTextView.setBackgroundColor(activity.getResources().getColor(R.color.colorPrimaryDark));
    }

    @Override
    public void onBackPressed() {
        if (detectionStarted) {
            core.stopThread();
            try {
                core.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        super.onBackPressed();
    }
}
