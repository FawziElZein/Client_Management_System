package com.clientsinfo.ui.clients;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.clientsinfo.R;
import com.clientsinfo.ui.purchases.AddUpdatePurchase;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreateClient extends DialogFragment {

    public interface CreateClientListener {
        void onClientAdded(DialogFragment dialog);
    }

    private CreateClientListener listener;

    @BindView(R.id.client_name)
    public EditText clientName;

    @BindView(R.id.phone_number)
    public EditText phoneNumber;

    @BindView(R.id.address_details)
    public EditText addressDetails;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (CreateClientListener) ((AddUpdatePurchase) context).getCurrentFragment();

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        final View root = inflater.inflate(R.layout.activity_add_update_client, (ViewGroup) getView(), false);
        ButterKnife.bind(this, root);

        builder.setView(root).setTitle("Add client")
                .setPositiveButton("Set", null).
                setNegativeButton("Cancel", null);

        return builder.create();
    }


    @Override
    public void onStart() {
        super.onStart();


        AlertDialog alertDialog = (AlertDialog) getDialog();
        assert alertDialog != null;
        alertDialog.getButton(Dialog.BUTTON_POSITIVE)
                .setOnClickListener(view -> {
                    if (clientName.getText().toString().isEmpty()
                            || phoneNumber.getText().toString().isEmpty()
                            || addressDetails.getText().toString().isEmpty())
                        Toast.makeText(getContext(), "Fields shouldn't be empty", Toast.LENGTH_LONG).show();
                    else
                        listener.onClientAdded(CreateClient.this);

                });
        alertDialog.getButton(Dialog.BUTTON_NEGATIVE)
                .setOnClickListener(view -> dismiss());

    }
}
