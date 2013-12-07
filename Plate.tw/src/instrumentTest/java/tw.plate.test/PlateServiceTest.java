package tw.plate.test;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;


import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import tw.plate.MainActivity;
import tw.plate.PlateService;
import tw.plate.PlateService.PlateTWAPI1;

public class PlateServiceTest extends ActivityInstrumentationTestCase2<MainActivity> {
	public static final String HTTP_PROXY_HOST = "192.168.1.2";
	public static final String HTTP_PROXY_PORT = "8080";
	public static final String API_URI = "http://10.0.2.2:8000";// "http://192.168.1.2:8000";
	private static final String TAG = "PlateServiceTest";

	private static PlateTWAPI1 plateTWV1;

	public PlateServiceTest() {
		super(MainActivity.class);
	}

	@Override protected void setUp() throws Exception {
		super.setUp();
		if (false) {
			System.setProperty("http.proxyHost", HTTP_PROXY_HOST);
			System.setProperty("http.proxyPort", HTTP_PROXY_PORT);
		}
		plateTWV1 = PlateService.getAPI1(API_URI);
	}

	public void testRegister() {
		final Object callDone = new Object();

		plateTWV1.register("0977777777", "mypassword", "raw", new Callback<Response>() {
			@Override public void success(Response r, Response response) {
				assertTrue(true);
				synchronized (callDone) {
					callDone.notify();
				}
			}
			@Override public void failure(RetrofitError e) {
				String t;
				try {
					t = e.getResponse().getReason();
				} catch (NullPointerException npe) {
					t = null;
				}
				if (t == null || t.equals(""))
					t = e.getMessage();
				Log.e(TAG, t);
				assertTrue(false);
				synchronized (callDone) {
					callDone.notify();
				}
			}
		});
		synchronized (callDone) {
			try {
				callDone.wait();
			} catch (InterruptedException e) {
				assertTrue(false);
			}
		}
	}
}
