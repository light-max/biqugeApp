package com.lifengqiang.biquge.ui.chapter;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lifengqiang.biquge.R;
import com.lifengqiang.biquge.base.call.Base;
import com.lifengqiang.biquge.base.mvp.BaseView;
import com.lifengqiang.biquge.book.ChapterReadRecorder;
import com.lifengqiang.biquge.data.BookDetails;
import com.lifengqiang.biquge.utils.VerticalSeekBar;

import java.util.List;

public class ChapterView extends BaseView {
    private boolean dark;
    private int backgroundColor;
    private int textColor;
    private ChapterListAdapter adapter;
    private VerticalSeekBar seekBar;
    private OnActionListener onActionListener;
    private RecyclerView recyclerView;

    @Override
    public void onCreate(Base base, Bundle savedInstanceState) {
        super.onCreate(base, savedInstanceState);
        FragmentActivity activity = base.getActivity();
        dark = activity.getIntent().getBooleanExtra("dark", false);
        backgroundColor = dark ?
                activity.getColor(R.color.colorDarkBG) :
                activity.getColor(R.color.colorLightBG);
        textColor = dark ?
                activity.getColor(R.color.colorDarkTC) :
                activity.getColor(R.color.colorLightTC);

        adapter = new ChapterListAdapter(textColor);
        recyclerView = get(R.id.recycler);
        recyclerView.setAdapter(adapter);
        DividerItemDecoration decor = new DividerItemDecoration(base.getContext(), DividerItemDecoration.VERTICAL);
        decor.getDrawable().setTint(Color.GRAY);
        recyclerView.addItemDecoration(decor);

        seekBar = get(R.id.seek_bar);
        get(R.id.root).setBackgroundColor(backgroundColor);
        getBack().setColorFilter(textColor);
        getTitle().setTextColor(textColor);
        getMenu().setColorFilter(textColor);
        getBack().setOnClickListener(v -> {
            base.getActivity().finish();
        });
        getMenu().setOnClickListener(this::showMenu);

        seekBar.setOnSeekListener(new VerticalSeekBar.OnSeekListener() {
            @Override
            public void onSeek(int max, int current) {
                recyclerView.scrollToPosition(current - 1);
            }

            @Override
            public void onStop(int max, int current) {
                recyclerView.scrollToPosition(current - 1);
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    seekBar.setProgress(manager.findFirstVisibleItemPosition() + 1);
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (!seekBar.isTouching()) {
                    LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    seekBar.setProgress(manager.findFirstVisibleItemPosition() + 1);
                }
            }
        });
        adapter.setOnItemClickListener((data, position) -> {
            if (onActionListener != null) {
                onActionListener.onSelected(data);
            }
        });
        adapter.setOnCacheListener((node, position) -> {
            if (onActionListener != null) {
                onActionListener.onCacheNode(node, position);
            }
        });
    }

    public void setBook(BookDetails book) {
        getTitle().setText(book.name);
        adapter.setData(book.getNodes());
        adapter.notifyDataSetChanged();
        seekBar.setMax(book.getNodes().size());
        base.map("bookUrl", book.url);
        scrollToLastNode(book.url);
    }

    public void scrollToLastNode(String bookUrl) {
        String lastReadNodeUrl = ChapterReadRecorder
                .getLastRead(base.getContext(), bookUrl);
        List<BookDetails.Node> data = getAdapter().getData();
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).url.equals(lastReadNodeUrl)) {
                getRecyclerView().scrollToPosition(i);
                break;
            }
        }
    }

    private void showMenu(View view) {
        ChapterMenuOptions.showMenu(view, backgroundColor, textColor,
                () -> {
                    String bookUrl = base.map("bookUrl");
                    if (bookUrl != null) {
                        ChapterDownloadUtils.getInstance().download(
                                base.getContext(),
                                bookUrl,
                                getAdapter().getData(),
                                () -> {
                                    getAdapter().getMap().clear();
                                    getAdapter().notifyDataSetChanged();
                                }
                        );
                    }
                }, () -> {
                    getAdapter().setDesc(false);
                    getAdapter().notifyDataSetChanged();
                }, () -> {
                    getAdapter().setDesc(true);
                    getAdapter().notifyDataSetChanged();
                }, () -> {
                    String bookUrl = base.map("bookUrl");
                    if (bookUrl != null) {
                        scrollToLastNode(bookUrl);
                    }
                });
    }

    public ImageView getBack() {
        return get(R.id.back);
    }

    public TextView getTitle() {
        return get(R.id.title);
    }

    public ImageView getMenu() {
        return get(R.id.menu);
    }

    public ChapterListAdapter getAdapter() {
        return adapter;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public interface OnActionListener {
        void onSelected(BookDetails.Node node);

        void onCacheNode(BookDetails.Node node, int position);
    }

    public void setOnActionListener(OnActionListener onActionListener) {
        this.onActionListener = onActionListener;
    }
}
