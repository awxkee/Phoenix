package com.github.dozzatq.phoenix.LocaleManager56;

import android.location.Address;
import android.location.Geocoder;

import com.github.dozzatq.phoenix.Phoenix;
import com.github.dozzatq.phoenix.Tasks56.Task;
import com.github.dozzatq.phoenix.Tasks56.TaskSource;
import com.github.dozzatq.phoenix.Tasks56.Tasks;

import java.util.List;
import java.util.Locale;

/**
 * Created by rodeon on 5/24/17.
 */

public class PhoenixGeocoder {
    public static Task<String> newGeocoderTask(final double latitude, final double longtitude) {
        TaskSource<String> taskSource = new TaskSource<String>() {
            @Override
            public String call() throws Exception {
                String strAdd = "";
                Geocoder geocoder = new Geocoder(Phoenix.getInstance().getContext(), Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(latitude, longtitude, 1);
                if (addresses != null) {
                    Address returnedAddress = addresses.get(0);
                    StringBuilder strReturnedAddress = new StringBuilder("");

                    for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                        strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                    }
                    strAdd = strReturnedAddress.toString();
                }
                return strAdd;
            }
        };
        return Tasks.execute(taskSource);
    }
}
