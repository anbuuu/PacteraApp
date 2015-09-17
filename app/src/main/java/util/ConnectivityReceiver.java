package util;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by anbu.subramanian on 16/09/15.
 */
public class ConnectivityReceiver {

    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;
    private final ConnectivityManager mConnectivityNetworkManager;

    public ConnectivityReceiver(ConnectivityManager connectivityManager) {
        mConnectivityNetworkManager = connectivityManager;
    }

    public boolean isOnline() {
        NetworkInfo netInfo = mConnectivityNetworkManager.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public int getConnectivityStatus() {
        NetworkInfo activeNetwork = mConnectivityNetworkManager.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

}
