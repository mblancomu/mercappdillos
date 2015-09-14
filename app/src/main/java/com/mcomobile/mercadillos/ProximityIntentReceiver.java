package com.mcomobile.mercadillos;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.MediaStore.Audio;
import android.util.Log;
import android.widget.Toast;



public class ProximityIntentReceiver extends BroadcastReceiver {
       private static final int NOTIFICATION_ID = 1000;      
       
       @SuppressWarnings("deprecation")
       @Override
       public void onReceive(Context context, Intent intent) {
    	   	
    	   String key = LocationManager.KEY_PROXIMITY_ENTERING;
    	   Boolean entering = intent.getBooleanExtra(key, false);
    	   if (entering) {
                     Log.d(getClass().getSimpleName(), "Llegando");
                     Toast.makeText(context, "Llegando a la zona de alerta.", Toast.LENGTH_LONG).show();
                   
              }else {
                     Log.d(getClass().getSimpleName(), "Saliendo");
                     Toast.makeText(context, "Saliendo de la zona de alerta.", Toast.LENGTH_LONG).show();
              }
              NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

              Intent notificationIntent = new Intent(context, Mapa.class);
              PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
              Notification notification = createNotification();
              notification.setLatestEventInfo(context, "Alerta de Proximidad!", "Te estas acercando a tu destino�", pendingIntent);
              notification.sound = Uri.withAppendedPath(Audio.Media.INTERNAL_CONTENT_URI, "1");

              notificationManager.notify(NOTIFICATION_ID, notification);              
                           
      }

       private Notification createNotification() {
              Notification notification = new Notification(); 
              notification.icon = R.drawable.alerta;
              notification.when = System.currentTimeMillis();
              notification.flags |= Notification.FLAG_AUTO_CANCEL;
              notification.flags |= Notification.FLAG_SHOW_LIGHTS;
              notification.sound = Uri.withAppendedPath(Audio.Media.INTERNAL_CONTENT_URI, "6");
              notification.defaults |= Notification.STREAM_DEFAULT;
              notification.ledARGB = Color.WHITE;
              notification.ledOnMS = 1500;
              notification.ledOffMS = 1500;              
              return notification;
              
        }
       
}




