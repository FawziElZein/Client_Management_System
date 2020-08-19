package com.clientsinfo.recyclerviewinterfaces;

import java.util.Date;

public interface FilterableItems {


    void filterItems(String name);

    void filterItems(String name, Date startDate, Date endDate);


}
