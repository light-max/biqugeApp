package com.lifengqiang.biquge.ui.content;

import android.content.Context;
import android.os.Bundle;

import com.lifengqiang.biquge.base.call.Base;
import com.lifengqiang.biquge.base.mvp.BasePresenter;
import com.lifengqiang.biquge.book.ChapterReadRecorder;
import com.lifengqiang.biquge.book.bean.BookChapter;
import com.lifengqiang.biquge.book.parse.content.ChapterContentParser;

public class ContentPresenter extends BasePresenter<ContentModel, ContentView> {
    @Override
    public void onCreate(Base base, Bundle savedInstanceState) {
        super.onCreate(base, savedInstanceState);
        if (model.makeParser()) {
            ChapterContentParser parser = model.getParser();
            parser.generateTask().before(() -> {
                view.setEnable(false);
            }).after(() -> {
                view.setEnable(true);
            }).success(data -> {
                view.setContent(data, false, false);
                view.setMaxChapterNumber(parser.getChapterCount());
                view.setMiniTitle(data.getChapterName());
                view.setProgress(parser.indexOfChapter(data.getSelf()) + 1);

                ChapterReadRecorder.putLastRead(base.getContext(),
                        parser.getBookUrl(),
                        parser.getChapterUrl()
                );
            }).run();
            view.setOnChapterChangeListener(new ContentView.OnChapterChangeListener() {
                @Override
                public void onChange(boolean previous) {
                    if (previous) {
                        requestPreviousPage(base);
                    } else {
                        requestNextPage(base);
                    }
                }

                @Override
                public void findTitle(int position) {
                    view.setMiniTitle(parser.getNode(position).name);
                }

                @Override
                public void setByPosition(int position) {
                    // 如果最新章节已经更新了，但本地没有更新则会引发model.getParser().getNode()的异常
                    try {
                        String chapterUrl = parser.getNode(position).url;
                        setNewChapterUrl(base.getContext(), chapterUrl);
                    } catch (Exception e) {
                        base.toast(e.getMessage());
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void requestPreviousPage(Base base) {
        ChapterContentParser parser = model.getParser();
        BookChapter chapter = parser.getChapter();
        if (chapter != null) {
            if (chapter.getPrevious() != null) {
                parser.setUrl(chapter.getPrevious());
                parser.generateTask().before(() -> {
                    view.setEnable(false);
                }).after(() -> {
                    view.setEnable(true);
                }).success(data -> {
                    view.setContent(data, true, true);
                    view.setMiniTitle(data.getChapterName());
                    view.setProgress(parser.indexOfChapter(data.getSelf()) + 1);

                    ChapterReadRecorder.putLastRead(base.getContext(),
                            parser.getBookUrl(),
                            parser.getChapterUrl()
                    );
                }).run();
            } else {
                base.toast("没有上一章了");
            }
        }
    }

    private void requestNextPage(Base base) {
        ChapterContentParser parser = model.getParser();
        BookChapter chapter = parser.getChapter();
        if (chapter != null) {
            if (chapter.getNext() != null) {
                parser.setUrl(chapter.getNext());
                parser.generateTask().before(() -> {
                    view.setEnable(false);
                }).after(() -> {
                    view.setEnable(true);
                }).success(data -> {
                    view.setContent(data, false, true);
                    view.setMiniTitle(data.getChapterName());
                    view.setProgress(parser.indexOfChapter(data.getSelf()) + 1);

                    ChapterReadRecorder.putLastRead(base.getContext(),
                            parser.getBookUrl(),
                            parser.getChapterUrl()
                    );
                }).run();
            } else {
                base.toast("没有下一章了");
            }
        }
    }

    public void setNewChapterUrl(Context context, String chapterUrl) {
        if (model.makeParser()) {
            ChapterContentParser parser = model.getParser();
            parser.setUrl(chapterUrl);
            parser.generateTask().before(() -> {
                view.setEnable(false);
            }).after(() -> {
                view.setEnable(true);
            }).success(data -> {
                view.setContent(data, false, false);
                ChapterReadRecorder.putLastRead(context,
                        parser.getBookUrl(),
                        parser.getChapterUrl()
                );
            }).run();
        }
    }

    public void invalidateProgressBar() {
        if (model.makeParser()) {
            ChapterContentParser parser = model.getParser();
            int progress = parser.indexOfChapter(parser.getChapterUrl()) + 1;
            view.setProgress(progress);
        }
    }
}
