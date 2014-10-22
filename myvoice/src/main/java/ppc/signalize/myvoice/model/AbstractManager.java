package ppc.signalize.myvoice.model;

import java.util.ArrayList;
import java.util.List;

import ppc.signalize.myvoice.model.menu.MyMenuItem;

public abstract class AbstractManager {
    public String[] menuArray = {};
    public AbstractManager mInstance;
    public List<AbstractItem> menu_items;


    public abstract List<AbstractItem> getItems();
}
