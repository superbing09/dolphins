package com.shakethetree.controller;

import io.webfolder.cdp.Launcher;
import io.webfolder.cdp.session.Session;
import io.webfolder.cdp.session.SessionFactory;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xubing
 * @description //TODO 设计说明
 * @date 19-8-27
 * @copyright 中网易企秀
 */
public class ImageController {

    private static String PATH = "/home/suibing/";

    private static final String FILE_PATH = "/home/suibing/url.txt";

    public static void main(String[] args) throws IOException {
        List<String> failList = new ArrayList<>();
        PATH = args[1];
        List<String> urlList = getUrlList(args[0]);
        urlList.forEach(url -> {
            try {
                String html = getHtmlResource(url);
                Set<String> set = getImgStr(html);
                download(set, title(html));
            }catch(Exception e) {
                failList.add(url);
                e.printStackTrace();
            }
        });
        failList.forEach(fail ->
                System.out.println("failed url : " + fail)
        );
        System.exit(0);
    }

    private static List<String> getUrlList(String filePath) throws IOException {
        List<String> result = new ArrayList<>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
        String line;
        while((line = bufferedReader.readLine()) != null) {
            result.add(line);
        }
        return result;
    }

    private static void download(Set<String> set, String name) {
        set.stream().forEach(file -> {
            if(!file.contains("http")) {
                return;
            }
            try {
                String suffix = suffix(file);
                new File(PATH + name).mkdir();
                File dir = new File(PATH + name + "/" + UUID.randomUUID() + "." + suffix);
                dir.createNewFile();

                URL imageDate = new URL(file);
                try(InputStream input = imageDate.openStream()) {
                    try(OutputStream output = new FileOutputStream(dir)) {
                        byte[] data = new byte[1024];
                        int len;
                        while((len = input.read(data)) != -1) {
                            output.write(data, 0, len);
                        }
                        output.flush();
                    }
                }
            }catch(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static String getHtmlResource(String url) {
        ArrayList<String> command = new ArrayList<>();
        //不显示google 浏览器
        command.add("--headless");
        Launcher launcher = new Launcher();
        try(SessionFactory factory = launcher.launch(command);
            Session session = factory.create()) {
            session.navigate(url);
            session.waitDocumentReady(50000);
            String content = session.getContent();
            return content;
        }
    }

    /**
     * 得到网页中图片的地址
     */
    public static Set<String> getImgStr(String htmlStr) {
        String compile = "<img\\s*src\\s*=\"(.*?)\"";
        Pattern pattern = Pattern.compile(compile);

        Set<String> pics = new HashSet<>();
        Matcher m = pattern.matcher(htmlStr);
        while(m.find()) {
            pics.add(m.group(1));
        }
        return pics;
    }

    private static String suffix(String url) {
        String compile = ".*\\.(png|jpg|jpeg|JPF|JPEG|gif|GIF){1}.*$";
        Pattern p = Pattern.compile(compile);
        Matcher m = p.matcher(url);
        if(m.matches()) {
            return m.group(1);
        }
        return "png";
    }

    private static String title(String html) {
        String compile = ".*<title>(.*)</title>.*";
        Pattern p = Pattern.compile(compile);
        Matcher m = p.matcher(html);
        if(m.find()) {
            return m.group(1);
        }
        return UUID.randomUUID().toString();
    }
}
