package edu.skku.capstone.justpay;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
    private ArrayList<Event> tabList;
    private Integer lastSelectedIndex;
    private TabOnClickListener tabOnClickListener;

    public EventAdapter(ArrayList<Event> tabList, TabOnClickListener tabOnClickListener) {
        this.tabList = tabList;
        this.lastSelectedIndex = tabList.size() - 1;
        this.tabOnClickListener = tabOnClickListener;
    }

    public interface TabOnClickListener {
        void onTabClicked(int position);
        void onTabDeleteBtnClicked(int position);
    }

    @NonNull
    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_tab, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapter.ViewHolder viewHolder, int position) {
        Event tabItem = tabList.get(position);
        viewHolder.textView.setText(tabItem.getEventTitle());

        final int pos = position;
        viewHolder.tab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabOnClickListener.onTabClicked(pos);
            }
        });

        viewHolder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabOnClickListener.onTabDeleteBtnClicked(pos);
            }
        });

        if (lastSelectedIndex != null) {
            viewHolder.tab.setSelected(position == lastSelectedIndex);
        }
    }

    @Override
    public int getItemCount() {
        return tabList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;
        public ImageButton deleteBtn;
        public ConstraintLayout tab;

        public ViewHolder(View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.tab_text_view);
            deleteBtn = itemView.findViewById(R.id.tab_delete_btn);
            tab = itemView.findViewById(R.id.tab_container);
        }
    }

    public void removeItem(int position) {
        tabList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, tabList.size());
    }

    public void addItem(int position, Event tabItem) {
        tabList.add(position, tabItem);
        notifyItemInserted(position);
        notifyItemRangeChanged(position, tabList.size());
    }

    public void setLastSelectedIndex(int pos) {
        lastSelectedIndex = pos;
    }
}
