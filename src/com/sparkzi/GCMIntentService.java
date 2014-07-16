/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sparkzi;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.sparkzi.utility.SparkziApplication;
import com.sparkzi.utility.Utility;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService {

	private static final String TAG = "GCMIntentService";
	public static final int NOTIFICATION_ID = 1;
	@SuppressWarnings("unused")
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;
	SparkziApplication appInstance;

	Context context;

	public GCMIntentService() {
		super(Utility.SENDER_ID);

	}

	@SuppressWarnings("unused")
	@Override
	protected void onRegistered(Context context, String registrationId) {
		Log.i(TAG, "Device registered: regId = " + registrationId);
		// Utility.displayMessage(context, getString(R.string.gcm_registered));
		this.context = context;
		boolean registered = ServerUtilities.register(context, registrationId);
		// if (!registered) {
		// GCMRegistrar.unregister(context);
		// }
		// ServerUtilities.register(context, registrationId);
	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {
		Log.i(TAG, "Device unregistered");
		Utility.displayMessage(context, getString(R.string.gcm_unregistered));
		if (GCMRegistrar.isRegisteredOnServer(context)) {
			ServerUtilities.unregister(context, registrationId);
		} else {
			// This callback results from the call to unregister made on
			// ServerUtilities when the registration to the server failed.
			Log.i(TAG, "Ignoring unregister callback");
		}
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		Log.i(TAG, "-------------------------------------------- Received message");

		handleMessage(context, intent);
	}

	@SuppressWarnings("unused")
	@Override
	protected void onDeletedMessages(Context context, int total) {
		Log.i(TAG, "Received deleted messages notification");
		String message = getString(R.string.gcm_deleted, total);
		// Utility.displayMessage(context, message);
		// notifies user
		// generateNotification(context, message);
	}

	@Override
	public void onError(Context context, String errorId) {
		Log.i(TAG, "Received error: " + errorId);
		// Utility.displayMessage(context, getString(R.string.gcm_error,
		// errorId));
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		// log message
		Log.i(TAG, "Received recoverable error: " + errorId);
		// Utility.displayMessage(context,
		// getString(R.string.gcm_recoverable_error, errorId));
		return super.onRecoverableError(context, errorId);
	}

	private void handleMessage(Context context, Intent intent) {
		Log.e("Qv21 App", "Message received");
		Bundle extras = intent.getExtras();
		Log.e("EXTRAS", extras.toString());
		if (extras != null) {
			String receivedMsg = "" + (String) extras.get("message");
			Log.e("received message", (String) extras.get("message"));

			sendNotification(receivedMsg);

			/*
			 * GcmMessage gcmMessage = new GcmMessage(receivedMsg);
			 * 
			 * Qv21Database dbInstance = new Qv21Database(context);
			 * dbInstance.open(); long msgId =
			 * dbInstance.insertGCMMessage(gcmMessage); // dbInstance.close();
			 * 
			 * gcmMessage = dbInstance.retrieveById((int)msgId);
			 * Log.e("QV21 -> READING FROM LOCAL-DB", "just received msg = " +
			 * gcmMessage.getMsgContent());
			 * 
			 * dbInstance.close();
			 */
		}

	}

	private void sendNotification(String msg) {
		appInstance = (SparkziApplication) getApplication();
		Boolean rememberMeFlag = appInstance.isRememberMe();
		Intent myIntent;
		int MY_NOTIFICATION_ID = 1;

		if (rememberMeFlag) {
			myIntent = new Intent(this, MainActivity.class);
		} else {
			myIntent = new Intent(this, SplashActivity.class);
		}

		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, myIntent, Intent.FLAG_ACTIVITY_NEW_TASK);

		Notification myNotification = new NotificationCompat.Builder(this).setContentTitle("Sparkzi")
				.setContentText(msg).setTicker("Notification!").setWhen(System.currentTimeMillis())
				.setContentIntent(pendingIntent).setDefaults(Notification.DEFAULT_SOUND).setAutoCancel(true)
				.setSmallIcon(R.drawable.ic_launcher).build();

		NotificationManager notificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(MY_NOTIFICATION_ID, myNotification);

	}

}
