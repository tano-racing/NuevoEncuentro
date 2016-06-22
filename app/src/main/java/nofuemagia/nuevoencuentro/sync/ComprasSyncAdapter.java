package nofuemagia.nuevoencuentro.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.List;


import cz.msebera.android.httpclient.Header;
import nofuemagia.nuevoencuentro.helper.DatabaseHelper;
import nofuemagia.nuevoencuentro.model.Actividades;
import nofuemagia.nuevoencuentro.model.Caracteristica;

/**
 * Created by jlionti on 06/06/2016. No Fue Magia
 */
public class ComprasSyncAdapter extends AbstractThreadedSyncAdapter {

    String urlActividades = "http://nofuemagia.site88.net/backend/actividades/listActividades.php?fid=-1";


    private DatabaseHelper dbHelper;

    public ComprasSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        System.out.println("Sincronizando, Con errores? " + syncResult.hasError());


        SyncHttpClient client = new SyncHttpClient();

        String que = extras.getString("QUE");
        if (que != null && que.equals("Actividades")) {
            SincronizarActividades(urlActividades, client);
        }

        //SincronizarCaracteristicas("http://magyp-iis-desa.magyp.ar:8027/Home/GetCaracteristicas", client);
    }

    private void SincronizarActividades(String url, SyncHttpClient client) {
        client.get(url, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                //super.onSuccess(statusCode, headers, response);

                ActiveAndroid.beginTransaction();
                try {

                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<Actividades>>() {
                    }.getType();


                    for (Actividades local : Actividades.GetAll()){
                        new Delete().from(Actividades.class).where("idActividad = ?", local.getId()).execute();
                    }

                    List<Actividades> remoto = gson.fromJson(response.toString(), listType);
                    for (Actividades carRemoto : remoto) {
                        Actividades nueva = new Actividades(carRemoto.idActividad, carRemoto.nombre, carRemoto.descripcion, carRemoto.cuando, carRemoto.repeticion);
                        nueva.save();
                    }

                    ActiveAndroid.setTransactionSuccessful();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                } finally {
                    System.out.println("Tres2");
                    ActiveAndroid.endTransaction();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    private void SincronizarCaracteristicas(String url, SyncHttpClient client) {
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);

                ActiveAndroid.beginTransaction();
                try {
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<Caracteristica>>() {
                    }.getType();


                    List<Caracteristica> remoto = gson.fromJson(response.toString(), listType);
                    System.out.println("Lista Remota = " + remoto.size());
                    for (Caracteristica carRemoto : remoto) {
                        Caracteristica enBase = new Select().from(Caracteristica.class).where(Caracteristica.NOMBRE + " = ?", carRemoto.getNombre()).executeSingle();
                        if (enBase == null) {
                            Caracteristica nueva = new Caracteristica(carRemoto.getNombre(), carRemoto.getCantidad());
                            nueva.save();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    ActiveAndroid.endTransaction();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }


}