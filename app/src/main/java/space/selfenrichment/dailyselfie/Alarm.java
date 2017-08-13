package space.selfenrichment.dailyselfie;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import space.selfenrichment.dailyselfie.lib.Defs;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by roberto on 12/08/2017.
 */

public class Alarm extends BroadcastReceiver {
    private static final long INTERVAL_TWO_MINUTES = 2 * 60 * 1000;
    public static final int ALARM_TYPE_ELAPSED = 1;
    public static final int ALARM_TYPE_RTC = 2;
    private static AlarmManager alarmManagerElapsed;
    private static PendingIntent alarmIntent;

// TODO - change this to an RTC alarm when we go daily
    public static void scheduleRepeatingElapsedNotification(Context context) {
        Log.i(Defs.TAG, "scheduleRepeatingElapsedNotification - hope we see a notification!");

        Intent intent = new Intent(context, Alarm.class);

        alarmIntent = PendingIntent.getBroadcast(context, ALARM_TYPE_ELAPSED, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManagerElapsed = (AlarmManager)context.getSystemService(ALARM_SERVICE);


        boolean alarmExists = (
                PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_NO_CREATE)
                        != null);

        Log.i(Defs.TAG, "alarmExists:" + alarmExists);

        if(!alarmExists)
            alarmManagerElapsed.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + INTERVAL_TWO_MINUTES, INTERVAL_TWO_MINUTES,
                alarmIntent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null && action.equals("android.intent.action.BOOT_COMPLETED")) {
            scheduleRepeatingElapsedNotification(context);
        } else {
            Intent intentToRepeat = new Intent(context, MainActivity.class);
            intentToRepeat.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent =
                    PendingIntent.getActivity(context, ALARM_TYPE_RTC, intentToRepeat,
                            PendingIntent.FLAG_UPDATE_CURRENT);

            Notification repeatedNotification = buildLocalNotification(context, pendingIntent).build();

            NotificationManager no_man = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            no_man.notify(ALARM_TYPE_RTC, repeatedNotification);
        }
    }

    public NotificationCompat.Builder buildLocalNotification(Context context,
                                                             PendingIntent pendingIntent) {
        NotificationCompat.Builder builder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(android.R.drawable.ic_menu_camera)
                        .setContentTitle(context.getText(R.string.app_name))
                        .setContentText(context.getText(R.string.notificationText))
                        .setAutoCancel(true);

        return builder;
    }

}
