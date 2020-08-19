package com.clientsinfo.ui.purchases;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.clientsinfo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PaymentMethod extends DialogFragment {

    public interface PaymentDialogListener {
        void onPaymentDialogPositiveClick(DialogFragment dialog);
    }

    private PaymentDialogListener listener;

    @BindView(R.id.header)
    TextView header;

    @BindView(R.id.cash)
    EditText cash_text;

    @BindView(R.id.debt)
    TextView debt_text;

    @BindView(R.id.check)
    EditText check_text;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        final View root = inflater.inflate(R.layout.activity_payment_method, (ViewGroup) getView(), false);
        ButterKnife.bind(this, root);


        builder.setView(root).setTitle("Set payments")
                .setPositiveButton("Set", null).
                setNegativeButton("Cancel", null);

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (PaymentDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(requireActivity().toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        Bundle bundle = getArguments();
        assert bundle != null;

        Purchase purchase = (Purchase) bundle.getSerializable(PurchasesFragment.PURCHASE);

        assert purchase != null;

        if (bundle.getBoolean(PurchasesFragment.IS_UPDATE, false)) {
            cash_text.setText(String.valueOf(purchase.getCash()));
            debt_text.setText(String.valueOf(purchase.getDebt()));
            check_text.setText(String.valueOf(purchase.getCheck()));
        }

        double price = purchase.getCategory().getPrice() * purchase.getWeight();
        String header = "Client should pay " + price;
        this.header.setText(header);

        cash_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                double cash = 0;
                double check = 0;

                if (!cash_text.getText().toString().isEmpty())
                    cash = Double.parseDouble(cash_text.getText().toString());

                if (!check_text.getText().toString().isEmpty())
                    check = Double.parseDouble(check_text.getText().toString());


                double debt = price - cash - check;

                debt_text.setText(String.valueOf(debt));
            }
        });

        check_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                double check = 0;
                double cash = 0;

                if (!check_text.getText().toString().isEmpty())
                    check = Double.parseDouble(check_text.getText().toString());

                if (!cash_text.getText().toString().isEmpty())
                    cash = Double.parseDouble(cash_text.getText().toString());

                double debt = price - cash - check;

                debt_text.setText(String.valueOf(debt));

            }
        });

        AlertDialog alertDialog = (AlertDialog) getDialog();

        assert alertDialog != null;
        alertDialog.getButton(Dialog.BUTTON_POSITIVE)
                .setOnClickListener(view -> {
                    if (cash_text.getText().toString().isEmpty()
                            || debt_text.getText().toString().isEmpty()
                            || check_text.getText().toString().isEmpty())
                        Toast.makeText(getContext(), "Fields shouldn't be empty", Toast.LENGTH_LONG).show();
                    else {
                        listener.onPaymentDialogPositiveClick(PaymentMethod.this);
                        dismiss();
                    }
                });
        alertDialog.getButton(Dialog.BUTTON_NEGATIVE)
                .setOnClickListener(view -> dismiss());
    }
}