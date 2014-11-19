package ppc.signalize.myvoice.model.menu;

import java.util.ArrayList;
import java.util.List;

import ppc.signalize.myvoice.model.AbstractItem;
import ppc.signalize.myvoice.model.AbstractManager;

/**
 * Created by Aron on 10/9/2014.
 */
public class MyMenuManager extends AbstractManager {


    public MyMenuManager() {
        menuArray = new String[]{"My Care Team", "My Journal", "My Medical Record", "Request Assistance"};

    }
    @Override
    public List<AbstractItem> getItems() {
        if (menu_items == null) {
            menu_items = new ArrayList<AbstractItem>();

            for (String menu_item : menuArray) {
                MyMenuItem item = new MyMenuItem();
                item.name = menu_item;
                item.resourceName = menu_item.replaceAll("\\s+","_").toLowerCase();
                menu_items.add(item);
            }
        }

        return  menu_items;
    }
    public int getMyIndex(String item) {
        int idx = -1;
        int found = idx;
        for (String menu_item : menuArray) {
            idx++;
            if (menu_item.toLowerCase().trim().equals(item.toLowerCase().trim()))
                found=idx;
        }

        return found;
    }
}
