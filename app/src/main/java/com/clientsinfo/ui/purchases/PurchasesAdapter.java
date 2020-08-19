package com.clientsinfo.ui.purchases;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;
import com.clientsinfo.R;
import com.clientsinfo.recyclerviewinterfaces.RecyclerItemClickListener;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PurchasesAdapter extends RecyclerView.Adapter<PurchasesAdapter.PurchaseHolder> implements Filterable {


    private RecyclerItemClickListener recyclerItemClickListener;
    private List<Purchase> list;
    private Context context;
    private final SimpleDateFormat dayMonthFormatter = new SimpleDateFormat("dd/MM", Locale.US);
    private final SimpleDateFormat yearFormatter = new SimpleDateFormat("yyyy", Locale.US);


    public void setRecyclerItemClickListener(RecyclerItemClickListener recyclerItemClickListener) {
        this.recyclerItemClickListener = recyclerItemClickListener;
    }

    public PurchasesAdapter(List<Purchase> list, Context context) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public PurchaseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.activity_purchase_layout, parent, false);
        return new PurchaseHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull PurchaseHolder holder, int position) {

        Purchase purchase = list.get(position);


        holder.categoryColor.setBackgroundColor(purchase.getCategory().getColor());

        holder.name.setText(purchase.getClient().getName());
        holder.dayMonth.setText(dayMonthFormatter.format(purchase.getDate()));
        holder.year.setText(yearFormatter.format(purchase.getDate()));
        holder.type.setText(purchase.getCategory().getName());
        holder.weight.setText(String.format("(%s Kg )", purchase.getWeight()));
        holder.cash.setText(String.valueOf(list.get(position).getCash()));
        holder.debt.setText(String.valueOf(list.get(position).getDebt()));
        holder.check.setText(String.valueOf(list.get(position).getCheck()));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public PurchaseFilter getFilter() {
        return new PurchaseFilter(this, this.list, this.context);
    }

    public class PurchaseHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        @BindView(R.id.category)
        TextView categoryColor;

        @BindView(R.id.client_name)
        TextView name;

        @BindView(R.id.dayMonth)
        TextView dayMonth;

        @BindView(R.id.year)
        TextView year;

        @BindView(R.id.type)
        TextView type;

        @BindView(R.id.weight)
        TextView weight;

        @BindView(R.id.cash)
        TextView cash;

        @BindView(R.id.debt)
        TextView debt;

        @BindView(R.id.check)
        TextView check;

        @BindView(R.id.swipe_layout)
        SwipeLayout swipeLayout;

        @BindView(R.id.main_info)
        LinearLayout mainInfo;

        @BindView(R.id.delete)
        LinearLayout delete;


        public PurchaseHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
            mainInfo.setOnClickListener(this);
            delete.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            if (recyclerItemClickListener != null) {
                if (view.getId() == R.id.main_info)
                    recyclerItemClickListener.onItemClickListener(getAdapterPosition());
                if (view.getId() == R.id.delete)
                    recyclerItemClickListener.onDeleteClickListener(getAdapterPosition());
            }

        }
    }
}
