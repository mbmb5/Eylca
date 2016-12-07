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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import static mbmb5.extendedcontrolapp.ManualControlActivity.activity;

public class StreamView extends SurfaceView {
    private Bitmap currentImage;
    private SurfaceHolder holder;
    private StreamViewManaging manager;
    private int streamViewWidth;
    private int streamViewHeigth = 0;

    public StreamView(Context context) {
        super(context);
        manager = new StreamViewManaging(this);
        setDisplayWidth();
    }

    public StreamView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        manager = new StreamViewManaging(this);
        setDisplayWidth();
    }

    public void setDisplayWidth() {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        streamViewWidth = size.x;
    }

    @Override
    protected void onFinishInflate() {
        holder = getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                manager.stopThread();
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                manager.start();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            }
        });
    }

    public void setCurrentImage(Bitmap bitmap) {
        if (bitmap == null)
            return;
        currentImage = bitmap;
        // compute the height of the view once we know the bitmap's width and height
        if (streamViewHeigth == 0) {
            float factor = (float) streamViewWidth / (float) (bitmap.getWidth());
            streamViewHeigth = (int) (currentImage.getHeight() * factor);
        }
        currentImage = Bitmap.createScaledBitmap(currentImage, streamViewWidth, streamViewHeigth, false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (currentImage != null) {
            canvas.drawBitmap(currentImage, 0, 0, null);
        }
    }

}
