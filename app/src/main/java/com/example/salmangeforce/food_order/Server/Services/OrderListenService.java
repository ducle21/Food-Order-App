package com.example.salmangeforce.food_order.Server.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.example.salmangeforce.food_order.Common.Common;
import com.example.salmangeforce.food_order.Model.Request;
import com.example.salmangeforce.food_order.R;
import com.example.salmangeforce.food_order.Server.OrderStatusActivityServer;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OrderListenService extends Service {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    public OrderListenService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Request");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Request request = dataSnapshot.getValue(Request.class);
                assert request != null;
                if(request.getStatus().equals("0") && request.getPhone().equals(Common.currentUser.getPhone()))
                    showNotification(dataSnapshot.getKey(), request);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return super.onStartCommand(intent, flags, startId);
    }

    //Helper method
    private void showNotification(String key, Request request) {
        Intent intent = new Intent(getBaseContext(), OrderStatusActivityServer.class);
        intent.putExtra("phone", request.getPhone());
        PendingIntent pendingIntent = PendingIntent.getActivity
                (getBaseContext(), 0,  intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext());
        builder.setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setTicker("EDMT")
                .setContentTitle("Eat IT")
                .setContentText("Order #" + key + " is added")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_access_time_black_24dp);

        NotificationManager notificationManager = (NotificationManager) getBaseContext().getSystemService(NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.notify(2, builder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        firebaseDatabase = null;
        databaseReference = null;
    }
}
