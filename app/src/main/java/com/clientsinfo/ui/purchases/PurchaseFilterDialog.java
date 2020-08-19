package com.clientsinfo.ui.purchases;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.clientsinfo.MainActivity;
import com.clientsinfo.R;
import com.clientsinfo.recyclerviewinterfaces.FilterableItems;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PurchaseFilterDialog extends DialogFragment {


    private FilterableItems filterableItems;

    private final DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

    @BindView(R.id.start_date)
    TextView startDate;

    @BindView(R.id.end_date)
    TextView endDate;

    Context context;

    private String name;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        final View root = inflater.inflate(R.layout.filter_layout, (ViewGroup) getView(), false);

        context = root.getContext();

        ButterKnife.bind(this, root);

        builder.setView(root).setPositiveButton("Filter", null)
                .setNegativeButton("Cancel", (dialogInterface, i) -> {

                });

        startDate.setOnClickListener(view -> datePicker(startDate));

        endDate.setOnClickListener(view -> datePicker(endDate));
        return builder.create();

    }

    public void datePicker(final TextView dateText) {

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(context, (view, year, monthOfYear, dayOfMonth) -> {
            String date;

            if (dayOfMonth < 10)
                date = "0" + dayOfMonth;
            else
                date = String.valueOf(dayOfMonth);

            monthOfYear = monthOfYear + 1;

            if (monthOfYear < 10)
                date = date + "-0" + monthOfYear;
            else
                date = date + "-" + monthOfYear;

            date = date + "-" + year;

            dateText.setText(date);
        }, mYear, mMonth, mDay).show();

    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            filterableItems = (MainActivity) context;
            name = ((MainActivity) context).getSelectedClient();
        } catch (ClassCastException e) {
            throw new ClassCastException(requireActivity().toString()
                    + " must implement FilterPurchase");
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        AlertDialog alertDialog = (AlertDialog) getDialog();

        assert alertDialog != null;
        alertDialog.getButton(Dialog.BUTTON_POSITIVE)
                .setOnClickListener(view -> {
                    String startDate = PurchaseFilterDialog.this.startDate.getText().toString();
                    String endDate = PurchaseFilterDialog.this.endDate.getText().toString();

                    if (!startDate.isEmpty() && !endDate.isEmpty()) {
                        try {
                            Date startLocalDate = formatter.parse(startDate);
                            Date endLocalDate = formatter.parse(endDate);
                            filterableItems.filterItems(name, startLocalDate, endLocalDate);
                            dismiss();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(context, "Date fields can't be empty", Toast.LENGTH_LONG).show();
                    }
                });

    }
}
