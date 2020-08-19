package com.clientsinfo.ui.clients;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ClientsFragment extends Fragment implements RecyclerItemClickListener, RecyclerViewSetter, FilterableItems, AddUpdateClient.AddUpdateClientListener {

    private List<Client> clients;
    private static RecyclerView.Adapter<ClientsAdapter.ClientHolder> adapter;
    private boolean update;
    private Client clientToBeUpdated;
    private int positionUpdate;
    private NavigationView navigationView;
    @BindView(R.id.clients_list)
    RecyclerView recyclerClientsList;


    public interface AddClientListener {
        void onClientAdded();
    }

    public interface UpdateClientListener {
        void onClientUpdate();
    }

    public interface DeleteClientListener {
        void onClientDeleted();
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        MainActivity mainActivity = (MainActivity) context;
        navigationView = mainActivity.getNavigationView();
        mainActivity.setFilterableItems(this);
        mainActivity.setCurrentFragment(this);
        mainActivity.clientsFragmentOptionsSetup();
        mainActivity.setRecyclerViewSetter(this);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_clients, container, false);

        ButterKnife.bind(this, root);
        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            update = false;
            addUpdateClientDialog();
        });

        setupRecyclerView();

        return root;
    }


    @Override
    public void setupRecyclerView() {
        clients = new ArrayList<>();
        adapter = new ClientsAdapter(clients, getContext());
        ((ClientsAdapter) adapter).setRecyclerItemClickListener(this);
        recyclerClientsList.setAdapter(adapter);
        recyclerClientsList.setHasFixedSize(true);
        recyclerClientsList.setLayoutManager(new LinearLayoutManager(getContext()));
        ParseManagement.initializeClientList(requireContext(), clients, (ClientsAdapter) adapter);
    }


    @Override
    public void onItemClickListener(int position) {
        update = true;
        positionUpdate = position;
        clientToBeUpdated = clients.get(position);
        addUpdateClientDialog();
    }

    private void addUpdateClientDialog() {
        DialogFragment addUpdateClient = new AddUpdateClient();
        FragmentManager fragmentManager = getParentFragmentManager();
        addUpdateClient.show(fragmentManager, "dialog");

    }

    @Override
    public void onDeleteClickListener(final int position) {

        new AlertDialog.Builder(getContext()).setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Delete Confirmation")
                .setMessage("Are you sure you want to delete this Client ?")
                .setPositiveButton(R.string.delete, (dialog, id) -> {


                    if (ParseManagement.clientHaveRelatedPurchases(requireContext(), clients.get(position).getPhoneNumber())) {
                        new AlertDialog.Builder(getContext()).setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Client can't be deleted")
                                .setMessage(clients.get(position).getName() + " still have related purchases")
                                .setNeutralButton("OK", null).show();
                    } else
                        removeClient(position);
                })
                .setNegativeButton(R.string.cancel, null).show();
    }

    @Override
    public void filterItems(String name) {
        ((ClientsAdapter) adapter).getFilter().filter(name);
    }

    @Override
    public void filterItems(String name, Date startDate, Date endDate) {
        Log.i("Info", "Nothing to be done her");
    }

    public boolean isUpdate() {
        return update;
    }

    public Client getClientToBeUpdated() {
        return clientToBeUpdated;
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {

        AddUpdateClient addUpdateClient = (AddUpdateClient) dialog;
        Client client = new Client(addUpdateClient.clientName.getText().toString(),
                addUpdateClient.phoneNumber.getText().toString(),
                addUpdateClient.addressDetails.getText().toString());

        if (!update)
            addClient(client);
        else
            updateClient(client);


        addUpdateClient.dismiss();

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    public void addClient(Client client) {

        ParseManagement.insertClient(requireContext(), client, getParentFragmentManager(), () -> {

            int position = addClientInAlphabeticOrder(client);
            Log.i("Info", "Update position is " + position);
            adapter.notifyItemInserted(position);
            ParseManagement.initializeClientsMenu(navigationView, clients);
        });
    }

    private int addClientInAlphabeticOrder(Client entry) {

        for (Client client : clients)
            if (client.compareTo(entry) > 0) {
                clients.add(clients.indexOf(client), entry);
                return clients.indexOf(entry);
            }

        clients.add(entry);
        return clients.indexOf(entry);
    }

    public void updateClient(Client client) {
        client.setOldPhoneNumber(clients.get(positionUpdate).getPhoneNumber());
        boolean newPhoneNumber = false;
        if (!client.getOldPhoneNumber().equals(client.getPhoneNumber())) {
            newPhoneNumber = true;
        }
        ParseManagement.updateClient(requireContext(), client, newPhoneNumber, getParentFragmentManager(), () -> {
            clients.set(positionUpdate, client);
            adapter.notifyItemChanged(positionUpdate);
            ParseManagement.initializeClientsMenu(navigationView, clients);
        });
    }

    public void removeClient(int position) {
        LoadingDialog loadingDialog = new LoadingDialog("Deleting Client ...");
        loadingDialog.show(getParentFragmentManager(), "dialog");
        loadingDialog.setCancelable(false);

        ParseManagement.removeClient(requireContext(), clients.get(position).getPhoneNumber(), () -> {
            loadingDialog.dismiss();
            clients.remove(position);
            adapter.notifyItemRemoved(position);
            ParseManagement.initializeClientsMenu(navigationView, clients);
        });
    }

}