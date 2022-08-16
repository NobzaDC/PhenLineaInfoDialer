package com.example.phenlineadialer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telecom.TelecomManager;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.example.phenlineadialer.databinding.ActivityDialerBinding;

import kotlin.collections.ArraysKt;

import static android.Manifest.permission.CALL_PHONE;
import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;
import static android.telecom.TelecomManager.ACTION_CHANGE_DEFAULT_DIALER;
import static android.telecom.TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME;

public class DialerActivity extends AppCompatActivity implements View.OnClickListener{

    private ActivityDialerBinding binding;
    public static int REQUEST_PERMISSION = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDialerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getIntent() != null && getIntent().getData() != null)
            binding.phoneNumberInput.setText(getIntent().getData().getSchemeSpecificPart());

        binding.phoneNumberInput.setInputType(InputType.TYPE_NULL);
    }

    @Override
    protected void onStart() {
        super.onStart();
        offerReplacingDefaultDialer();
        requestAudioRecord();

        binding.btn0.setOnClickListener(this);
        binding.btn1.setOnClickListener(this);
        binding.btn2.setOnClickListener(this);
        binding.btn3.setOnClickListener(this);
        binding.btn4.setOnClickListener(this);
        binding.btn5.setOnClickListener(this);
        binding.btn6.setOnClickListener(this);
        binding.btn7.setOnClickListener(this);
        binding.btn8.setOnClickListener(this);
        binding.btn9.setOnClickListener(this);
        binding.btnErase.setOnClickListener(this);
        binding.btnClear.setOnClickListener(this);

        binding.btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeCall();
            }
        });
    }

    private void requestAudioRecord() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DialerActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 1000);
        }
    }

    @Override
    public void onClick(View v) {
        String message = binding.phoneNumberInput.getText().toString();

        if (v.getId() == binding.btn0.getId()) {
            message = String.valueOf(Integer.parseInt(message) * 10);
        } else if (v.getId() == binding.btn1.getId()) {
            if (message.equals("0"))
                message = "1";
            else
                message += "1";
        } else if (v.getId() == binding.btn2.getId()) {
            if (message.equals("0"))
                message = "2";
            else
                message += "2";
        } else if (v.getId() == binding.btn3.getId()) {
            if (message.equals("0"))
                message = "3";
            else
                message += "3";
        } else if (v.getId() == binding.btn4.getId()) {
            if (message.equals("0"))
                message = "4";
            else
                message += "4";
        } else if (v.getId() == binding.btn5.getId()) {
            if (message.equals("0"))
                message = "5";
            else
                message += "5";
        } else if (v.getId() == binding.btn6.getId()) {
            if (message.equals("0"))
                message = "6";
            else
                message += "6";
        } else if (v.getId() == binding.btn7.getId()) {
            if (message.equals("0"))
                message = "7";
            else
                message += "7";
        } else if (v.getId() == binding.btn8.getId()) {
            if (message.equals("0"))
                message = "8";
            else
                message += "8";
        } else if (v.getId() == binding.btn9.getId()) {
            if (message.equals("0"))
                message = "9";
            else
                message += "9";
        } else if (v.getId() == binding.btnClear.getId()) {
            message = "0";
        } else if (v.getId() == binding.btnErase.getId()) {
            if (message.length() == 1)
                message = "0";
            else if (message.length() >= 2)
                message = message.substring(0, message.length() - 1);
        }

            binding.phoneNumberInput.setText(message);
    }

    private void makeCall() {
        if (android.support.v4.content.PermissionChecker.checkSelfPermission(this, CALL_PHONE) == PERMISSION_GRANTED && !binding.phoneNumberInput.getText().toString().equals("")) {
            Uri uri = Uri.parse("tel:"+binding.phoneNumberInput.getText().toString().trim());
            startActivity(new Intent(Intent.ACTION_CALL, uri));
        }
    }

    private void offerReplacingDefaultDialer() {
        TelecomManager telecomManager = (TelecomManager) getSystemService(TELECOM_SERVICE);

        if (!getPackageName().equals(telecomManager.getDefaultDialerPackage())) {
            Intent intent = new Intent(ACTION_CHANGE_DEFAULT_DIALER)
                    .putExtra(EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, getPackageName());
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION && ArraysKt.contains(grantResults, PERMISSION_GRANTED)) {
            makeCall();
        }
    }
}
