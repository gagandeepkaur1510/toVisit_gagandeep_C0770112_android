package com.example.tovisit_gagandeep_c0770112_android;

import android.location.Address;
import android.text.format.DateFormat;

import java.util.Date;

public class Util {
    public static String getTitle(Address address)
    {
        String title = "";
        try {
            title += address.getFeatureName() + " ";
            title += address.getThoroughfare() + " ";
            title += address.getLocality() + " ";
            title += address.getAdminArea();
            title = title.trim();
        }
        catch (Exception e)
        {}
        if(title.isEmpty())
        {
            return new DateFormat().format("EEE, MM-dd-yyyy hh:mm", new Date()).toString();
        }
        return title;
    }
}
