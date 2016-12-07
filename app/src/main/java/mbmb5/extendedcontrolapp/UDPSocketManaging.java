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
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static mbmb5.extendedcontrolapp.ManualControlActivity.activity;
import static mbmb5.extendedcontrolapp.ManualControlActivity.myWebView;

public class UDPSocketManaging extends AsyncTask<Void, Void, Bitmap> {
    int serverPort = 49199;
    DatagramSocket socket;
    Bitmap currentImage;
    private static long referenceTime = System.currentTimeMillis();

    public UDPSocketManaging() {
    }

    public Bitmap doInBackground(Void... param) {
        DatagramPacket udpPacket;
        byte[] outBuffer = new byte[30000];
        int offset;

        try {
            socket = new DatagramSocket(serverPort);
            // this is here to anticipate the timeout.
            // It seems that the stream has to be restarted periodically,
            // something like every 12 seconds. We take a little margin here.
            if (System.currentTimeMillis()- referenceTime > 11000) {
                throw new Exception("stream has to be restarted");
            }

            socket.setSoTimeout(300);
            udpPacket = new DatagramPacket(outBuffer, outBuffer.length, InetAddress.getByName("127.0.1.1"), serverPort);
            socket.receive(udpPacket);
            outBuffer = udpPacket.getData();

            offset = 0;
            // find beginning of image
            while (outBuffer[offset] != -1 || outBuffer[offset+1] != -40) {
                offset++;
            }

            currentImage = BitmapFactory.decodeByteArray(outBuffer, offset, 30000-offset);
            socket.close();
            return currentImage;

        } catch (Exception e) {
            //FIXME Probably not the best way to do the trick at all
            // If we get (or anticipate) a timeout, ask the camera to restart the stream
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    myWebView.loadUrl("http://192.168.54.1/cam.cgi?mode=startstream&value=49199");
                }
            });
            referenceTime = System.currentTimeMillis();
            e.printStackTrace();
            socket.close();
            return null;
        }
    }

}
