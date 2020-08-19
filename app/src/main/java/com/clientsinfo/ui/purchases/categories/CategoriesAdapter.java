package com.clientsinfo.ui.purchases.categories;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;
import com.clientsinfo.R;
import com.clientsinfo.recyclerviewinterfaces.RecyclerItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryHolder> {

    private RecyclerItemClickListener recyclerItemClickListener;
    private List<Category> list;

    public CategoriesAdapter(List<Category> list) {
        this.list = list;
    }

    public void setRecyclerItemClickListener(RecyclerItemClickListener recyclerItemClickListener) {
        this.recyclerItemClickListener = recyclerItemClickListener;
    }

    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_category_layout, parent, false);
        return new CategoryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryHolder holder, int position) {

        Category category = list.get(position);
        holder.categoryName.setText(category.getName());
        holder.price.setText(String.valueOf(category.getPrice()));
        holder.categoryColor.setBackgroundColor(category.getColor());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class CategoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.category_name)
        TextView categoryName;

        @BindView(R.id.price)
        TextView price;

        @BindView(R.id.category)
        TextView categoryColor;

        @BindView(R.id.main_info)
        LinearLayout mainInfo;

        @BindView(R.id.delete)
        LinearLayout delete;

        @BindView(R.id.swipe_layout)
        SwipeLayout swipeLayout;

        public CategoryHolder(@NonNull View itemView) {
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
