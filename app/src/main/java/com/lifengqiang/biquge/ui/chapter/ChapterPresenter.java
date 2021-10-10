package com.lifengqiang.biquge.ui.chapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.lifengqiang.biquge.base.call.Base;
import com.lifengqiang.biquge.base.mvp.BasePresenter;
import com.lifengqiang.biquge.data.BookDetails;

public class ChapterPresenter extends BasePresenter<ChapterModel, ChapterView> {
    @Override
    public void onCreate(Base base, Bundle savedInstanceState) {
        super.onCreate(base, savedInstanceState);
        model.getBook().observe(base, details -> {
            view.setBook(details);
        });
        view.setOnActionListener(new ChapterView.OnActionListener() {
            @Override
            public void onSelected(BookDetails.Node node) {
                Intent intent = new Intent();
                intent.putExtra(ChapterActivity.NODE_URL, node.url);
                base.getActivity().setResult(Activity.RESULT_OK, intent);
                base.getActivity().finish();
            }

            @Override
            public void onCacheNode(BookDetails.Node node, int position) {
                ChapterDownloadUtils.getInstance().download(node, () -> {
                    view.getAdapter().getMap().remove(node.url);
                    view.getAdapter().notifyItemChanged(position);
                });
            }
        });
    }
}
