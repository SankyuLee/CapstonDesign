package edu.skku.capstone.justpay;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class RoomTabAdapter extends RecyclerView.Adapter<RoomTabAdapter.ViewHolder> {

    private ArrayList<String> tabList;
    private Context context;
    private View.OnClickListener onClickTab;

    public RoomTabAdapter(Context context, ArrayList<String> tabList, View.OnClickListener onClickTab) {
        this.context = context;
        this.tabList = tabList;
        this.onClickTab = onClickTab;
    }

    @NonNull
    @Override
    public RoomTabAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_tab, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomTabAdapter.ViewHolder viewHolder, int i) {
        String tab = tabList.get(i);

        viewHolder.textView.setText(tab);
        viewHolder.textView.setTag(tab);
        viewHolder.textView.setOnClickListener(onClickTab);
    }

    @Override
    public int getItemCount() {
        return tabList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tab_text_view);
        }
    }
}
