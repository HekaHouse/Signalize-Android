package ppc.signalize.myvoice.util.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ppc.signalize.myvoice.R;
import ppc.signalize.myvoice.model.AbstractItem;


public class CareTeamAdapter extends AbstractRecyclerAdapter {



    public CareTeamAdapter(List<AbstractItem> items, int rowLayout, Context context) {
        super(items, rowLayout, context);
    }

    @Override
    public CareTeamViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        return new CareTeamViewHolder(v);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        AbstractItem item = items.get(i);
        CareTeamViewHolder cv = (CareTeamViewHolder)viewHolder;
        cv.member_name.setText(mContext.getString(item.getStringResourceId(mContext)));
        cv.member_specialty.setText(mContext.getString(item.getMetaStringResourceId(mContext, "_specialty")));
        cv.member_experience.setText(mContext.getString(item.getMetaStringResourceId(mContext, "_experience")));
        cv.member_photo.setImageResource(item.getImageResourceId(mContext));
    }


}