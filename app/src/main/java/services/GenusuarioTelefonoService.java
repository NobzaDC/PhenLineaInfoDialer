package services;

import android.content.Context;
import android.widget.Toast;

import com.example.phenlineadialer.helpers.MySharedPreferences;
import com.example.phenlineadialer.models.GenusuarioTelefonos;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import services.interfaces.IGenusuarioTelefonoService;

public class GenusuarioTelefonoService {private Retrofit retrofit = null;

    public GenusuarioTelefonoService(Retrofit retrofit) {
        this.retrofit = retrofit;
    }


    public void getAllWithCode( Context context, Callback<List<GenusuarioTelefonos>> callback){
        IGenusuarioTelefonoService _service = retrofit.create(IGenusuarioTelefonoService.class);

        MySharedPreferences mPref = new MySharedPreferences(context);

        String nombreBd = mPref.getPreferencesString(MySharedPreferences.CODIGO_PH);

        if(nombreBd.isEmpty()){
            Toast.makeText(context, "No se encontro configuracion", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<List<GenusuarioTelefonos>> call = _service.getAllWithCode(nombreBd);

        call.enqueue(callback);
    }
}
