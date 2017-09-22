package com.github.dozzatq.phoenix.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;

/**
 * Created by Rodion Bartoshik on 27.06.2017.
 */

public class ActivitySupplier{

   protected ActivityConnectorStrategy connectorStrategy;

   public ActivitySupplier(ActivityConnectorStrategy connectorStrategy)
   {
      this.connectorStrategy = connectorStrategy;
   }

   public Activity getActivity()
   {
      return connectorStrategy.getActivity();
   }

   @MainThread
   public void onCreate(Bundle bundle) {
   }

   @MainThread
   public void onStart() {
   }

   @MainThread
   public void onResume() {
   }

   @MainThread
   public void onSaveInstanceState(Bundle bundle) {
   }

   @MainThread
   public void onActivityResult(int requestCode, int resultCode, Intent dataIntent) {
   }

   @MainThread
   public void onStop() {
   }

   @MainThread
   public void onDestroy() {
   }
}
