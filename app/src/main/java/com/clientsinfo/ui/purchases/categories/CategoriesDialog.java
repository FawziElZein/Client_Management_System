package com.clientsinfo.ui.purchases.categories;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.clientsinfo.R;
import com.clientsinfo.recyclerviewinterfaces.RecyclerItemClickListener;
import com.clientsinfo.parsemanagement.ParseManagement;
import com.clientsinfo.ui.purchases.AddUpdatePurchase;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoriesDialog extends DialogFragment implements RecyclerItemClickListener, AddCategory.AddCategoryDialogListener {

    private static RecyclerView.Adapter<CategoriesAdapter.CategoryHolder> adapter;
    private List<Category> categories;
    //    private ParseManagement parseManagement;
    private Category selectedCategory;
    private CategorySetListener listener;

    @BindView(R.id.categories_list)
    RecyclerView recyclerCategoriesList;


    public interface CategorySetListener {
        void onCategorySelected(DialogFragment dialog);
    }

    public interface AddCategoryListener {
        void onCategoryAdded();

    }

    public interface DeleteCategoryListener {
        void onCategoryDeleted();

    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.i("Info", "============================================");
        Log.i("Info", context.getClass().toString());
        Log.i("Info", "============================================");

        AddUpdatePurchase addUpdatePurchase = (AddUpdatePurchase) context;
        listener = addUpdatePurchase;
        addUpdatePurchase.setCurrentFragment(this);

    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        final View root = inflater.inflate(R.layout.categories_dialog, (ViewGroup) getView(), false);
        ButterKnife.bind(this, root);

        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(view -> addUpdateCategoryDialog());


        builder.setView(root).setTitle("Categories")
                .setNegativeButton("Cancel", null);

        setupRecyclerView();

        return builder.create();
    }

    public void setupRecyclerView() {
        categories = new ArrayList<>();
        adapter = new CategoriesAdapter(categories);
        ((CategoriesAdapter) adapter).setRecyclerItemClickListener(this);
        recyclerCategoriesList.setAdapter(adapter);
        recyclerCategoriesList.setHasFixedSize(true);
        recyclerCategoriesList.setLayoutManager(new LinearLayoutManager(getContext()));
        ParseManagement.initializeCategoryList(requireContext(), categories, (CategoriesAdapter) adapter);
    }


    private void addUpdateCategoryDialog() {
        DialogFragment addUpdateCategory = new AddCategory();
        FragmentManager fragmentManager = getParentFragmentManager();
        addUpdateCategory.show(fragmentManager, "dialog");
    }


    @Override
    public void onItemClickListener(int position) {
        selectedCategory = categories.get(position);
        listener.onCategorySelected(this);
        dismiss();
    }

    @Override
    public void onDeleteClickListener(int position) {
        new AlertDialog.Builder(getContext()).setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Delete Confirmation")
                .setMessage("Are you sure you want to delete this Client ?")
                .setPositiveButton(R.string.delete, (dialog, id) -> {
                    if (ParseManagement.categoryHaveRelatedPurchases(requireContext(), categories.get(position).getName())) {
                        new AlertDialog.Builder(getContext()).setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Category can't be deleted")
                                .setMessage(categories.get(position).getName() + " still have related purchases")
                                .setNeutralButton("OK", null)
                                .show();
                    } else
                        CategoriesDialog.this.removeCategory(position);
                })
                .setNegativeButton(R.string.cancel, null).show();
    }

    @Override
    public void onAddCategoryDialogPositiveClick(DialogFragment dialog) {
        AddCategory addCategory = (AddCategory) dialog;
        Category category = new Category(addCategory.categoryName.getText().toString(),
                Integer.parseInt(addCategory.price.getText().toString()),
                addCategory.colorPicker.getCurrentHintTextColor());
        addCategory(category);

    }

    @Override
    public void onAddCategoryDialogNegativeClick(DialogFragment dialog) {

    }

    private void addCategory(Category category) {
        ParseManagement.insertCategory(requireContext(), category, getParentFragmentManager(), () -> {
            categories.add(category);
            adapter.notifyItemInserted(categories.size() - 1);
        });
    }


    public void removeCategory(int position) {
        ParseManagement.removeCategory(requireContext(), categories.get(position).getName(), () -> {
            categories.remove(position);
            adapter.notifyItemRemoved(position);
        });
    }

    public Category getSelectedCategory() {
        return selectedCategory;
    }
}
