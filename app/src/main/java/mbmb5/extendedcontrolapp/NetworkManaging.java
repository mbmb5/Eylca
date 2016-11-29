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

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

public class NetworkManaging {
    public static boolean isMobileDataOn(Context context) {
        if (context == null)
            return false;

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info == null)
            return false;
        if (info.getType() == manager.TYPE_MOBILE)
            return info.isConnectedOrConnecting();
        else
            return false;

    }

    public static boolean forceWifiUse(Context context) {
        if (context == null)
            return false;
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        //TODO rewrite this in order to support multiples API
        //TODO (use bindProcessToNetwork, setProcessDefaultNetwork, and requestRouteToHost)
        Network[] networks = manager.getAllNetworks();
        int i = 0;
        for (i = 0; i < networks.length; i++) {
            NetworkInfo info = manager.getNetworkInfo(networks[i]);
            if (info != null)
                if (info.isConnected())
                    if (info.getType() == manager.TYPE_WIFI)
                        break;
        }
        return manager.bindProcessToNetwork(networks[i]);
    }

    public static String getHost(Context context) {
        //TODO : retrieve ip of host to put it in the urls
        return "";
    }
}
