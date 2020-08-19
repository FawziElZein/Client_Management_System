package com.clientsinfo.ui.purchases;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Filter;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.clientsinfo.MainActivity;
import com.clientsinfo.parsemanagement.ParseManagement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PurchaseFilter extends Filter {

    private List<Purchase> list;
    private Context context;
    private RecyclerView.Adapter<PurchasesAdapter.PurchaseHolder> adapter;
    private Date startDate;
    private Date endDate;

    public PurchaseFilter(RecyclerView.Adapter<PurchasesAdapter.PurchaseHolder> adapter, List<Purchase> list, Context context) {
        this.adapter = adapter;
        this.list = list;
        this.context = context;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {

        List<Purchase> filteredPurchases = new ArrayList<>();
        List<Purchase> fullList = ParseManagement.initializePurchaseFilterList(context);

        if (constraint != null && constraint.length() != 0) {

            for (Purchase purchase : fullList) {

                if (startDate == null || endDate == null) {
                    if (purchase.getClient().getName().contentEquals(constraint))
                        filteredPurchases.add(purchase);
                } else {
                    if (constraint.equals(MainActivity.SHOW_ALL)) {

                        if (purchase.getDate().before(endDate) && purchase.getDate().after(startDate))
                            filteredPurchases.add(purchase);
                    } else if (purchase.getClient().getName().contentEquals(constraint) && purchase.getDate().before(endDate) && purchase.getDate().after(startDate))
                        filteredPurchases.add(purchase);
                }
            }
            if (filteredPurchases.isEmpty()) {
                filteredPurchases.addAll(fullList);
                ((MainActivity) context).setSelectedClient(MainActivity.SHOW_ALL);
                Handler mainThread = new Handler(Looper.getMainLooper());
                mainThread.post(() -> Toast.makeText(context, "No purchases found", Toast.LENGTH_LONG).show());
            }
        } else {
            filteredPurchases.addAll(fullList);
        }

        FilterResults results = new FilterResults();
        results.values = filteredPurchases;
        return results;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        list.clear();
        list.addAll((ArrayList<Purchase>) filterResults.values);
        adapter.notifyDataSetChanged();
    }

    public PurchaseFilter setStartDate(Date startDate) {
        this.startDate = startDate;
        return this;
    }

    public PurchaseFilter setEndDate(Date endDate) {
        this.endDate = endDate;
        return this;
    }
}
