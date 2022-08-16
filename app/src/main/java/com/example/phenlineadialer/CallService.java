package com.example.phenlineadialer;

import static android.telecom.CallAudioState.ROUTE_EARPIECE;
import static android.telecom.CallAudioState.ROUTE_SPEAKER;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.telecom.Call;
import android.telecom.CallAudioState;
import android.telecom.InCallService;
import android.telecom.TelecomManager;
import android.view.View;

public class CallService extends InCallService {

    public static final String SPEAKER_ON = "TURN_SPEAKER_ON";
    public static final String SPEAKER_OFF = "TURN_SPEAKER_OFF";

    @Override
    public void onCallAdded(Call call) {
        super.onCallAdded(call);
        new OngoingCall().setCall(call);
        CallActivity.start(this, call);
    }

    @Override
    public void onCallRemoved(Call call) {
        super.onCallRemoved(call);
        new OngoingCall().setCall(null);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null){
            if(intent.hasCategory(SPEAKER_ON))
                activateSpeaker();
            else if(intent.hasCategory(SPEAKER_OFF))
                deactivateSpeaker();
        }
        return START_STICKY;
    }

    public void activateSpeaker(){
        setAudioRoute(ROUTE_SPEAKER);
    }

    public void deactivateSpeaker(){
        setAudioRoute(ROUTE_EARPIECE);
    }
}
