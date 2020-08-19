package com.clientsinfo.ui.clients;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.clientsinfo.R;
import com.clientsinfo.parsemanagement.ParseManagement;
import com.clientsinfo.ui.purchases.AddUpdatePurchase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ClientsDialog extends DialogFragment implements CreateClient.CreateClientListener {



    public interface ClientSetListener {
        void onClientSelected(DialogFragment dialog);
    }

    @BindView(R.id.clients_list)
    ListView clientsList;

    private List<Client> clients;
    private ClientSetListener listener;
    private ArrayList<String> clientsName;
    private Client selectedClient;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        AddUpdatePurchase addUpdatePurchase = (AddUpdatePurchase) context;
        listener = addUpdatePurchase;
        addUpdatePurchase.setCurrentFragment(this);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        final View root = inflater.inflate(R.layout.clients_dialog, (ViewGroup) getView(), false);
        ButterKnife.bind(this, root);

        builder.setView(root).setTitle("Clients");

        clientsName = new ArrayList<>();
        clients = new ArrayList<>();

        ClientsAdapter clientsAdapter = new ClientsAdapter(getContext(), clientsName);
        ParseManagement.initializeClientsDialog(requireContext(), clientsList, clients, clientsName, clientsAdapter);

        return builder.create();
    }

    @Override
    public void onClientAdded(DialogFragment dialog) {
        CreateClient createClient = (CreateClient) dialog;

        Client client = new Client(createClient.clientName.getText().toString(),
                createClient.phoneNumber.getText().toString(),
                createClient.addressDetails.getText().toString());

        ParseManagement.insertClient(requireContext(), client, getParentFragmentManager(), () -> {
            clients.add(client);
            clientsName.add(clientsName.size() - 1, client.getName());
            clientsList.setAdapter(new ClientsAdapter(getContext(), clientsName));
            createClient.dismiss();
        });
    }


    public class ClientsAdapter extends ArrayAdapter<String> {
        public ClientsAdapter(Context context, ArrayList<String> clients) {
            super(context, 0, clients);
        }

        @NotNull
        @Override
        public View getView(int position, View convertView, @NotNull ViewGroup parent) {
            String name = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.text_view_client_layout, parent, false);
            }
            TextView clientName = convertView.findViewById(R.id.client_name);
            clientName.setText(name);
            if (name != null) {
                if (name.equals("+")) {
                    clientName.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    clientName.setOnClickListener(view -> {
                        DialogFragment createClient = new CreateClient();
                        FragmentManager fragmentManager = getParentFragmentManager();
                        createClient.show(fragmentManager, "dialog");

                    });
                } else {
                    clientName.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                    clientName.setOnClickListener(view -> {
                        selectedClient = clients.get(position);
                        listener.onClientSelected(ClientsDialog.this);
                        dismiss();
                    });

                }
            }

            return convertView;
        }

    }


    public Client getSelectedClient() {
        return selectedClient;
    }
}
