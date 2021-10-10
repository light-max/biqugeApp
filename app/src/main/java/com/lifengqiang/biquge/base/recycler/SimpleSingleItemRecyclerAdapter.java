package com.lifengqiang.biquge.base.recycler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lifengqiang.biquge.base.call.ViewGet;

import java.util.ArrayList;
import java.util.List;

public abstract class SimpleSingleItemRecyclerAdapter<T> extends RecyclerView.Adapter<SimpleSingleItemRecyclerAdapter.ViewHolder> {
    private static final int TYPE_HEAD = -1001;
    private static final int TYPE_ITEM = -1002;
    private static final int TYPE_FOOTER = -1003;

    protected LayoutInflater inflater;
    protected OnItemClickListener<T> onItemClickListener;
    protected OnItemLongClickListener<T> onItemLongClickListener;

    private List<T> data;
    private View headView;
    private View footerView;

    public SimpleSingleItemRecyclerAdapter() {
        data = new ArrayList<>();
    }

    public SimpleSingleItemRecyclerAdapter(List<T> data) {
        this.data = data;
    }

    protected abstract int getItemViewLayout();

    protected abstract void onBindViewHolder(ViewHolder holder, T data, int position);

    public void setHeadView(View headView) {
        this.headView = headView;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEAD) {
            return new ViewHolder(headView);
        } else if (viewType == TYPE_FOOTER) {
            return new ViewHolder(footerView);
        } else {
            if (inflater == null) {
                inflater = LayoutInflater.from(parent.getContext());
            }
            View view = inflater.inflate(getItemViewLayout(), parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int dataIndex = headView == null ? position : position - 1;
        if (dataIndex >= 0 && dataIndex < getDataCount()) {
            onBindViewHolder(holder, data.get(dataIndex), dataIndex);
            holder.itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onClick(data.get(dataIndex), dataIndex);
                }
            });
            holder.itemView.setOnLongClickListener(v -> {
                if (onItemLongClickListener != null) {
                    onItemLongClickListener.onLongClick(data.get(dataIndex), dataIndex);
                }
                return true;
            });
        }
    }

    @Override
    public int getItemCount() {
        return getDataCount() +
                (headView == null ? 0 : 1) +
                (footerView == null ? 0 : 1);
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public int getDataCount() {
        return data == null ? 0 : data.size();
    }

    public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener<T> onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return headView == null ? TYPE_ITEM : TYPE_HEAD;
        } else if (position == getItemCount() - 1) {
            return footerView == null ? TYPE_ITEM : TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements ViewGet {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public <T extends View> T get(int viewId) {
            return itemView.findViewById(viewId);
        }

        public ViewHolder setText(int viewId, String text) {
            ((TextView) get(viewId)).setText(text);
            return this;
        }

        public TextView getText(int viewId) {
            return get(viewId);
        }

        public ImageView getImage(int viewId) {
            return get(viewId);
        }
    }

    public interface OnItemClickListener<T> {
        void onClick(T data, int position);
    }

    public interface OnItemLongClickListener<T> {
        void onLongClick(T data, int position);
    }
}
