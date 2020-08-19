package com.clientsinfo.parsemanagement;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import com.clientsinfo.LoadingDialog;
import com.clientsinfo.MainActivity;
import com.clientsinfo.R;
import com.clientsinfo.recyclerviewinterfaces.RecyclerViewSetter;
import com.clientsinfo.ui.accounting.AccountingFragment;
import com.clientsinfo.ui.clients.Client;
import com.clientsinfo.ui.clients.ClientsAdapter;
import com.clientsinfo.ui.clients.ClientsFragment;
import com.clientsinfo.ui.purchases.Purchase;
import com.clientsinfo.ui.purchases.PurchasesAdapter;
import com.clientsinfo.ui.purchases.PurchasesFragment;
import com.clientsinfo.ui.purchases.categories.CategoriesAdapter;
import com.clientsinfo.ui.purchases.categories.CategoriesDialog;
import com.clientsinfo.ui.purchases.categories.Category;
import com.google.android.material.navigation.NavigationView;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class ParseManagement {

    private static ParseObject purchaseEntry;

    public static void initializePurchase() {
        ParseManagement.purchaseEntry = new ParseObject("Purchase");
    }

    public static void initializeLocalDataStore(Context context, FragmentManager fragmentManager, NavigationView navigationView,
                                                RecyclerViewSetter recyclerViewSetter) {

        LoadingDialog loadingDialog = new LoadingDialog("Loading data ...");
        loadingDialog.show(fragmentManager, "dialog");
        loadingDialog.setCancelable(false);

        AtomicBoolean isPurchasesPinned = new AtomicBoolean(false);
        AtomicBoolean isClientsPinned = new AtomicBoolean(false);
        AtomicBoolean isCategoriesPinned = new AtomicBoolean(false);
        AtomicBoolean noConnection = new AtomicBoolean(false);

        new Thread(() -> {

            Handler handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(@NotNull Message msg) {
                    loadingDialog.dismiss();

                    if (msg.what == 0) {
                        recyclerViewSetter.setupRecyclerView();
                        initializeClientsMenu(navigationView);

                    } else if (msg.what == 1)
                        new AlertDialog.Builder(context).setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Connection problem")
                                .setMessage("Failed to connect to parse server, make sure you have internet connection then try again")
                                .show();
                }
            };

            while (true) {
                if (isPurchasesPinned.get() && isClientsPinned.get() && isCategoriesPinned.get()) {
                    handler.sendEmptyMessage(0);
                    Thread.currentThread().interrupt();
                    return;
                }

                if (noConnection.get()) {
                    handler.sendEmptyMessage(1);
                    Thread.currentThread().interrupt();
                    return;
                }
            }

        }).start();


        ParseQuery.getQuery("Purchase").fromNetwork().findInBackground((purchases, e) -> {
            if (e == null)
                ParseObject.unpinAllInBackground(context.getString(R.string.all_purchases),
                        e1 -> ParseObject.pinAllInBackground(context.getString(R.string.all_purchases), purchases,
                                e2 -> isPurchasesPinned.set(true)));
            else if (e.getCode() == ParseException.CONNECTION_FAILED)
                noConnection.set(true);

        });

        ParseQuery.getQuery("Client").fromNetwork().findInBackground((clients, e) -> {
            if (e == null)
                ParseObject.unpinAllInBackground(context.getString(R.string.all_clients),
                        e1 -> ParseObject.pinAllInBackground(context.getString(R.string.all_clients), clients,
                                e2 -> isClientsPinned.set(true)));
            else if (e.getCode() == ParseException.CONNECTION_FAILED)
                noConnection.set(true);
        });

        ParseQuery.getQuery("Category").fromNetwork().findInBackground((categories, e) -> {
            if (e == null)
                ParseObject.unpinAllInBackground(context.getString(R.string.all_categories),
                        e1 -> ParseObject.pinAllInBackground(context.getString(R.string.all_categories), categories,
                                e2 -> isCategoriesPinned.set(true)));
            else if (e.getCode() == ParseException.CONNECTION_FAILED)
                noConnection.set(true);
        });


    }

    public static void initializePurchaseList(Context context, List<Purchase> purchases, PurchasesAdapter adapter) {

        ParseQuery.getQuery("Purchase").fromPin(context.getString(R.string.all_purchases))
                .orderByAscending("createdAt")
                .findInBackground(((objects, e) -> {
                    if (e == null)
                        for (ParseObject object : objects) {

                            try {
                                ParseQuery.getQuery("Client").fromPin(context.getString(R.string.all_clients)).find();
                            } catch (ParseException ex) {
                                ex.printStackTrace();
                            }

                            ParseObject clientObject = object.getParseObject("client");
                            assert clientObject != null;
                            Client client = new Client(clientObject.getString("name"),
                                    clientObject.getString("phone_number"),
                                    clientObject.getString("address"));

                            try {
                                ParseQuery.getQuery("Category").fromPin(context.getString(R.string.all_categories)).find();
                            } catch (ParseException ex) {
                                ex.printStackTrace();
                            }

                            ParseObject categoryObject = object.getParseObject("category");
                            assert categoryObject != null;
                            Category category = new Category(categoryObject.getString("name"),
                                    categoryObject.getDouble("price"),
                                    categoryObject.getInt("color"));

                            purchases.add(new Purchase(
                                    object.getObjectId(),
                                    client,
                                    category,
                                    object.getDate("date"),
                                    object.getDouble("weight"),
                                    object.getDouble("cash"),
                                    object.getDouble("debt"),
                                    object.getDouble("check"),
                                    object.getDouble("outlay"),
                                    object.getString("note")
                            ));

                        }
                    else
                        e.printStackTrace();

                    if (adapter != null)
                        adapter.notifyDataSetChanged();

                }));


    }


    public static List<Purchase> initializePurchaseFilterList(Context context) {

        List<ParseObject> objects;
        List<Purchase> purchases = new ArrayList<>();
        try {
            objects = ParseQuery.getQuery("Purchase").fromPin(context.getString(R.string.all_purchases))
                    .orderByAscending("createdAt")
                    .find();

            for (ParseObject object : objects) {

                try {
                    ParseQuery.getQuery("Client").fromPin(context.getString(R.string.all_clients)).find();
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }

                ParseObject clientObject = object.getParseObject("client");
                assert clientObject != null;
                Client client = new Client(clientObject.getString("name"),
                        clientObject.getString("phone_number"),
                        clientObject.getString("address"));

                try {
                    ParseQuery.getQuery("Category").fromPin(context.getString(R.string.all_categories)).find();
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }

                ParseObject categoryObject = object.getParseObject("category");
                assert categoryObject != null;
                Category category = new Category(categoryObject.getString("name"),
                        categoryObject.getDouble("price"),
                        categoryObject.getInt("color"));

                purchases.add(new Purchase(
                        object.getObjectId(),
                        client,
                        category,
                        object.getDate("date"),
                        object.getDouble("weight"),
                        object.getDouble("cash"),
                        object.getDouble("debt"),
                        object.getDouble("check"),
                        object.getDouble("outlay"),
                        object.getString("note")
                ));

            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return purchases;

    }

    public static List<Client> initializeClientFilterList(Context context) {

        List<ParseObject> objects;
        List<Client> clients = new ArrayList<>();
        try {
            objects = ParseQuery.getQuery("Client").fromPin(context.getString(R.string.all_clients))
                    .orderByAscending("createdAt")
                    .find();

            for (ParseObject object : objects) {
                clients.add(new Client(object.getString("name"),
                        object.getString("phone_number"),
                        object.getString("address")));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return clients;
    }

    public static void initializeClientList(Context context, List<Client> clients, ClientsAdapter adapter) {

        ParseQuery.getQuery("Client").fromPin(context.getString(R.string.all_clients))
                .orderByAscending("name")
                .findInBackground((objects, e) -> {
                    if (e == null)
                        for (ParseObject object : objects)
                            clients.add(new Client(object.getString("name"),
                                    object.getString("phone_number"),
                                    object.getString("address")));
                    else
                        e.printStackTrace();

                    if (adapter != null)
                        adapter.notifyDataSetChanged();

                });
    }

    public static void initializeClientsDialog(Context context, ListView clientsList, List<Client> clients, ArrayList<String> clientsName, ListAdapter adapter) {

        ParseQuery.getQuery("Client").fromPin(context.getString(R.string.all_clients))
                .orderByAscending("name")
                .findInBackground((objects, e) -> {
                    if (e == null) {
                        for (ParseObject object : objects) {

                            clients.add(new Client(object.getString("name"),
                                    object.getString("phone_number"),
                                    object.getString("address")));
                            clientsName.add(object.getString("name"));
                        }

                        clientsName.add("+");

                        clientsList.setAdapter(adapter);

                    } else
                        e.printStackTrace();


                });

    }

    public static void initializeClientsMenu(NavigationView navigationView) {

        Context context = navigationView.getContext();

        final Menu menu = navigationView.getMenu();
        MenuItem clientsFilter = menu.getItem(3);
        SubMenu clientsSubMenu = clientsFilter.getSubMenu();
        clientsSubMenu.clear();

        ParseQuery.getQuery("Client").fromPin(context.getString(R.string.all_clients))
                .orderByAscending("name")
                .findInBackground((clients, e) -> {
                    if (e == null)
                        for (ParseObject client : clients)
                            clientsSubMenu.add(Menu.NONE, clients.indexOf(client) + 1, clients.indexOf(client) + 1, client.getString("name"))
                                    .setIcon(R.drawable.ic_client)
                                    .setOnMenuItemClickListener(menuItem -> {
                                        Log.i("Info menu item title", menuItem.getTitle().toString());
                                        ((MainActivity) navigationView.getContext()).filterItems(client.getString("name"));
                                        ((MainActivity) navigationView.getContext()).setSelectedClient(client.getString("name"));
                                        ((DrawerLayout) ((MainActivity) navigationView.getContext()).findViewById(R.id.drawer_layout)).closeDrawers();
                                        return false;
                                    });
                    else
                        e.printStackTrace();

                    navigationView.invalidate();

                });
    }

    public static void initializeClientsMenu(NavigationView navigationView, List<Client> clients) {

        final Menu menu = navigationView.getMenu();
        MenuItem clientsFilter = menu.getItem(3);
        SubMenu clientsSubMenu = clientsFilter.getSubMenu();
        clientsSubMenu.clear();

        for (Client client : clients)
            clientsSubMenu.add(Menu.NONE, clients.indexOf(client) + 1, clients.indexOf(client) + 1, client.getName())
                    .setIcon(R.drawable.ic_client)
                    .setOnMenuItemClickListener(menuItem -> {
                        Log.i("Info menu item title", menuItem.getTitle().toString());
                        ((MainActivity) navigationView.getContext()).filterItems(client.getName());
                        ((MainActivity) navigationView.getContext()).setSelectedClient(client.getName());
                        ((DrawerLayout) ((MainActivity) navigationView.getContext()).findViewById(R.id.drawer_layout)).closeDrawers();
                        return false;
                    });

        navigationView.invalidate();

    }

    public static void initializeCategoryList(Context context, List<Category> categories, CategoriesAdapter adapter) {

        ParseQuery.getQuery("Category").fromPin(context.getString(R.string.all_categories))
                .orderByAscending("createdAt")
                .findInBackground((objects, e) -> {
                    if (e == null)
                        for (ParseObject object : objects)
                            categories.add(new Category(object.getString("name"),
                                    object.getDouble("price"),
                                    object.getInt("color")));
                    else
                        e.printStackTrace();

                    adapter.notifyDataSetChanged();

                });
    }


    private static ParseObject getClient(Context context, String phoneNumber) {
        try {
            return ParseQuery.getQuery("Client").fromPin(context.getString(R.string.all_clients))
                    .whereEqualTo("phone_number", phoneNumber)
                    .getFirst();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    private static ParseObject getCategory(Context context, String categoryName) {
        try {
            return ParseQuery.getQuery("Category").fromPin(context.getString(R.string.all_categories))
                    .whereEqualTo("name", categoryName)
                    .getFirst();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void insertPurchase(Context context, Purchase purchase, PurchasesFragment.AddPurchaseListener listener) {

        purchaseEntry.put("client", Objects.requireNonNull(getClient(context, purchase.getClient().getPhoneNumber())));
        purchaseEntry.put("category", Objects.requireNonNull(getCategory(context, purchase.getCategory().getName())));
        purchaseEntry.put("weight", purchase.getWeight());
        purchaseEntry.put("date", purchase.getDate());
        purchaseEntry.put("cash", purchase.getCash());
        purchaseEntry.put("debt", purchase.getDebt());
        purchaseEntry.put("check", purchase.getCheck());
        purchaseEntry.put("outlay", purchase.getOutlay());
        purchaseEntry.put("note", purchase.getNote());


        purchaseEntry.saveEventually();

        purchaseEntry.pinInBackground(context.getString(R.string.all_purchases), e -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

            ParseQuery.getQuery("Purchase").fromPin(context.getString(R.string.all_purchases))
                    .orderByDescending("createdAt")
                    .getFirstInBackground((object, e1) -> {
                        if (e1 == null)
                            listener.onPurchaseAdded(object.getObjectId());
                        else
                            e1.printStackTrace();
                    });
        });


    }

    public static void insertClient(Context context, Client client, FragmentManager
            fragmentManager, ClientsFragment.AddClientListener listener) {

        ParseObject entry = new ParseObject("Client");

        entry.put("name", client.getName());
        entry.put("address", client.getAddress());
        entry.put("phone_number", client.getPhoneNumber());

        LoadingDialog loadingDialog = new LoadingDialog("Creating client ...");
        loadingDialog.show(fragmentManager, "dialog");
        loadingDialog.setCancelable(false);

        ParseQuery.getQuery("Client").fromPin(context.getString(R.string.all_clients))
                .whereEqualTo("phone_number", client.getPhoneNumber())
                .getFirstInBackground((object, e) -> {
                    if (object == null) {
                        entry.saveEventually();
                        entry.pinInBackground(context.getString(R.string.all_clients), e1 -> {
                            loadingDialog.dismiss();
                            listener.onClientAdded();
                        });
                    } else {
                        loadingDialog.dismiss();
                        new AlertDialog.Builder(context).setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Duplicate phone number")
                                .setMessage("Phone number already exist")
                                .show();
                    }
                });


    }

    public static void insertCategory(Context context, Category category, FragmentManager
            fragmentManager, CategoriesDialog.AddCategoryListener listener) {

        ParseObject entry = new ParseObject("Category");
        entry.put("name", category.getName());
        entry.put("price", category.getPrice());
        entry.put("color", category.getColor());

        LoadingDialog loadingDialog = new LoadingDialog("Creating category ...");
        loadingDialog.show(fragmentManager, "dialog");
        loadingDialog.setCancelable(false);

        ParseQuery.getQuery("Category")
                .fromPin(context.getString(R.string.all_categories))
                .whereEqualTo("name", category.getName())
                .getFirstInBackground((object, e) -> {

                    if (object == null) {
                        entry.saveEventually();
                        entry.pinInBackground(context.getString(R.string.all_categories), e1 -> {
                            loadingDialog.dismiss();
                            listener.onCategoryAdded();
                        });
                    } else {
                        loadingDialog.dismiss();
                        new AlertDialog.Builder(context).setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Duplicate category")
                                .setMessage("Category name already exist")
                                .show();
                    }
                });

    }

    public static void updatePurchase(Context context, Purchase purchase, PurchasesFragment.UpdatePurchaseListener listener) {


        ParseQuery.getQuery("Purchase").fromPin(context.getString(R.string.all_purchases))
                .getInBackground(purchase.getId(), (object, e) -> {
                    object.put("client", Objects.requireNonNull(getClient(context, purchase.getClient().getPhoneNumber())));
                    object.put("category", Objects.requireNonNull(getCategory(context, purchase.getCategory().getName())));
                    object.put("weight", purchase.getWeight());
                    object.put("cash", purchase.getCash());
                    object.put("debt", purchase.getDebt());
                    object.put("check", purchase.getCheck());
                    object.put("date", purchase.getDate());
                    object.put("note", purchase.getNote());
                    object.put("outlay", purchase.getOutlay());

                    object.saveEventually(e1 -> listener.onPurchaseUpdated());
                });


    }

    public static void updateClient(Context context, Client client, boolean newPhoneNumber, FragmentManager
            fragmentManager, ClientsFragment.UpdateClientListener listener) {

        LoadingDialog loadingDialog = new LoadingDialog("Updating client ...");
        loadingDialog.show(fragmentManager, "dialog");
        loadingDialog.setCancelable(false);

        ParseQuery.getQuery("Client").fromPin(context.getString(R.string.all_clients))
                .whereEqualTo("phone_number", client.getPhoneNumber())
                .getFirstInBackground((object, e) -> {
                    if (!newPhoneNumber) {
                        object.put("name", client.getName());
                        object.put("address", client.getAddress());
                        object.saveEventually(e2 -> {
                            loadingDialog.dismiss();
                            listener.onClientUpdate();
                        });
                    } else {
                        if (object == null) {
                            ParseQuery.getQuery("Client").fromPin(context.getString(R.string.all_clients))
                                    .whereEqualTo("phone_number", client.getOldPhoneNumber())
                                    .getFirstInBackground((newObject, e1) -> {
                                        newObject.put("name", client.getName());
                                        newObject.put("phone_number", client.getPhoneNumber());
                                        newObject.put("address", client.getAddress());

                                        newObject.saveEventually(e2 -> {
                                            loadingDialog.dismiss();
                                            listener.onClientUpdate();
                                        });
                                    });
                        } else {
                            loadingDialog.dismiss();
                            new AlertDialog.Builder(context).setIcon(android.R.drawable.ic_dialog_alert)
                                    .setTitle("Duplicate phone number")
                                    .setMessage("Phone number already exist")
                                    .show();
                        }
                    }
                });


    }

    public static boolean clientHaveRelatedPurchases(Context context, String phoneNumber) {

        try {
            ParseQuery.getQuery("Purchase").fromPin(context.getString(R.string.all_purchases))
                    .whereEqualTo("client", getClient(context, phoneNumber))
                    .getFirst();
            return true;
        } catch (ParseException e) {
            return false;
        }

    }


    public static boolean categoryHaveRelatedPurchases(Context context, String categoryName) {
        try {
            ParseQuery.getQuery("Purchase").fromPin(context.getString(R.string.all_purchases))
                    .whereEqualTo("category", getCategory(context, categoryName))
                    .getFirst();
            return true;
        } catch (ParseException e) {
            return false;
        }
    }


    public static void removePurchase(Context context, String
            id, PurchasesFragment.DeletePurchaseListener listener) {

        ParseQuery.getQuery("Purchase").fromPin(context.getString(R.string.all_purchases))
                .getInBackground(id, (object, e) -> object.deleteEventually(e1 -> listener.onPurchaseDeleted()));
    }


    public static void removeClient(Context context, String
            phoneNumber, ClientsFragment.DeleteClientListener listener) {
        ParseQuery.getQuery("Client").fromPin(context.getString(R.string.all_clients))
                .whereEqualTo("phone_number", phoneNumber)
                .getFirstInBackground((object, e) -> object.deleteEventually(e1 -> listener.onClientDeleted()));
    }

    public static void removeCategory(Context context, String
            categoryName, CategoriesDialog.DeleteCategoryListener listener) {
        ParseQuery.getQuery("Category").fromPin(context.getString(R.string.all_categories))
                .whereEqualTo("name", categoryName)
                .getFirstInBackground((object, e) -> object.deleteEventually(e1 -> listener.onCategoryDeleted()));
    }


    public static void accountingTableSetup(Context context, AccountingFragment.AccountingListener listener) {
        AtomicLong sales = new AtomicLong(0);
        AtomicLong paid = new AtomicLong(0);
        AtomicLong totalWeight = new AtomicLong(0);
        AtomicLong debt = new AtomicLong(0);

        AtomicBoolean isDataReady = new AtomicBoolean(false);

        new Thread(() -> {

            Handler handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(@NotNull Message msg) {
                    listener.onCalculateListener(sales.get(), paid.get(), totalWeight.get(), debt.get());
                }
            };

            while (true) {
                if (isDataReady.get()) {
                    handler.sendEmptyMessage(0);
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }).start();

        ParseQuery.getQuery("Purchase").fromPin(context.getString(R.string.all_purchases))
                .findInBackground((objects, e) ->

                {
                    if (e == null) {
                        if (!objects.isEmpty())
                            for (ParseObject object : objects) {
                                sales.addAndGet(object.getLong("cash"));
                                paid.addAndGet(object.getLong("outlay"));
                                totalWeight.addAndGet(object.getLong("weight"));
                                debt.addAndGet(object.getLong("debt"));
                                isDataReady.set(true);
                            }
                        else
                            isDataReady.set(true);
                    }
                });
    }
}
