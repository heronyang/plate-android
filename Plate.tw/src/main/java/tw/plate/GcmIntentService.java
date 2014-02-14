package tw.plate;
import android.app.AlertDialog;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import static tw.plate.GcmUtilities.SENDER_ID;
import static tw.plate.GcmUtilities.displayMessage;

/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class GcmIntentService extends IntentService {

    public static final int NOTIFICATION_ID = 1;
    NotificationManager mNotificationManager;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        //
        if (extras!= null &&  !extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM will be
             * extended in the future with new message types, just ignore any message types you're
             * not interested in, or that you don't recognize.
             */
            switch (messageType) {
                case GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR:
                    //sendNotification("Send error: " + extras.toString());
                    break;
                case GoogleCloudMessaging.MESSAGE_TYPE_DELETED:
                    //sendNotification("Deleted messages on server: " + extras.toString());
                    // If it's a regular GCM message, do some work.
                    break;
                case GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE:
                    messageReceiveHandler(extras);
                    break;
                default:
                    Log.d(Constants.LOG_TAG, "message type no listed");
                    break;
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void messageReceiveHandler(Bundle extras) {

        // ignore wrong receiver here
        SharedPreferences sp = getSharedPreferences("account", 0);
        if (sp.contains(Constants.SP_TAG_PHONE_NUMBER) && extras.containsKey("username")){
            String phone_number = sp.getString(Constants.SP_TAG_PHONE_NUMBER, null);
            String gcm_receiver = extras.get("username").toString();
            if (!phone_number.equals(gcm_receiver)) {
                Log.d(Constants.LOG_TAG, "Message dismissed, original receiver: " + gcm_receiver);
                return;
            }
        }

        if (!sp.contains(Constants.SP_TAG_PHONE_NUMBER)) {
            Log.d(Constants.LOG_TAG, "Message dismissed, no user registered in this phone");
            return;
        }

        // Post notification of received message.
        String title, message, ticker;

        if (extras.containsKey("title")) {
            title = extras.get("title").toString();
        } else {
            title = getString(R.string.app_name);
        }

        if (extras.containsKey("message")) {
            message = extras.get("message").toString();
        } else {
            message = "";
        }

        if (extras.containsKey("ticker")) {
            ticker = extras.get("ticker").toString();
        } else {
            ticker = message;
        }
        Log.d(Constants.LOG_TAG, "title:" + title + "\tmessage:" + message + "\tticker:" + ticker);
        sendNotification(title, message, ticker);
    }


    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String title, String message, String ticker) {
        int icon = R.drawable.ic_launcher;
        long when = System.currentTimeMillis();

        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("fragPosition",1);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                intent, 0);

        Uri ringtonePath = Uri.parse("android.resource://tw.plate/" + R.raw.ringtone_finish);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(icon)
                        .setContentTitle(title)
                        .setWhen(when)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                        .setAutoCancel(true)
                        .setTicker(ticker)
                        .setSound(ringtonePath)
                        .setContentText(message);

        //.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
