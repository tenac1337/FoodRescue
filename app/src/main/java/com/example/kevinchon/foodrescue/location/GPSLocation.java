package com.example.kevinchon.foodrescue.location;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;

/**
 * Created by Rohan Jadvani on 10/24/14.
 */
public class GPSLocation implements LocationListener {

    private static final long MIN_DISTANCE_UPDATE = 1000; // 1 km
    private static final long MIN_TIME_UPDATES = 1000 * 60 * 1; // 1 min
    private static boolean isGPSEnabled = false;
    private static boolean isNetworkEnabled = false;
    private LocationUpdateListener mListener = null;
    private static LocationManager mManager = null;

    public GPSLocation(Context context, LocationUpdateListener listener) {
        // set listener and manager from context
        mListener = listener;
        LocationManager manager = getLocationManager(context);
        boolean enabled = checkGPSEnabled(context);
        // gps can be accessed
        if (mListener != null && manager != null && enabled) {
            // get updates from whichever service is available
            if (isNetworkEnabled) {
                manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_UPDATES,
                        MIN_DISTANCE_UPDATE, this);
            } else if (isGPSEnabled) {
                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_UPDATES,
                        MIN_DISTANCE_UPDATE, this);
            }
        } else {
            if (!enabled && mListener != null) {
                mListener.isGPSAvailable(false);
            }
        }
    }

    private static LocationManager getLocationManager(Context context) {
        if (mManager == null) {
            mManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }
        return mManager;
    }

    /**
     * Check if the GPS is enabled.
     *
     * @param context mobile context
     * @return if the gps or network feature is enabled
     */
    public static boolean checkGPSEnabled(Context context) {
        LocationManager manager = getLocationManager(context);
        if (manager == null) {
            return false;
        }
        // check for enabling
        isGPSEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!isGPSEnabled && !isNetworkEnabled) {
            servicesAlert(context);
        }
        return isGPSEnabled || isNetworkEnabled;
    }

    /**
     * Set a services alert to turn on a service.
     *
     * @param context mobile context
     */
    public static void servicesAlert(final Context context) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("GPS Settings");
        alertDialog.setMessage("GPS is not enabled. Do you want to set it?");
        // prompt to set network settings
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }

        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }

        });

        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        mListener.onLocationRecieved(location);
        mManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
        mListener.isGPSAvailable(true);
    }

    @Override
    public void onProviderDisabled(String s) {
        mListener.isGPSAvailable(false);
    }


    public static interface LocationUpdateListener {

        public void onLocationRecieved(Location location);

        public void isGPSAvailable(boolean available);

    }

}
