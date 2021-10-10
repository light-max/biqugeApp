package com.lifengqiang.biquge.ui.bookdetails;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lifengqiang.biquge.R;
import com.lifengqiang.biquge.base.call.Base;
import com.lifengqiang.biquge.base.mvp.BaseView;
import com.lifengqiang.biquge.base.recycler.SimpleSingleItemRecyclerAdapter;
import com.lifengqiang.biquge.data.BookDetails;
import com.lifengqiang.biquge.net.BiqugeApi;

import java.util.Collections;
import java.util.List;

public class BookDetailsView extends BaseView {
    private ImageView cover;
    private TextView name;
    private TextView author;
    private TextView lastNode;
    private TextView updateTime;
    private TextView intro;
    private ImageView background;
    private RecyclerView miniNodesView;
    private LinearLayout allNodeLayout;
    private RecyclerView nodesView;

    private OpenNodeListener openNodeListener;

    private final MiniNodesAdapter miniNodesAdapter = new MiniNodesAdapter();
    private final NodesAdapter nodesAdapter = new NodesAdapter();

    @Override
    public void onCreate(Base base, Bundle savedInstanceState) {
        super.onCreate(base, savedInstanceState);
        cover = base.get(R.id.cover);
        name = base.get(R.id.name);
        author = base.get(R.id.author);
        lastNode = base.get(R.id.last_node);
        updateTime = base.get(R.id.date_time);
        intro = base.get(R.id.intro);
        background = base.get(R.id.background);
        miniNodesView = base.get(R.id.mini_nodes_view);
        allNodeLayout = base.get(R.id.all_node_layout);
        nodesView = base.get(R.id.nodes_view);

        getReadButton().setOnClickListener(v -> {
            List<BookDetails.Node> book = miniNodesAdapter.getData();
            if (openNodeListener != null && !book.isEmpty()) {
                openNodeListener.onOpen(new BookDetails.Node());
            }
        });

        getAllNodeButton().setOnClickListener(v -> {
            allNodeLayout.startAnimation(AnimationUtils.loadAnimation(base.getContext(), R.anim.nodes_layout_botton_in));
            allNodeLayout.setVisibility(View.VISIBLE);
        });

        click(R.id.nodes_put_away, this::allNodeLayoutIsHide);

        click(R.id.nodes_sort, () -> {
            Collections.reverse(nodesAdapter.getData());
            nodesAdapter.notifyDataSetChanged();
        });
    }

    public boolean allNodeLayoutIsHide() {
        if (allNodeLayout.getVisibility() == View.VISIBLE) {
            allNodeLayout.startAnimation(AnimationUtils.loadAnimation(base.getContext(), R.anim.nodes_layout_botton_out));
            allNodeLayout.setVisibility(View.GONE);
            return false;
        } else {
            return true;
        }
    }

    public void setBookDetails(BookDetails book) {
        String coverUrl = BiqugeApi.url(book.cover);
        Glide.with(base.getContext())
                .load(coverUrl)
                .into(cover);
        Glide.with(base.getContext())
                .load(coverUrl)
                .into(background);
        name.setText(book.name);
        author.setText(String.format("作者：%s", book.author));
        lastNode.setText(book.lastUpdateNode);
        updateTime.setText(String.format("（%s）", book.dateTime));
        intro.setText(book.intro);
        getAddButton().setVisibility(View.VISIBLE);
        getReadButton().setVisibility(View.VISIBLE);
        getAllNodeButton().setVisibility(View.VISIBLE);
        lastNode.setOnClickListener(v -> {
            if (openNodeListener != null) {
                BookDetails.Node node = new BookDetails.Node();
                node.name = book.lastUpdateNode;
                node.url = book.url + book.lastUpdateUrl;
                openNodeListener.onOpen(node);
            }
        });

        SimpleSingleItemRecyclerAdapter.OnItemClickListener<BookDetails.Node> listener =
                (data, position) -> {
                    if (openNodeListener != null) {
                        openNodeListener.onOpen(data);
                    }
                };
        List<BookDetails.Node> nodes = book.getNodes();
        nodes = nodes.size() >= 10 ?
                nodes.subList(0, 10) :
                nodes.subList(0, nodes.size());
        miniNodesAdapter.setData(nodes);
        miniNodesAdapter.setOnItemClickListener(listener);
        miniNodesView.setAdapter(miniNodesAdapter);

        nodesAdapter.setData(book.getNodes());
        nodesView.setAdapter(nodesAdapter);
        nodesAdapter.setOnItemClickListener(listener);
    }

    public TextView getAddButton() {
        return get(R.id.add);
    }

    public TextView getReadButton() {
        return get(R.id.read);
    }

    public TextView getAllNodeButton() {
        return get(R.id.all_node);
    }

    public void setOpenNodeListener(OpenNodeListener openNodeListener) {
        this.openNodeListener = openNodeListener;
    }

    public interface OpenNodeListener {
        void onOpen(BookDetails.Node node);
    }
}
