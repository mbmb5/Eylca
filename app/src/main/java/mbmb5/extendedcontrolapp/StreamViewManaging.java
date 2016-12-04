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

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class StreamViewManaging extends Thread {
    private StreamView streamView;
    private boolean stop;

    public StreamViewManaging(StreamView view) {
        streamView = view;
        stop = false;
    }

    public void stopThread() {
        stop = true;
    }

    @Override
    public void run() {
        while (!stop) {
            Canvas canvas = null;
            try {
                UDPSocketManaging udpSocketManaging = new UDPSocketManaging();
                Bitmap bitmap = udpSocketManaging.execute().get();
                streamView.postInvalidate();
                canvas = streamView.getHolder().lockCanvas();
                synchronized (streamView.getHolder()) {
                    streamView.setCurrentImage(bitmap);
                    streamView.onDraw(canvas);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (canvas != null) {
                    streamView.getHolder().unlockCanvasAndPost(canvas);
                }
            }
            try {
                sleep(40);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
