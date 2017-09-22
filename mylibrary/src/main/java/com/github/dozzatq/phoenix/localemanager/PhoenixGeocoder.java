package com.github.dozzatq.phoenix.localemanager;

import android.location.Address;
import android.location.Geocoder;

import com.github.dozzatq.phoenix.Phoenix;
import com.github.dozzatq.phoenix.tasks.Task;
import com.github.dozzatq.phoenix.tasks.TaskSource;
import com.github.dozzatq.phoenix.tasks.Tasks;

import java.util.List;
import java.util.Locale;

/**
 * Created by Rodion Bartoshik on 5/24/17.
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

                    int countLines = returnedAddress.getMaxAddressLineIndex();

                    // something strange error with address and 0 lines length !
                    if (countLines==0)
                        countLines = 1;

                    for (int i = 0; i < countLines; i++) {
                        try {
                            strReturnedAddress.append(returnedAddress.getAddressLine(i));
                        }
                        catch (Exception e)
                        {

                        }
                        if (countLines>1 && countLines+1<i)
                        {
                            strReturnedAddress.append("\n");
                        }
                    }
                    strAdd = strReturnedAddress.toString();
                }
                return strAdd;
            }
        };
        return Tasks.execute(taskSource);
    }
}
