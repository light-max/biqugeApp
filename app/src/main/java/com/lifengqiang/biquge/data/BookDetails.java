package com.lifengqiang.biquge.data;

import java.util.ArrayList;
import java.util.List;

public class BookDetails {
    public String url;
    public String name;
    public String cover;
    public String author;
    public String lastUpdateNode;
    public String lastUpdateUrl;
    public String intro;
    public String dateTime;

    public List<Node> nodes;

    public List<Node> getNodes() {
        if (nodes == null) {
            nodes = new ArrayList<>();
        }
        return nodes;
    }

    public static class Node {
        public String name;
        public String url;
    }
}
