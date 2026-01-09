package com.example.musapiapp.workers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.musapiapp.R;
import com.example.musapiapp.activities.menu.MenuPrincipalActivity;
import com.example.musapiapp.dto.NotificacionDTO;
import com.example.musapiapp.network.ApiCliente;
import com.example.musapiapp.servicios.NotificacionServicio;
import com.example.musapiapp.util.SesionUsuario;

import java.util.List;
import retrofit2.Response;

public class NotificacionWorker extends Worker {

    private static final String CHANNEL_ID = "canal_musapi_notificaciones";

    public NotificacionWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        int idUsuario = SesionUsuario.getIdUsuario();
        if (idUsuario <= 0) {
            return Result.failure();
        }


        try {
            NotificacionServicio servicio = ApiCliente.getClient().create(NotificacionServicio.class);
            Response<List<NotificacionDTO>> respuesta = servicio.obtenerPendientes(idUsuario).execute();

            if (respuesta.isSuccessful() && respuesta.body() != null) {
                List<NotificacionDTO> notificaciones = respuesta.body();


                for (NotificacionDTO noti : notificaciones) {
                    mostrarNotificacionSistema(noti);
                    servicio.marcarComoLeida(noti.getIdNotificacion()).execute();
                }
                return Result.success();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.retry();
        }

        return Result.success();
    }

    private void mostrarNotificacionSistema(NotificacionDTO notificacion) {
        crearCanalNotificacion();


        Intent intent = new Intent(getApplicationContext(), MenuPrincipalActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_logo)
                .setContentTitle("MusAPI")
                .setContentText(notificacion.getMensaje())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());


        try {
            notificationManager.notify(notificacion.getIdNotificacion(), builder.build());
        } catch (SecurityException e) {
        }
    }

    private void crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notificaciones MusAPI";
            String description = "Avisos de nuevos artistas y canciones";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
