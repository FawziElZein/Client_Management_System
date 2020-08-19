package com.clientsinfo.ui.purchases;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.clientsinfo.LoadingDialog;
import com.clientsinfo.MainActivity;
import com.clientsinfo.R;
import com.clientsinfo.parsemanagement.ParseManagement;
import com.clientsinfo.recyclerviewinterfaces.FilterableItems;
import com.clientsinfo.recyclerviewinterfaces.RecyclerItemClickListener;
import com.clientsinfo.recyclerviewinterfaces.RecyclerViewSetter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PurchasesFragment extends Fragment implements Serializable, RecyclerItemClickListener, FilterableItems, RecyclerViewSetter {

    private static final int CREATE = 0;
    private static final int UPDATE = 1;
    public static final String IS_UPDATE = "IS_UPDATE";
    public static final String PURCHASE = "PURCHASE";

    private NavigationView navigationView;
    private int positionUpdate;

    @BindView(R.id.purchases_list)
    RecyclerView recyclerPurchasesList;

    private List<Purchase> purchases;
    private static RecyclerView.Adapter<PurchasesAdapter.PurchaseHolder> adapter;


    public interface AddPurchaseListener {
        void onPurchaseAdded(String id);
    }

    public interface UpdatePurchaseListener {
        void onPurchaseUpdated();
    }

    public interface DeletePurchaseListener {
        void onPurchaseDeleted();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        MainActivity mainActivity = (MainActivity) context;
        mainActivity.setFilterableItems(this);
        mainActivity.setCurrentFragment(this);
        mainActivity.purchasesFragmentOptionsSetup();
        navigationView = mainActivity.getNavigationView();
        mainActivity.setRecyclerViewSetter(this);

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        View root = inflater.inflate(R.layout.fragment_purchases, container, false);
        ButterKnife.bind(this, root);
        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(view -> addUpdatePurchaseIntent(false));

        setupRecyclerView();

        return root;
    }


    public void setupRecyclerView() {

        Log.i("Info", "Setup recycler view In Purchase Fragment");
        purchases = new ArrayList<>();
        adapter = new PurchasesAdapter(purchases, getContext());
        ((PurchasesAdapter) adapter).setRecyclerItemClickListener(this);
        recyclerPurchasesList.setAdapter(adapter);
        recyclerPurchasesList.setHasFixedSize(true);
        recyclerPurchasesList.setLayoutManager(new LinearLayoutManager(getContext()));
        ParseManagement.initializePurchaseList(requireContext(), purchases, (PurchasesAdapter) adapter);

    }

    @Override
    public void onItemClickListener(int position) {
        positionUpdate = position;
        addUpdatePurchaseIntent(true);
    }

    @Override
    public void onDeleteClickListener(final int position) {

        new AlertDialog.Builder(getContext()).setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Delete Confirmation")
                .setMessage("Are you sure you want to delete this purchase ?")
                .setPositiveButton(R.string.delete, (dialog, id) -> removePurchase(position))
                .setNegativeButton(R.string.cancel, null).show();


    }

    public void addUpdatePurchaseIntent(boolean isUpdate) {
        Intent intent = new Intent(getContext(), AddUpdatePurchase.class);

        if (!isUpdate) {
            startActivityForResult(intent, CREATE);
        } else {
            intent.putExtra(PURCHASE, purchases.get(positionUpdate));
            intent.putExtra(IS_UPDATE, true);
            startActivityForResult(intent, UPDATE);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            Purchase purchase = null;
            if (data != null)
                purchase = (Purchase) data.getSerializableExtra(PURCHASE);
            if (requestCode == CREATE)
                addPurchase(purchase);
            else if (requestCode == UPDATE)
                updatePurchase(purchase);
        }

        ParseManagement.initializeClientsMenu(navigationView);

    }

    public void addPurchase(Purchase purchase) {
        LoadingDialog loadingDialog = new LoadingDialog("Creating Purchase ...");
        loadingDialog.show(getParentFragmentManager(), "dialog");
        loadingDialog.setCancelable(false);

        ParseManagement.insertPurchase(requireContext(), purchase, (id) -> {
            loadingDialog.dismiss();
            purchase.setId(id);
            purchases.add(purchase);
            adapter.notifyItemInserted(purchases.size() - 1);
        });
    }


    public void updatePurchase(Purchase purchase) {
        LoadingDialog loadingDialog = new LoadingDialog("Updating purchase ...");
        loadingDialog.show(getParentFragmentManager(), "dialog");
        loadingDialog.setCancelable(false);
        ParseManagement.updatePurchase(requireContext(), purchase, () -> {
            loadingDialog.dismiss();
            purchases.set(positionUpdate, purchase);
            adapter.notifyItemChanged(positionUpdate);
        });
    }

    public void removePurchase(int position) {
        LoadingDialog loadingDialog = new LoadingDialog("Deleting Purchase ...");
        loadingDialog.show(getParentFragmentManager(), "dialog");
        loadingDialog.setCancelable(false);

        ParseManagement.removePurchase(requireContext(), purchases.get(position).getId(), () -> {
            purchases.remove(position);
            adapter.notifyItemRemoved(position);
            loadingDialog.dismiss();
        });
    }


    @Override
    public void filterItems(String name) {
        ((PurchasesAdapter) adapter).getFilter().filter(name);
    }

    @Override
    public void filterItems(String name, Date startDate, Date endDate) {
        ((PurchasesAdapter) adapter).getFilter().setStartDate(startDate).setEndDate(endDate).filter(name);
    }

}