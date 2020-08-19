package com.clientsinfo.ui.accounting;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.clientsinfo.LoadingDialog;
import com.clientsinfo.MainActivity;
import com.clientsinfo.R;
import com.clientsinfo.parsemanagement.ParseManagement;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AccountingFragment extends Fragment {

    @BindView(R.id.total_sales)
    TextView totalSales;

    @BindView(R.id.total_paid)
    TextView totalPaid;

    @BindView(R.id.total_cash)
    TextView totalCash;

    @BindView(R.id.total_quantity)
    TextView totalQuantity;

    @BindView(R.id.total_debt)
    TextView totalDebt;

    public interface AccountingListener {
        void onCalculateListener(double sales, double paid, double weight, double debt);
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        MainActivity mainActivity = (MainActivity) context;
        mainActivity.accountingFragmentOptionsSetup();
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_accounting, container, false);

        ButterKnife.bind(this, root);

        tableSetup();

        return root;
    }


    public void tableSetup() {

        LoadingDialog loadingDialog = new LoadingDialog("Accounting setup");
        loadingDialog.show(getParentFragmentManager(), "dialog");
        loadingDialog.setCancelable(false);

        ParseManagement.accountingTableSetup(requireContext(), (sales, paid, weight, debt) -> {
            loadingDialog.dismiss();
            double cash = sales - paid;
            totalSales.setText(String.valueOf(sales));
            totalPaid.setText(String.valueOf(paid));
            totalCash.setText(String.valueOf(cash));
            totalQuantity.setText(String.valueOf(weight));
            totalDebt.setText(String.valueOf(debt));
        });

    }


}