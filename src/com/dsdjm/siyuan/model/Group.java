package com.dsdjm.siyuan.model;

import java.util.ArrayList;
//{"title":"testTitle","url":"testUrl","items":[{"url":"1.jpg"}]}
//[{"title":"testTitle","url":"testUrl","items":[{"url":"1.jpg"}]}]

public class Group {
    public String title;
    public String url;
    public String prefix;
    public String suffix;
    public int length;
    public int start;
    public int end;
    public ArrayList<Item> items = new ArrayList<Item>();
}
