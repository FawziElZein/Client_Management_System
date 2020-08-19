package com.clientsinfo.ui.purchases.categories;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.clientsinfo.R;
import com.clientsinfo.ui.purchases.AddUpdatePurchase;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddCategory extends DialogFragment {


    public interface AddCategoryDialogListener {
        void onAddCategoryDialogPositiveClick(DialogFragment dialog);

        void onAddCategoryDialogNegativeClick(DialogFragment dialog);
    }

    @BindView(R.id.category_name)
    TextView categoryName;

    @BindView(R.id.price)
    TextView price;

    @BindView(R.id.color_pick)
    TextView colorPicker;

    @BindView(R.id.palette)
    ImageView palette;

    private AddCategoryDialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        AddUpdatePurchase addUpdatePurchase = (AddUpdatePurchase) context;
        listener = (CategoriesDialog) addUpdatePurchase.getCurrentFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        final View root = inflater.inflate(R.layout.activity_add_update_category, (ViewGroup) getView(), false);
        ButterKnife.bind(this, root);

        builder.setView(root).setTitle("Add category")
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
                    if (categoryName.getText().toString().isEmpty()
                            || price.getText().toString().isEmpty())
                        Toast.makeText(getContext(), "Fields shouldn't be empty", Toast.LENGTH_LONG).show();
                    else {
                        listener.onAddCategoryDialogPositiveClick(AddCategory.this);
                        dismiss();
                    }
                });
        alertDialog.getButton(Dialog.BUTTON_NEGATIVE)
                .setOnClickListener(view -> {
                    listener.onAddCategoryDialogNegativeClick(AddCategory.this);
                    dismiss();
                });

        colorPicker.setOnClickListener(view -> {
            ColorPickerDialog.Builder builder =
                    new ColorPickerDialog.Builder(getContext(), R.style.DarkDialog)
                            .setTitle("ColorPicker Dialog")
                            .setPreferenceName("Test")
                            .setPositiveButton(
                                    getString(R.string.select),
                                    (ColorEnvelopeListener) (envelope, fromUser) -> setTextColor(envelope)
                            ).setNegativeButton(
                            getString(R.string.cancel),
                            (dialogInterface, i) -> dialogInterface.dismiss());
            builder.getColorPickerView().setFlagView(new BubbleFlag(getContext(), R.layout.layout_flag));
            builder.show();
        });
    }

    @SuppressLint("SetTextI18n")
    private void setTextColor(ColorEnvelope envelope) {
        palette.setColorFilter(envelope.getColor());
        colorPicker.setHintTextColor(envelope.getColor());
    }
}
