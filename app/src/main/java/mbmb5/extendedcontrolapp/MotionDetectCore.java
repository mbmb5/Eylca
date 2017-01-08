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

import static mbmb5.extendedcontrolapp.ControlActivity.ACTION_SHOT_PICTURE;
import static mbmb5.extendedcontrolapp.ControlActivity.ACTION_START_MOVIE;
import static mbmb5.extendedcontrolapp.ControlActivity.ACTION_STOP_MOVIE;
import static mbmb5.extendedcontrolapp.MotionDetectActivity.MOTION_DETECTED;
import static mbmb5.extendedcontrolapp.MotionDetectActivity.NO_MOTION;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

import java.util.LinkedList;


public class MotionDetectCore extends Thread {
    private boolean stop;
    private boolean running;
    private LinkedList<Bitmap> oldImages;
    private Handler actionHandler, uiHandler;
    public static final int RECORD = 0;
    public static final int SHOOT = 1;
    private int behavior = RECORD;

    public MotionDetectCore(Handler actionHandler, Handler uiHandler) {
        stop = false;
        running = false;
        oldImages = new LinkedList<Bitmap>();
        this.actionHandler = actionHandler;
        this.uiHandler = uiHandler;
    }

    public void stopThread() {
        stop = true;
    }

    public boolean isRunning() {
        return running;
    }

    /* Compare two pixels to see if there is a significant difference between them */
    /* returns true if the pixels are similar */
    private boolean comparePixels(int pixel1, int pixel2) {
        int threshold = 100;
        int shift = 0;
        for (int i = 0; i < 3; i++) {
            int component1 = (pixel1 >> shift) & 0xff;
            int component2 = (pixel2 >> shift) & 0xff;
            shift += 8;
            if ((component1 - component2) > threshold || (component2 - component1) > threshold) {
                return true;
            }
        }
        return false;
    }

    /* Compare newBitmap to reference, and if there is a significant difference, returns true */
    private boolean detectMotion(Bitmap reference, Bitmap newBitmap) {
        /* Test if comparison can be made */
        if (reference.getWidth() != newBitmap.getWidth())
            return false;
        if (reference.getHeight() != newBitmap.getHeight())
            return false;
        int width = reference.getWidth() / 10;
        int height = reference.getHeight() / 10;
        int resol = width*height;
        int[] refPixels = new int[resol];
        int[] newPixels = new int[resol];
        Bitmap.createScaledBitmap(reference, width, height, false)
                .getPixels(refPixels, 0, width, 0, 0, width, height);
        Bitmap.createScaledBitmap(newBitmap, width, height, false)
                .getPixels(newPixels, 0, width, 0, 0, width, height);

        int differentPixels = 0;
        for (int i = 0; i < resol; i++) {
            if (comparePixels(newPixels[i], refPixels[i])) {
                differentPixels++;
            }
        }
        if (differentPixels > (resol / 200))
            return true;

        return false;
    }

    public void setBehavior(int behavior) {
        assert(!running);
        this.behavior = behavior;
    }

    @Override
    public void run() {
        running = true;
        boolean inAction = false;
        while (!stop) {
            try {
                UDPSocketManaging udpSocketManaging = new UDPSocketManaging();
                Bitmap bitmap = udpSocketManaging.execute().get();
                if (bitmap == null) {
                    throw new Exception("Found null Bitmap");
                }
                oldImages.add(bitmap);
                if (oldImages.size() > 10) {
                    Bitmap oldBitmap = oldImages.removeFirst();
                    if (detectMotion(oldBitmap, bitmap)) {
                        System.err.println("Motion detected");
                        Message msg;
                        switch (behavior) {
                            case (RECORD):
                                if (!inAction) {
                                    msg = uiHandler.obtainMessage();
                                    msg.what = MOTION_DETECTED;
                                    msg.sendToTarget();

                                    msg = actionHandler.obtainMessage();
                                    msg.what = ACTION_START_MOVIE;
                                    msg.sendToTarget();

                                    sleep(100);
                                    inAction = true;
                                }
                                break;
                            case (SHOOT):
                                msg = actionHandler.obtainMessage();
                                msg.what = ACTION_SHOT_PICTURE;
                                msg.sendToTarget();
                                sleep(100);
                                break;
                        }
                    }else{
                        switch (behavior) {
                            case (RECORD):
                                System.err.println("No motion anymore");
                                if (inAction) {
                                    Message msg = uiHandler.obtainMessage();
                                    msg.what = NO_MOTION;
                                    msg.sendToTarget();

                                    msg = actionHandler.obtainMessage();
                                    msg.what = ACTION_STOP_MOVIE;
                                    msg.sendToTarget();

                                    sleep(100);
                                    oldImages.clear();
                                }
                                inAction = false;
                                break;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                sleep(40);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        running = false;
    }
}
