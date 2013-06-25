package com.dsdjm.siyuan.ui;

import android.os.Environment;
import com.dsdjm.siyuan.MainConst;
import com.dsdjm.siyuan.model.Group;
import com.dsdjm.siyuan.util.HttpUtil;
import com.dsdjm.siyuan.util.JSonUtil;
import junit.framework.TestCase;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtractDataTest extends TestCase {
    public void testExtractGroups() throws Throwable {
        String url = "http://www.ppmsg.com/siwameitui/";
        List<String> groupsPages = findGroupsPages(url);

        List<String> groups = new ArrayList<String>();
//        for (String groupsPage : groupsPages) {
//            List<String> gs = findGroups(groupsPage, groupsPages.get(0));
//            groups.addAll(gs);
//        }

        for (int i = 0; i < 10; i++) {
            List<String> gs = findGroups(groupsPages.get(i), groupsPages.get(0));
            groups.addAll(gs);
        }

        List<Group> groupList = new ArrayList<Group>();
        for (String groupUrl : groups) {
            Group g = getGroup(groupUrl);
            groupList.add(g);
        }

        File path = new File(
                Environment.getExternalStorageDirectory().getAbsolutePath()
                        + "/Android/data/com.dsdjm.siyuan/files/");
        if (!path.exists()) {
            path.mkdirs();
        }
        File jsonFile = null;
        try {
            jsonFile = File.createTempFile("images", ".txt", path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedWriter bos = new BufferedWriter(new FileWriter(jsonFile));
        bos.write(JSonUtil.toJSON(groupList));
        bos.flush();
        bos.close();
    }

    private List<String> findGroups(String url, String firstUrl) throws Throwable {
        if (!firstUrl.endsWith("/")) {
            firstUrl = firstUrl + "/";
        }
        List<String> groups = new ArrayList<String>();
        Thread.sleep(1000);
        String content = HttpUtil.get(url, "gb2312");
        Pattern p;
        p = Pattern.compile("<a target=\"_blank\" href=\"\\d*/\\d*.html\">");
        Matcher m;
        m = p.matcher(content);
        while (m.find()) {
            String str = m.group();
            int start = str.indexOf("href=\"");
            int end = str.indexOf(".html");
            String t = str.substring(start + 6, end + 5);
            groups.add(firstUrl + t);
        }

        return groups;

    }

    private List<String> findGroupsPages(String url) throws Throwable {
        if (!url.endsWith("/")) {
            url = url + "/";
        }
        List<String> groupPages = new ArrayList<String>();
        groupPages.add(url);
        Thread.sleep(1000);
        String content = HttpUtil.get(url, "gb2312");
        Pattern p;
        p = Pattern.compile("<a href=\"\\d*.html\">尾页</a>");
        Matcher m;
        m = p.matcher(content);
        if (m.find()) {
            String str = m.group();
            int start = str.indexOf("\"");
            int end = str.lastIndexOf(".html");
            String t = str.substring(start + 1, end);
            int number = Integer.parseInt(t);
            for (int i = 2; i <= number; i++) {
                groupPages.add(url + i + ".html");
            }

        }

        return groupPages;
    }

    public void testExtractGroup() throws Throwable {
        String url = "http://www.ppmsg.com/siwameitui/201306/20714.html";
        Group g = getGroup(url);
    }

    private Group getGroup(String url) throws Throwable {
        Group group = new Group();

        String title = findTitle(url);
        group.title = title;

        List<String> pages = findPage(url, ".html");
        List<String> images = new ArrayList<String>();
        for (String pageUrl : pages) {
            List<String> pageImages = findImage(pageUrl);
            images.addAll(pageImages);
        }
        if (images != null && images.size() > 0) {
            String imageUrl = images.get(0);
            int start1 = imageUrl.lastIndexOf("/");
            int start2 = imageUrl.lastIndexOf(".");
            int length = start2 - start1 - 1;
            group.url = imageUrl.substring(0, start1 + 1);
            group.suffix = imageUrl.substring(start2, imageUrl.length());
            group.start = 1;
            group.end = images.size();
            group.length = length;
            group.prefix = "";
        }

        return group;
    }

    private List<String> findImage(String url) throws Throwable {
        Thread.sleep(1000);
        String content = HttpUtil.get(url, "gb2312");
        Pattern p;
        p = Pattern.compile("http://img\\S*\\.jpg");
        Matcher m;
        m = p.matcher(content);
        List<String> imageList = new ArrayList<String>();

        while (m.find()) {
            String image = m.group();
            imageList.add(image);
        }
        return imageList;
    }

    private List<String> findPage(String url, String extension) throws Throwable {
        List<String> urlList = new ArrayList<String>();
        Thread.sleep(1000);
        String content = HttpUtil.get(url, "gb2312");
        Pattern p;
        p = Pattern.compile("本组图片共<strong>\\d*</strong>页");
        Matcher m;
        m = p.matcher(content);

        if (m.find()) {
            String str = m.group();
            int start = str.toLowerCase().indexOf("<strong>");
            int end = str.toLowerCase().indexOf("</strong>");
            String t = str.substring(start + 8, end);
            int number = Integer.parseInt(t);

            if (!extension.startsWith(".")) {
                extension = "." + extension;
            }

            int pos = url.toLowerCase().lastIndexOf(extension);
            String preUrl = url.substring(0, pos);
            urlList.add(url);
            for (int i = 2; i <= number; i++) {
                //TODO : 如果是两位数，是否会补0
                urlList.add(preUrl + "_" + i + extension);
            }
        }
        return urlList;
    }

    private String findTitle(String url) throws Throwable {
        Thread.sleep(1000);
        String content = HttpUtil.get(url, MainConst.CHARSET);
        Pattern p;
        p = Pattern.compile("<title>.*</title>");
        Matcher m;
        m = p.matcher(content);

        if (m.find()) {
            String str = m.group();
            int start = str.toLowerCase().indexOf("<title>");
            int end = str.toLowerCase().indexOf("</title>");
            String t = str.substring(start + 7, end);
            return t;
        }

        return null;
    }

}
