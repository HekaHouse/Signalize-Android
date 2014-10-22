package ppc.signalize.myvoice.util.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ppc.signalize.myvoice.model.AbstractItem;

public class MyMenuAdapter extends AbstractRecyclerAdapter {

    public MyMenuAdapter(List<AbstractItem> items, int rowLayout, Context context) {
        super(items, rowLayout, context);
    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);

        return new MenuViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        AbstractItem item = items.get(i);
        MenuViewHolder mv = (MenuViewHolder)viewHolder;
        mv.item_name.setText(mContext.getString(item.getStringResourceId(mContext)));
        mv.item_image.setImageResource(item.getImageResourceId(mContext));
    }

    @Override
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
    @Override
    public void clearSelections() {
        selectedItems.clear();
        notifyDataSetChanged();
    }


}