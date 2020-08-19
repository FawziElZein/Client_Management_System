package com.clientsinfo.ui.clients;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;
import com.clientsinfo.R;
import com.clientsinfo.recyclerviewinterfaces.RecyclerItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ClientsAdapter extends RecyclerView.Adapter<ClientsAdapter.ClientHolder> implements Filterable {

    private RecyclerItemClickListener recyclerItemClickListener;
    private List<Client> list;
    private Context context;

    public void setRecyclerItemClickListener(RecyclerItemClickListener recyclerItemClickListener) {
        this.recyclerItemClickListener = recyclerItemClickListener;
    }

    public ClientsAdapter(List<Client> list, Context context) {
        this.context = context;
        this.list = list;
    }


    @NonNull
    @Override
    public ClientHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.activity_client_layout, parent, false);
        return new ClientHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClientHolder holder, int position) {
        Client client = list.get(position);
        holder.clientName.setText(client.getName());
        holder.phoneNumber.setText(client.getPhoneNumber());
        holder.addressDetails.setText(client.getAddress());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Filter getFilter() {
        return new ClientFilter(this, this.list, this.context);
    }


    public class ClientHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.client_name)
        TextView clientName;

        @BindView(R.id.phone_number)
        TextView phoneNumber;

        @BindView(R.id.address_details)
        TextView addressDetails;

        @BindView(R.id.swipe_layout)
        SwipeLayout swipeLayout;

        @BindView(R.id.main_info)
        ConstraintLayout mainInfo;

        @BindView(R.id.delete)
        LinearLayout delete;

        public ClientHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
            mainInfo.setOnClickListener(this);
            delete.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            if (recyclerItemClickListener != null)
                if (view.getId() == R.id.main_info)
                    recyclerItemClickListener.onItemClickListener(getAdapterPosition());
                else if (view.getId() == R.id.delete)
                    recyclerItemClickListener.onDeleteClickListener(getAdapterPosition());

        }
    }
}
