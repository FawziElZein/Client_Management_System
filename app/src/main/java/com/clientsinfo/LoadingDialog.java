package com.clientsinfo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoadingDialog extends DialogFragment {


    @BindView(R.id.message)
    TextView message;

    private String textMessage;

    public LoadingDialog(String textMessage) {
        this.textMessage = textMessage;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View root = inflater.inflate(R.layout.loading_dialog, (ViewGroup) getView(), false);
        builder.setView(root);
        ButterKnife.bind(this, root);
        message.setText(textMessage);
        return builder.create();
    }
}
