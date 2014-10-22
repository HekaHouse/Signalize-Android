package ppc.signalize.myvoice.model;

import android.content.Context;

/**
 * Created by Aron on 10/9/2014.
 */
public class AbstractItem {
    public String name;
    public String description;
    public String resourceName;

    public int getMetaStringResourceId(Context context, String meta)
    {
        try {
            return context.getResources().getIdentifier(this.resourceName+meta, "string", context.getPackageName());

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public int getStringResourceId(Context context)
    {
        try {
            return context.getResources().getIdentifier(this.resourceName, "string", context.getPackageName());

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
    public int getImageResourceId(Context context)
    {
        try {
            return context.getResources().getIdentifier(this.resourceName, "drawable", context.getPackageName());

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
