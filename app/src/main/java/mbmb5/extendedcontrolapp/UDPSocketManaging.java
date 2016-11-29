/*
 *     Copyright 2016 mbmb5
 *
 *     This file is part of Extended Control For Lumix Cameras.
 *
 *     Extended Control For Lumix Cameras is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Extended Control For Lumix Cameras is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Extended Control For Lumix Cameras.  If not, see <http://www.gnu.org/licenses/>.
 */

package mbmb5.extendedcontrolapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPSocketManaging extends AsyncTask<Void, Void, Bitmap> {
    int serverPort = 49199;
    DatagramSocket socket;
    Bitmap currentImage;

    public Bitmap doInBackground(Void... param) {
        DatagramPacket udpPacket;
        byte[] outBuffer = new byte[30000];
        int offset;

        try {
            socket = new DatagramSocket(serverPort);
            socket.setSoTimeout(100);
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
            e.printStackTrace();
            socket.close();
            return null;
        }
    }

}
