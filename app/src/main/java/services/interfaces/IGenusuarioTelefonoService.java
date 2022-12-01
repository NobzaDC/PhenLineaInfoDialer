package services.interfaces;

import com.example.phenlineadialer.models.GenusuarioTelefonos;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface IGenusuarioTelefonoService {

    @GET("v1/UsuarioTelefonos/getAllWithCode/{bd}")
    Call<List<GenusuarioTelefonos>> getAllWithCode(@Path("bd") String bd);
}
