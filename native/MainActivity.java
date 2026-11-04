package io.github.bibomoglal.brightstarwifi;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import com.getcapacitor.BridgeActivity;

/* Keeps the app's traffic on the Wi-Fi network even while that Wi-Fi has no internet yet
   (before login), so Android does not send the requests to mobile data instead. */
public class MainActivity extends BridgeActivity {
    private ConnectivityManager cm;
    private ConnectivityManager.NetworkCallback cb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkRequest req = new NetworkRequest.Builder()
                    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI).build();
            cb = new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(Network network) {
                    try { cm.bindProcessToNetwork(network); } catch (Exception e) { }
                }
                @Override
                public void onLost(Network network) {
                    try { cm.bindProcessToNetwork(null); } catch (Exception e) { }
                }
            };
            cm.requestNetwork(req, cb);
        } catch (Exception e) { }
    }

    @Override
    public void onDestroy() {
        try {
            if (cm != null && cb != null) {
                cm.unregisterNetworkCallback(cb);
                cm.bindProcessToNetwork(null);
            }
        } catch (Exception e) { }
        super.onDestroy();
    }
}
