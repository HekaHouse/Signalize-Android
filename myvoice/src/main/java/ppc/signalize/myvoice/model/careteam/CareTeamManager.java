package ppc.signalize.myvoice.model.careteam;


import java.util.ArrayList;
import java.util.List;

import ppc.signalize.myvoice.model.AbstractItem;
import ppc.signalize.myvoice.model.AbstractManager;

/**
 * Created by Aron on 10/9/2014.
 */
public class CareTeamManager extends AbstractManager {

    public CareTeamManager() {
        menuArray = new String[]{"Ken Dobular", "Jeanne Herrmann", "Sarah Anderson", "Jim Lepich"};
    }
    @Override
    public List<AbstractItem> getItems() {
        if (menu_items == null) {
            menu_items = new ArrayList<AbstractItem>();

            for (String menu_item : menuArray) {
                CareTeamMember item = new CareTeamMember();
                item.name = menu_item;
                item.resourceName = menu_item.replaceAll("\\s+","_").toLowerCase();
                menu_items.add(item);
            }
        }

        return  menu_items;
    }
}