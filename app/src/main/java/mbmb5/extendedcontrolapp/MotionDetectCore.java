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

import static mbmb5.extendedcontrolapp.MotionDetectActivity.activity;
import static mbmb5.extendedcontrolapp.ControlActivity.startMovie;
import static mbmb5.extendedcontrolapp.ControlActivity.stopMovie;
import android.graphics.Bitmap;
import java.util.LinkedList;

public class MotionDetectCore extends Thread {
    private boolean stop;
    private boolean running;
    private LinkedList<Bitmap> oldImages;

    public MotionDetectCore() {
        stop = false;
        running = false;
        oldImages = new LinkedList<Bitmap>();
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
        int threshold = 50;
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
        int width = reference.getWidth();
        int height = reference.getHeight();
        int resol = width*height;
        int[] refPixels = new int[resol];
        int[] newPixels = new int[resol];
        reference.getPixels(refPixels, 0, width, 0, 0, width, height);
        newBitmap.getPixels(newPixels, 0, width, 0, 0, width, height);

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

    @Override
    public void run() {
        running = true;
        boolean videoRecording = false;
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
                        if (!videoRecording) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    System.err.println("start movie");
                                    startMovie();
                                }
                            });
                            videoRecording = true;
                        }
                    } else {
                        System.err.println("No motion anymore");
                        if (videoRecording) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    System.err.println("stop movie");
                                    stopMovie();
                                }
                            });
                            oldImages.clear();
                        }
                        videoRecording = false;
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
