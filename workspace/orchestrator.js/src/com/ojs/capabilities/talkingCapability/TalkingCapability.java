package com.ojs.capabilities.talkingCapability;

import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;

public class TalkingCapability {
	private TextToSpeech tts_;
	private boolean readyForUse_;
	private static Context applicationContext_;
	
	public void initCapability( Context applicationContext ) {
		TalkingCapability.applicationContext_ = applicationContext;
		tts_ = new TextToSpeech( TalkingCapability.applicationContext_, new OnInitListener() {
			@Override
			public void onInit(int status) {
				readyForUse_ = true;
			}
		});
	}

	public JSONObject say( String str, String filter, String pitch ) throws Exception {
		if( !readyForUse_ )
			throw new Exception( "TalkingCapability not yet initialized!" );
		tts_.setPitch( Float.parseFloat( pitch ) );
		tts_.speak( str, TextToSpeech.QUEUE_FLUSH, null );
		return null;
	}
}






/*

	public static boolean waitForVoiceFile = true;
	public static File dlVoiceFile = null;
	public static String dlVoiceUri = null;


	private MediaPlayer mediaPlayer = null;

*/


	/*

	@SuppressLint("NewApi")
	public JSONObject say(String str, String filter, String pitch) throws Exception {

		JSONObject retVal = new JSONObject();

		try {

			p("Running say() method");






			if(mediaPlayer == null) {
				mediaPlayer = new MediaPlayer();	
			}
			mediaPlayer.reset();

			if(filter.equals("")) {
				filter = "david";	
			}

			String encodedLine = URLEncoder.encode(str, "UTF-8");


			String urlToDownload = "http://social.cs.tut.fi/voice/synth?pitch="+pitch+"&speaker="+filter+"&text="+encodedLine;
			dlVoiceUri = urlToDownload;

			p("Getting voice file..");
			dlVoiceFile = null;
			new DownloadFileTask().execute(new String[] {""});
			p("Voice file get starts..");

			// TIMEOUT for 10s.
			long timeout = 10000;
			long sleepTime = 100; 
			long j = 0;
			while(waitForVoiceFile && j < timeout / sleepTime) {
				sleep(sleepTime);
				j++;
			}

			// Wait for file to finish
			sleep(100);

			p("Voice file downloaded....");
			File file = dlVoiceFile;

			if (file != null && file.exists()) {
				p("file not null and did exist");
				// Set to Readable and MODE_WORLD_READABLE
				file.setReadable(true, false);
				Uri myUri = Uri.parse(file.getAbsolutePath()); 

				for (int i = 0; i < 3; i++) {
					try {

						mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
						try {
							//mediaPlayer.reset();
							mediaPlayer.setDataSource(applicationContext, myUri);	
						} catch (IOException e) {
							p("second try for setDataSource");
							mediaPlayer.setDataSource(applicationContext, myUri);
						}

						mediaPlayer.prepare();
						mediaPlayer.start();
						Thread.sleep(mediaPlayer.getDuration());
						break;
					} catch (Exception e) {
						p(e.toString());
					}
				}
			}


			p("say() finished");


			return retVal;

		} catch (Exception e) {
			p("Error in TalkingDevice::say() - "+e.toString());
			throw e;
		} finally {
			if(mediaPlayer != null) {
				mediaPlayer.reset();	
			}
		}
	}
	 */

	/*

	public static class DownloadFileTask extends AsyncTask <String,Void,File> {

		@Override
		protected File doInBackground(String... arg0) {

			waitForVoiceFile = true;
			File file = null;

			try {

				//String encodedLine = URLEncoder.encode(str, "UTF-8");
				String urlToDownload = dlVoiceUri;//"http://131.228.164.158:8080/synth?speaker="+filter+"&text="+encodedLine ;
				p(urlToDownload);

				String fName = md5(urlToDownload) + ".wav";
				file = applicationContext.getFileStreamPath(fName);
				if (!file.exists()) {

					String fullVoicePath = applicationContext.getFilesDir().getPath().toString() + "/";
					fullVoicePath += fName;

					URL url = new URL(urlToDownload);
					URLConnection connection = url.openConnection();
					connection.connect();

					InputStream input = new BufferedInputStream(url.openStream());
					OutputStream output = new FileOutputStream(fullVoicePath);

					byte data[] = new byte[1024];
					int count;
					while ((count = input.read(data)) != -1) {          
						output.write(data, 0, count);
					}
					output.flush();
					output.close();
					input.close();
				}

			} catch (Exception e) {
				p("Error while downloading voice file: "+e.toString());
			}
			waitForVoiceFile = false;
			dlVoiceFile = file;
			return file; 
		}
	}
	 */
/*
	private static void p(String s){
		Log.e("TalkingDevice", s.toString());
	}


	private void sleep(long sleepTime) {
		try {Thread.sleep(sleepTime);} catch (InterruptedException e) {p("error in timeout: "+e.toString());}
	}

	public static String md5(String s) {
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			for (int i=0; i<messageDigest.length; i++)
				hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
			return hexString.toString();

		} catch (Exception e) {
			p("Error in calculating MD5: "+e.toString());
		}
		return "";
	}
}
*/