package fr.ycoupe.pronobike.utils;

import android.annotation.TargetApi;
import android.os.Build;

public class AsyncTask extends android.os.AsyncTask<Void,Void,Void> {

	protected Void doInBackground(Void... params) {
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB) // API 11
	public <T> void startTask() {
	    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
	        executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
	    else
	        execute();
	}
	
}