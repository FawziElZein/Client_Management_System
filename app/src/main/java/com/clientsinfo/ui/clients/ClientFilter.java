package com.clientsinfo.ui.clients;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Filter;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.clientsinfo.MainActivity;
import com.clientsinfo.parsemanagement.ParseManagement;

import java.util.ArrayList;
import java.util.List;

public class ClientFilter extends Filter {
    private List<Client> list;
    private Context context;
    private RecyclerView.Adapter<ClientsAdapter.ClientHolder> adapter;

    public ClientFilter(RecyclerView.Adapter<ClientsAdapter.ClientHolder> adapter, List<Client> list, Context context) {
        this.adapter = adapter;
        this.list = list;
        this.context = context;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        List<Client> filteredClients = new ArrayList<>();
        List<Client> fullList = ParseManagement.initializeClientFilterList(context);
        if (constraint != null && constraint.length() != 0) {
            for (Client client : fullList)
                if (client.getName().contentEquals(constraint))
                    filteredClients.add(client);

            if (filteredClients.isEmpty()) {
                ((MainActivity) context).setSelectedClient(MainActivity.SHOW_ALL);
                filteredClients.addAll(fullList);
                Handler mainThread = new Handler(Looper.getMainLooper());
                mainThread.post(() -> Toast.makeText(context, "No clients found", Toast.LENGTH_LONG).show());
            }
        } else {
            filteredClients.addAll(fullList);
        }

        FilterResults results = new FilterResults();
        results.values = filteredClients;
        return results;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        list.clear();
        list.addAll((ArrayList<Client>) filterResults.values);
        adapter.notifyDataSetChanged();
    }
}
