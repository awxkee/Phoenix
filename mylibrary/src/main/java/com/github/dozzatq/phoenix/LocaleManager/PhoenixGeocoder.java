package com.github.dozzatq.phoenix.LocaleManager;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import com.github.dozzatq.phoenix.Phoenix;
import com.github.dozzatq.phoenix.Util.Task;

import java.util.List;
import java.util.Locale;

/**
 * Created by rodeon on 5/24/17.
 */

public class PhoenixGeocoder {
    public static Task<String> newGeocoderTask(double latitude, double longtitude)
    {
        final GeodecoderTask geodecoderTask = new GeodecoderTask(latitude, longtitude);
        Task<String> task = new Task<String>() {
            @Override
            public void cancelTask() {
                geodecoderTask.cancel(true);
            }
        };
        geodecoderTask.setTask(task);
        geodecoderTask.execute();
        return task;
    }

    private static class GeodecoderTask extends AsyncTask<Void, Void, GeoTaskResult>{

        private double latitude;
        private double longtitude;
        private Task<String> task;

        private GeodecoderTask(double latitude, double longtitude) {
            this.latitude = latitude;
            this.longtitude = longtitude;
        }

        @Override
        protected void onPostExecute(GeoTaskResult geoTaskResult) {
            super.onPostExecute(geoTaskResult);
            if(!geoTaskResult.isResult()) {
                this.task.notifyFailureListener(geoTaskResult.getException());
            } else {
                this.task.notifyCompleteListeners(geoTaskResult.getResultString());
            }

        }

        @Override
        protected GeoTaskResult doInBackground(Void... voids) {

            String strAdd = "";
            Geocoder geocoder = new Geocoder(Phoenix.getInstance().getContext(), Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longtitude, 1);
                if (addresses != null) {
                    Address returnedAddress = addresses.get(0);
                    StringBuilder strReturnedAddress = new StringBuilder("");

                    for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                        strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                    }
                    strAdd = strReturnedAddress.toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return new GeoTaskResult(e, null, false);
            }
            return new GeoTaskResult(null, strAdd, true);
        }

        public void setTask(Task<String> task) {
            this.task = task;
        }
    }

    private static class GeoTaskResult {
        private Exception exception;
        private String resultString;
        private boolean result;

        private GeoTaskResult(Exception exception, String resultString, boolean result) {
            this.exception = exception;
            this.resultString = resultString;
            this.result = result;
        }

        public boolean isResult() {
            return this.result;
        }

        public String getResultString() {
            return this.resultString;
        }

        public Exception getException() {
            return this.exception;
        }
    }
}
