package com.example.phenlineadialer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.telecom.Call;
import android.telecom.InCallService;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import kotlin.collections.CollectionsKt;
import timber.log.Timber;

import static com.example.phenlineadialer.CallService.SPEAKER_OFF;
import static com.example.phenlineadialer.CallService.SPEAKER_ON;
import static com.example.phenlineadialer.Constants.asString;

import androidx.annotation.RequiresApi;

import com.example.phenlineadialer.Models.GenusuarioTelefonoModel;
import com.example.phenlineadialer.databinding.ActivityCallBinding;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class CallActivity extends AppCompatActivity {

    //VIEW
    private ActivityCallBinding binding;

    //CALL
    private CompositeDisposable disposables;
    private String number;
    private OngoingCall ongoingCall;

    //SOAP
    private final static String NAMESPACE = "http://tempuri.org/";
    private final static String METHOD_NAME = "ConsultarListaSQL";
    private final static String SOAP_ACTION = "http://tempuri.org/ConsultarListaSQL";
    private final static String URL = "https://www.phenlinea.info/WSPhEnlinea/InformesPhenlinea.asmx";

    final static String CAMPO = "GENUsuario.CodigoBien + ' - ' +  GENUsuarioTelefonos.Telefono+ ' - ' + nombre AS nombreProp, idUsuario, nombreCompleto AS nombreTelefono, email, prioridad";
    final static String TABLA = "GENUsuario inner join GENUsuarioTelefonos on GENUsuario.Id_Usuario = GENUsuarioTelefonos.IdUsuario";
    private String condicion = "";
    private final static String BD = "phenlinea";

    private SoapPrimitive result;
    private String message = "";

    private ObjectMapper mapper;
    private boolean speakerState = false;

    private Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ongoingCall = new OngoingCall();
        disposables = new CompositeDisposable();

        mapper = new ObjectMapper();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        number = Objects.requireNonNull(getIntent().getData()).getSchemeSpecificPart().trim();

        binding.btnAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ongoingCall.answer();
            }
        });

        binding.btnHangup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ongoingCall.hangup();
            }
        });

        binding.btnSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchSpeaker(v);
            }
        });
    }

    private void switchSpeaker(View v) {


        serviceIntent = new Intent(this, CallService.class);

        if(speakerState){
            binding.btnSpeaker.setImageResource(R.drawable.ic_speaker_off);
            serviceIntent.addCategory(SPEAKER_OFF);
        }
        else{
            binding.btnSpeaker.setImageResource(R.drawable.ic_speaker_on);
            serviceIntent.addCategory(SPEAKER_ON);
        }
        startService(serviceIntent);
        speakerState = !speakerState;


    }

    private void createSoapConfig() {
        condicion = "WHERE GENUsuarioTelefonos.Telefono = '"+ number +"'";

        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            Request.addProperty("campo", CAMPO);
            Request.addProperty("tabla", TABLA);
            Request.addProperty("condicion", condicion);
            Request.addProperty("bd", BD);
            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);
            HttpTransportSE transport = new HttpTransportSE(URL);
            transport.call(SOAP_ACTION, soapEnvelope);
            result = (SoapPrimitive) soapEnvelope.getResponse();
            message = "OK";
        } catch (Exception e) {
            message = "Error: " + e.getMessage();
        }
    }

    public class GetUsuarioByNumber extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            createSoapConfig();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if ((message.equals("OK"))) {
                String value = result.toString();

                if (value.equals("")) {
                    binding.callName.setText(number);
                    return;
                }

                try {
                    List<GenusuarioTelefonoModel> userLst = mapper.readValue(value, new TypeReference<List<GenusuarioTelefonoModel>>(){});

                    if(userLst.size() == 0) {
                        binding.callName.setText(number);
                        return;
                    }

                    String[] dataTitle = userLst.get(0).getNombreProp().split("-");

                    String title = dataTitle[0] + " - " +  userLst.get(0).getNombreTelefono();

                    binding.callName.setText(title);

                } catch (IOException e) {
                    Timber.tag("Error").d(e);
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        assert updateUi(-1) != null;

        GetUsuarioByNumber soapRequest = new GetUsuarioByNumber();
        soapRequest.execute();

        disposables.add(
                OngoingCall.state
                        .subscribe(new Consumer<Integer>() {
                            @Override
                            public void accept(Integer integer) throws Exception {
                                updateUi(integer);
                            }
                        }));

        disposables.add(
                OngoingCall.state
                        .filter(new Predicate<Integer>() {
                            @Override
                            public boolean test(Integer integer) throws Exception {
                                return integer == Call.STATE_DISCONNECTED;
                            }
                        })
                        .delay(1, TimeUnit.SECONDS)
                        .firstElement()
                        .subscribe(new Consumer<Integer>() {
                            @Override
                            public void accept(Integer integer) throws Exception {
                                finish();
                            }
                        }));

    }

    @SuppressLint("SetTextI18n")
    private Consumer<? super Integer> updateUi(Integer state) {

        binding.callState.setText(asString(state));

        if (state == Call.STATE_DIALING) { //DIALING
            binding.speakerContainer.setVisibility(View.VISIBLE);
        } else {
            binding.btnAnswer.setVisibility(View.GONE);
        }

        if (state == Call.STATE_RINGING) { //RINGING
            binding.btnHangup.setVisibility(View.VISIBLE);
            binding.btnAnswer.setVisibility(View.VISIBLE);
        } else {
            binding.btnAnswer.setVisibility(View.GONE);
        }

        if (state == Call.STATE_ACTIVE){ //IN CALL
            binding.btnHangup.setVisibility(View.VISIBLE);

            binding.speakerContainer.setVisibility(View.VISIBLE);
            binding.callTimer.setBase(SystemClock.elapsedRealtime());
            binding.callTimer.stop();

            binding.callTimer.setVisibility(View.VISIBLE);
            binding.callTimer.start();
            //record();
        }

        if(state == Call.STATE_DISCONNECTED){ //CALL END
        //stopRecord();
        binding.callTimer.stop();
        binding.btnHangup.setVisibility(View.GONE);
        binding.speakerContainer.setVisibility(View.GONE);

        }
        return null;
    }

    @Override
    protected void onStop() {
    super.onStop();
        disposables.clear();
    }

    public static void start(Context context, Call call ) {
        Intent intent = new Intent(context, CallActivity.class)
        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        .setData(call.getDetails().getHandle());

        context.startActivity(intent);
    }
}
