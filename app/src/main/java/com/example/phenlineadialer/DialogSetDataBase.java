package com.example.phenlineadialer;

import static com.example.phenlineadialer.helpers.MySharedPreferences.CODIGO_PH;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.phenlineadialer.databinding.DialogSetDatabaseBinding;
import com.example.phenlineadialer.helpers.MySharedPreferences;

@SuppressLint("ValidFragment")
public class DialogSetDataBase extends AppCompatDialogFragment {

    private final Context context;
    private MySharedPreferences mPreferences;
    private DialogSetDatabaseBinding binding;


    public DialogSetDataBase(Context context) {
        this.context = context;
        mPreferences = new MySharedPreferences(context);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();

        binding = DialogSetDatabaseBinding.inflate(inflater);
        this.setCancelable(false);
        init();

        builder.setView(binding.getRoot());
        return builder.create();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        String codigoBD = mPreferences.getPreferencesString(CODIGO_PH);
        if (!codigoBD.isEmpty()) super.onDismiss(dialog);
    }

    private void init() {

        binding.btnSetBd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String bdName = binding.txtDb.getText().toString();

                if (bdName.isEmpty()) {
                    Toast.makeText(context, "Debe seleccionar un nombre para la bd.", Toast.LENGTH_SHORT).show();
                    return;
                }

                mPreferences.editPreferencesString(CODIGO_PH, bdName);

                Toast.makeText(context, "Bd configurada con exito", Toast.LENGTH_SHORT).show();

                dismiss();
            }
        });
    }
}