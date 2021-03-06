package rainvisitor.personal_assistant.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import rainvisitor.personal_assistant.R;

import static com.google.android.gms.internal.zzs.TAG;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private Context context = this;

    public FirebaseMessagingService() {

    }

    public void onDestroy() {
        super.onDestroy();
        //Toast.makeText(this, "Service stop", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            // 建立NotificationCompat.Builder物件
            Notification.InboxStyle style = new Notification.InboxStyle();
            style.addLine(remoteMessage.getNotification().getBody());
            style.setSummaryText("內容");
            long[] vib = { 50, 400, 200, 400, 50, 400 };
            Notification builder =
                    new Notification.Builder(context)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setWhen(System.currentTimeMillis())
                            .setContentTitle(remoteMessage.getNotification().getTitle())
                            .setContentText("資訊")
                            .setLargeIcon(BitmapFactory.decodeResource(
                                    getResources(), R.drawable.ic_notifications))
                            .setStyle(style)
                            .setVibrate(vib)
                            .setCategory(NotificationCompat.CATEGORY_PROMO)
                            .setPriority(Notification.PRIORITY_HIGH)
                            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                            .build();
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // 設定小圖示、大圖示、狀態列文字、時間、內容標題、內容訊息和內容額外資訊
            manager.notify(remoteMessage.getData().size(), builder);
        }
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
}
