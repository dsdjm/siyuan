package com.dsdjm.siyuan.model;

import java.util.ArrayList;
//{"title":"testTitle","url":"testUrl","items":[{"url":"1.jpg"}]}
//[{"title":"testTitle","url":"testUrl","items":[{"url":"1.jpg"}]}]

public class Group {
    public String title;
    public String url;
    public ArrayList<Item> items = new ArrayList<Item>();
}
