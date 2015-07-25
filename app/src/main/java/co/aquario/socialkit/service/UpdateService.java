package co.aquario.socialkit.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateService extends AsyncTask<String, Integer, String> {
	private Context context;
	private PowerManager.WakeLock mWakeLock;

	ProgressDialog mProgressDialog;

	public void setContext(Context contextf, ProgressDialog mProgressDialogf) {
		context = contextf;
		mProgressDialog = mProgressDialogf;
	}

	@Override
	protected void onPreExecute() {
		PowerManager pm = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass()
				.getName());
		mWakeLock.acquire();
		mProgressDialog.show();

		// do initialization of required objects objects here
	};

	@Override
	protected String doInBackground(String... arg0) {
		try {
			URL url = new URL(arg0[0]);
			HttpURLConnection c = (HttpURLConnection) url.openConnection();
			c.setRequestMethod("GET");
			c.setDoOutput(true);
			c.connect();

			
			String PATH = "/mnt/sdcard/Download/";
			File file = new File(PATH);
			file.mkdirs();
			File outputFile = new File(file, "update.apk");
			if (outputFile.exists()) {
				outputFile.delete();
			}
			FileOutputStream fos = new FileOutputStream(outputFile);

			int fileLength = c.getContentLength();
			
			InputStream is = c.getInputStream();
			long total = 0;

			byte[] buffer = new byte[4096];
			int len1 = 0;
			while ((len1 = is.read(buffer)) != -1) {
				 total += len1;
				if (fileLength > 0) // only if total length is known
					publishProgress((int) (total * 100 / fileLength));
				fos.write(buffer, 0, len1);
			}
			fos.close();
			is.close();

			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(
					Uri.fromFile(new File("/mnt/sdcard/Download/update.apk")),
					"application/vnd.android.package-archive");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag
															// android returned
															// a intent error!
			context.startActivity(intent);

		} catch (Exception e) {
			Log.e("UpdateAPP", "Update error! " + e.getMessage());
		}

		return null;
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
		super.onProgressUpdate(progress);
		// if we get here, length is known, now set indeterminate to false
		mProgressDialog.setIndeterminate(false);
		mProgressDialog.setMax(100);
		mProgressDialog.setProgress(progress[0]);
	}

	@Override
	protected void onPostExecute(String result) {
		mWakeLock.release();
		mProgressDialog.dismiss();
		if (result != null)
			Toast.makeText(context, "Download error: " + result,
                    Toast.LENGTH_LONG).show();
		else
			Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT)
					.show();
	}
}
