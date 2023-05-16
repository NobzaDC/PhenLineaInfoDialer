package com.example.phenlineadialer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.telecom.Call;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import retrofit2.Callback;
import retrofit2.Response;
import services.GenusuarioTelefonoService;

import static com.example.phenlineadialer.CallService.SPEAKER_OFF;
import static com.example.phenlineadialer.CallService.SPEAKER_ON;
import static com.example.phenlineadialer.Constants.asString;

import com.example.phenlineadialer.databinding.ActivityCallBinding;
import com.example.phenlineadialer.helpers.MyRetrofit;
import com.example.phenlineadialer.models.GenusuarioTelefonos;

public class CallActivity extends AppCompatActivity {

    //VIEW
    private ActivityCallBinding binding;

    //CALL
    private CompositeDisposable disposables;
    private String number;
    private OngoingCall ongoingCall;

    private boolean speakerState = false;

    private Intent serviceIntent;

    //RECORDING
    MediaRecorder mediaRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ongoingCall = new OngoingCall();
        disposables = new CompositeDisposable();

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

    @Override
    protected void onStart() {
        super.onStart();

        assert updateUi(-1) != null;

        GenusuarioTelefonoService usuarioService = new GenusuarioTelefonoService(new MyRetrofit().getRetrofitConfig());
        usuarioService.getAllWithCode(CallActivity.this, new Callback<List<GenusuarioTelefonos>>() {
            @Override
            public void onResponse(retrofit2.Call<List<GenusuarioTelefonos>> call, Response<List<GenusuarioTelefonos>> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(CallActivity.this, "Error: " + response.errorBody().toString(), Toast.LENGTH_SHORT).show();
                    binding.callName.setText(number);
                    return;
                }

                List<GenusuarioTelefonos> lstUsers = response.body();
                if(lstUsers == null || lstUsers.size() == 0){
                    binding.callName.setText(number);
                    return;
                }
                GenusuarioTelefonos userToUse = null;
                for (GenusuarioTelefonos user: lstUsers) {
                    if(user.getTelefono().trim().equals(number.trim())){
                        userToUse = user;
                        break;
                    }
                }

                if(userToUse == null) {
                    binding.callName.setText(number);
                }else{
                    binding.callName.setText(userToUse.getNombreCompleto());
                    /*if(userToUse.getTipo() == null) return;

                    if(userToUse.getTipo().equals("M")){
                        binding.callType.setText("MOVIL");
                    }else if(userToUse.getTipo().equals("F")) {
                        binding.callType.setText("FIJO");
                    }else {
                        binding.callType.setText("No identificado");
                    }*/
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<GenusuarioTelefonos>> call, Throwable t) {
                binding.callName.setText(number);
                Toast.makeText(CallActivity.this, "Fatal error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

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
            //onRecord();
        }

        if(state == Call.STATE_DISCONNECTED){ //CALL END
        //onStopRecord();
        binding.callTimer.stop();
        binding.btnHangup.setVisibility(View.GONE);
        binding.speakerContainer.setVisibility(View.GONE);
        this.finishAffinity();
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
