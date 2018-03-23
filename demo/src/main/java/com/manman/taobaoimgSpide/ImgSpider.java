package com.manman.taobaoimgSpide;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xuchao on 2018-3-20.
 */
public class ImgSpider {


    public static Goods tbImgSpider(String url){

        Goods goods = new Goods();
        Connection conn = Jsoup.connect(url);
        try{
            Document doc = conn.get();

            Element tb_item_info_r = doc.getElementsByClass("tb-item-info-r").first();

            // 主图
            Element J_UlThumb = doc.getElementById("J_UlThumb");

            Elements imgTags = J_UlThumb.getElementsByTag("img");
            List<String> zhutuUrls = new ArrayList<>();

            for(Element e : imgTags){
                zhutuUrls.add("http:" + e.attr("data-src").replace("50x50", "800x800"));
            }

            Element tb_main_title = tb_item_info_r.getElementsByClass("tb-main-title").first();
            //宝贝标题
            String title = tb_main_title.attr("data-title");

            //sku图
            Element J_Prop_Color = tb_item_info_r.getElementsByClass("J_Prop_Color").first();

            Elements aTags = J_Prop_Color.getElementsByTag("a");

            List<String> skuUrls = new ArrayList<>();
            for (Element a : aTags){
                String style = a.attr("style");
                skuUrls.add("http:" + style.substring(style.indexOf("(") + 1, style.indexOf(")")).replace("30x30", "800x800"));
            }

            //详情图
            List<String> descUrls = new ArrayList<>();
            Elements scripts =doc.getElementsByTag("script");

            for(Element e : scripts){
                if (e.html().contains("g_config =")){
                    String data = e.data();
                    String descUrlData = data.substring(data.indexOf("descUrl")+1, data.indexOf("counterApi"));
                    String descUrl = "http:" + descUrlData.substring(descUrlData.indexOf("//"), descUrlData.indexOf("' :"));
                    Connection descConn = Jsoup.connect(descUrl);
                    Document d = descConn.get();
                    Elements descTags = d.getElementsByTag("img");
                    for (Element img: descTags){
                        descUrls.add(img.attr("src"));
                    }
                }
            }

            goods.setTitle(title);
            goods.setZhutuList(zhutuUrls);
            goods.setSkuList(skuUrls);
            goods.setDetailList(descUrls);
        }catch (IOException e){
            e.printStackTrace();
        }

        return goods;
    }

    public static Goods tmallImgSpider(String url){

        Goods goods = new Goods();
        Connection conn = Jsoup.connect(url);
        try{
            Document doc = conn.get();

            // 主图
            Element J_UlThumb = doc.getElementById("J_UlThumb");

            Elements imgTags = J_UlThumb.getElementsByTag("img");
            List<String> zhutuUrls = new ArrayList<>();

            for(Element e : imgTags){
                zhutuUrls.add("http:" + e.attr("src").replace("60x60", "800x800"));
            }

            Element tb_detail_hd = doc.getElementsByClass("tb-detail-hd").first();
            Element h1 = tb_detail_hd.getElementsByTag("h1").first();
            //宝贝标题
            String title = h1.text();

            //sku图
            Element tb_img = doc.getElementsByClass("tb-img").first();

            Elements aTags = tb_img.getElementsByTag("a");

            List<String> skuUrls = new ArrayList<>();
            for (Element a : aTags){
                String style = a.attr("style");
                skuUrls.add("http:" + style.substring(style.lastIndexOf("(") + 1, style.lastIndexOf(")")).replace("40x40", "800x800"));
            }

            //详情图
            List<String> descUrls = new ArrayList<>();
            Elements scripts =doc.getElementsByTag("script");

            for(Element e : scripts){
                if (e.html().contains("TShop.Setup")){
                    String data = e.data();
                    String descUrlData = data.substring(data.indexOf("\"descUrl\":\"")+11, data.indexOf("\",\"fetchDcUrl\""));
                    String descUrl = "http:" + descUrlData;
                    Connection descConn = Jsoup.connect(descUrl);
                    Document d = descConn.get();
                    Element p = d.getElementsByTag("p").first();
                    Elements descTags = p.getElementsByTag("img");
                    for (Element img: descTags){
                        descUrls.add(img.attr("src"));
                    }
                }
            }

            goods.setTitle(title);
            goods.setZhutuList(zhutuUrls);
            goods.setSkuList(skuUrls);
            goods.setDetailList(descUrls);
        }catch (IOException e){
            e.printStackTrace();
        }

        return goods;
    }

    public static void downloadImg(String filePath, String filename, String imgUrl){
        System.out.println("正在下载的图片地址: " + imgUrl);

        filePath += File.separator + filename;

        try {
            URL url = new URL(imgUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream is = connection.getInputStream();
            // 创建文件
            File file = new File(filePath);
            FileOutputStream out = new FileOutputStream(file);
            int i = 0;
            while((i = is.read()) != -1){
                out.write(i);
            }
            is.close();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
