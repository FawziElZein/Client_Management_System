package com.clientsinfo.ui.purchases;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.clientsinfo.R;
import com.clientsinfo.parsemanagement.ParseManagement;
import com.clientsinfo.ui.clients.Client;
import com.clientsinfo.ui.clients.ClientsDialog;
import com.clientsinfo.ui.purchases.categories.CategoriesDialog;
import com.clientsinfo.ui.purchases.categories.Category;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddUpdatePurchase extends AppCompatActivity implements PaymentMethod.PaymentDialogListener
        , CategoriesDialog.CategorySetListener, ClientsDialog.ClientSetListener {


    @BindView(R.id.client_name)
    TextView clientName;

    @BindView(R.id.date)
    TextView date;

    @BindView(R.id.category_name)
    TextView categoryName;

    @BindView(R.id.weight)
    TextView weight;

    @BindView(R.id.cash)
    TextView cash;

    @BindView(R.id.debt)
    TextView debt;

    @BindView(R.id.check)
    TextView check;

    @BindView(R.id.outlay)
    TextView outlay;

    @BindView(R.id.note)
    TextView note;

    @BindView(R.id.add_edit_btn)
    Button addEditBtn;

    @BindView(R.id.category_icon)
    ImageView categoryIcon;

    private DateFormat dfOutput = new SimpleDateFormat("MMM dd, yyyy, hh:mm a", Locale.US);
    private DateFormat dfInput = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.US);

    private boolean isUpdate;
    private Purchase purchase;
    private Fragment currentFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_update_purchase);
        ButterKnife.bind(this);

        this.isUpdate = getIntent().getBooleanExtra(PurchasesFragment.IS_UPDATE, false);

        weight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (weight.getText().toString().isEmpty())
                    purchase.setWeight(0);
                else
                    purchase.setWeight(Double.parseDouble(weight.getText().toString()));
            }
        });

        layoutSetup(isUpdate);

    }

    private void layoutSetup(boolean isUpdate) {

        if (isUpdate) {

            purchase = (Purchase) getIntent().getSerializableExtra(PurchasesFragment.PURCHASE);

            assert purchase != null;
            clientName.setText(purchase.getClient().getName());
            date.setText(dfOutput.format(purchase.getDate()));
            categoryName.setText(purchase.getCategory().getName());
            weight.setText(String.valueOf(purchase.getWeight()));
            cash.setText(String.valueOf(purchase.getCash()));
            debt.setText(String.valueOf(purchase.getDebt()));
            check.setText(String.valueOf(purchase.getCheck()));
            outlay.setText(String.valueOf(purchase.getOutlay()));
            note.setText(purchase.getNote());
            addEditBtn.setText(R.string.edit);
        } else {
            ParseManagement.initializePurchase();
            purchase = new Purchase();
        }

    }

    public Fragment getCurrentFragment() {
        return currentFragment;
    }

    public void setCurrentFragment(Fragment currentFragment) {
        this.currentFragment = currentFragment;
    }


    public void clientPick(View view) {
        new ClientsDialog().show(getSupportFragmentManager(), "dialog");
    }

    public void datePick(View view) {

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        final int mHour = c.get(Calendar.HOUR_OF_DAY);
        final int mMinute = c.get(Calendar.MINUTE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view1, year, monthOfYear, dayOfMonth) -> {
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


            final String finalDate = date;
            new TimePickerDialog(AddUpdatePurchase.this, (view11, hourOfDay, minute) -> {

                String dateTime = finalDate;

                if (hourOfDay < 10)
                    dateTime = dateTime + " 0" + hourOfDay;
                else
                    dateTime = dateTime + " " + hourOfDay;

                if (minute < 10)
                    dateTime = dateTime + ":0" + minute;
                else
                    dateTime = dateTime + ":" + minute;

                try {
                    dfInput.setTimeZone(TimeZone.getTimeZone("GMT+3"));
                    Date purchaseDate = dfInput.parse(dateTime);
                    assert purchaseDate != null;
                    Log.i("Info purchase date ", purchaseDate.toString());
                    this.date.setText(dfOutput.format(purchaseDate));
                    purchase.setDate(purchaseDate);

                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }, mHour, mMinute, false).show();


        }, mYear, mMonth, mDay);

        datePickerDialog.show();
    }


    public void createUpdatePurchase(View view) {

        String clientName = this.clientName.getText().toString();
        String date = this.date.getText().toString();
        String category = this.categoryName.getText().toString();
        String weight = this.weight.getText().toString();
        String cash = this.cash.getText().toString();
        String debt = this.debt.getText().toString();
        String check = this.check.getText().toString();
        String outlay = this.outlay.getText().toString();
        String note = this.note.getText().toString();

        if (clientName.isEmpty() || date.isEmpty() || category.isEmpty() || weight.isEmpty()
                || cash.isEmpty() || debt.isEmpty() || check.isEmpty() || outlay.isEmpty()) {
            Toast.makeText(this, "Field's shouldn't be empty", Toast.LENGTH_LONG).show();
        } else {

            ///
            Intent intent = new Intent();
            purchase.setOutlay(Double.parseDouble(outlay));
            purchase.setNote(note);
            purchase.setWeight(Double.parseDouble(weight));

            intent.putExtra(PurchasesFragment.PURCHASE, purchase);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }


    }

    public void categoryPick(View view) {
        new CategoriesDialog().show(getSupportFragmentManager(), "dialog");
    }

    public void setPayment(View view) {
        if (categoryName.getText().toString().isEmpty() ||
                weight.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please pick a category and/or set a weight", Toast.LENGTH_LONG).show();
        } else {
            DialogFragment paymentMethodDialog = new PaymentMethod();
            Bundle bundle = new Bundle();
            bundle.putSerializable(PurchasesFragment.PURCHASE, purchase);

            if (isUpdate)
                bundle.putBoolean(PurchasesFragment.IS_UPDATE, true);

            paymentMethodDialog.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            paymentMethodDialog.show(fragmentManager, "dialog");
        }
    }

    @Override
    public void onPaymentDialogPositiveClick(DialogFragment dialog) {
        PaymentMethod paymentMethodDialog = (PaymentMethod) dialog;
        cash.setText(paymentMethodDialog.cash_text.getText());
        debt.setText(paymentMethodDialog.debt_text.getText());
        check.setText(paymentMethodDialog.check_text.getText());
        purchase.setCash(Double.parseDouble(cash.getText().toString()));
        purchase.setDebt(Double.parseDouble(debt.getText().toString()));
        purchase.setCheck(Double.parseDouble(check.getText().toString()));
    }

    @Override
    public void onCategorySelected(DialogFragment dialog) {
        CategoriesDialog categoriesDialog = (CategoriesDialog) dialog;
        this.categoryName.setText(categoriesDialog.getSelectedCategory().getName());
        this.categoryIcon.setColorFilter(categoriesDialog.getSelectedCategory().getColor());
        Category category = categoriesDialog.getSelectedCategory();
        purchase.setCategory(category);
    }


    @Override
    public void onClientSelected(DialogFragment dialog) {
        ClientsDialog clientsDialog = (ClientsDialog) dialog;
        Client client = clientsDialog.getSelectedClient();
        clientName.setText(client.getName());
        purchase.setClient(client);
    }
}