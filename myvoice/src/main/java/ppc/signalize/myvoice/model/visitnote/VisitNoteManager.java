package ppc.signalize.myvoice.model.visitnote;


import java.util.ArrayList;
import java.util.List;

import ppc.signalize.myvoice.model.AbstractItem;
import ppc.signalize.myvoice.model.AbstractManager;

/**
 * Created by Aron on 10/9/2014.
 */
public class VisitNoteManager extends AbstractManager {

    public VisitNoteManager() {
        menuArray = new String[]{"Ken Dobular", "Jeanne Herrmann", "Sarah Anderson", "Jim Lepich"};
    }
    @Override
    public List<AbstractItem> getItems() {
        if (menu_items == null) {
            menu_items = new ArrayList<AbstractItem>();

            for (String menu_item : menuArray) {
                VisitNoteMember item = new VisitNoteMember();
                item.name = menu_item;
                item.resourceName = menu_item.replaceAll("\\s+","_").toLowerCase();
                menu_items.add(item);
            }
        }

        return  menu_items;
    }
}