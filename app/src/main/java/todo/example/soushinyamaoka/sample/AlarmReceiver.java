package todo.example.soushinyamaoka.sample;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
    public static String NOTIFICATION_ID = "notificationId";
    public static String NOTIFICATION_CONTENT = "content";
    private int requestCode;

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
        requestCode = intent.getIntExtra("requestCode", 0);
        String content = intent.getStringExtra(NOTIFICATION_CONTENT);
        notificationManager.notify(id, buildNotification(context, content));
    }

    private Notification buildNotification(Context context, String content) {
        Intent resultIntent = new Intent(context, TodoDetail.class);
        resultIntent.putExtra("todoId",requestCode);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(context,
                                        requestCode,
                                        resultIntent,
                                        PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentTitle("リマインダー")
                .setContentText(content)
                .setAutoCancel(true)
                .setSmallIcon(android.R.drawable.sym_def_app_icon);

        builder.setContentIntent(resultPendingIntent);
        // click remove
        builder.build().flags |= Notification.FLAG_AUTO_CANCEL;

        return builder.build();
    }
}
