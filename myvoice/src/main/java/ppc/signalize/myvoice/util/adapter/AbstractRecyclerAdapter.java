package ppc.signalize.myvoice.util.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.caverock.androidsvg.SVGImageView;

import java.util.List;

import ppc.signalize.myvoice.R;
import ppc.signalize.myvoice.model.AbstractItem;

/**
 * Created by Aron on 10/9/2014.
 */
public abstract class AbstractRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public List<AbstractItem> items;
    public int rowLayout;
    public Context mContext;
    public SparseBooleanArray selectedItems;

    public AbstractRecyclerAdapter(List<AbstractItem> items, int rowLayout, Context context) {
        this.items = items;
        this.rowLayout = rowLayout;
        this.mContext = context;
        selectedItems = new SparseBooleanArray(items.size());
    }



    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    public void toggleSelection(int pos) {
        clearSelections();

        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
        }
        else {
            selectedItems.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void clearSelections() {
        selectedItems.clear();
        notifyDataSetChanged();

    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public Integer getSelectedItem() {

        return selectedItems.keyAt(0);
    }


    public static class MenuViewHolder extends RecyclerView.ViewHolder {
        public TextView item_name;
        public SVGImageView item_image;

        public MenuViewHolder(View itemView) {
            super(itemView);
            item_name = (TextView) itemView.findViewById(R.id.menu_item_name);
            item_image = (SVGImageView)itemView.findViewById(R.id.menu_image);
        }

    }

    public static class CareTeamViewHolder extends RecyclerView.ViewHolder {
        public TextView member_name;
        public TextView member_experience;
        public TextView member_specialty;
        public ImageView member_photo;

        public CareTeamViewHolder(View itemView) {
            super(itemView);
            member_photo = (ImageView) itemView.findViewById(R.id.care_team_image);
            member_name = (TextView) itemView.findViewById(R.id.care_team_name);
            member_specialty = (TextView) itemView.findViewById(R.id.care_team_specialty);
            member_experience = (TextView) itemView.findViewById(R.id.care_team_experience);
        }


    }
}