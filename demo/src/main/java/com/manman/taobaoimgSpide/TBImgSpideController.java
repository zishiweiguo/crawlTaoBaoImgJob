package com.manman.taobaoimgSpide;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipOutputStream;

/**
 * Created by xuchao on 2018-3-20.
 */
@Controller
public class TBImgSpideController {

    private static Logger logger = LoggerFactory.getLogger(TBImgSpideController.class);

    @RequestMapping("/downloadImg")
    public void test(HttpServletResponse response, String url){
//        String url = "https://item.taobao.com/item.htm?spm=a230r.1.14.1.dccb42c3ao7Ra6&id=546746057907&ns=1&abbucket=7#detail,dankou";
//        url = "https://detail.tmall.com/item.htm?spm=a1z10.5-b.w4011-7838460923.51.6e836b91S33ota&id=40945642037&rn=1566a427ea5eadf2c9d50379c2a323c7&abbucket=1,test,tmall";

        //String filePath = "d:" + File.separator + "img";
        String filePath = File.separator + "server" + File.separator + "img";
        String[] urls = url.split(",");
        String filename;
        String goodsType;
        if(urls.length==1) {
            filename = UUID.randomUUID().toString();
            goodsType = "tb";
        }else if (urls.length == 2) {
            filename = urls[1];
            goodsType = "tb";
        }else{
            filename = urls[1];
            goodsType = urls[2];
        }
        Goods goods;
        if("tmall".equals(goodsType))
            goods = ImgSpider.tmallImgSpider(urls[0]);
        else
            goods = ImgSpider.tbImgSpider(urls[0]);

        String rootDir = filePath;
        //写文件
        filePath = filePath + File.separator + filename;
        String fileDirPath = filePath;
        String zhutuPath = filePath + File.separator + "zhutu";
        String skuPath = filePath + File.separator + "sku";
        String detailPath = filePath + File.separator + "detail";

        //创建文件的目录结构
        File files1 = new File(zhutuPath);
        File files2 = new File(skuPath);
        File files3 = new File(detailPath);
        if(!files1.exists()){// 判断文件夹是否存在，如果不存在就创建一个文件夹
            files1.mkdirs();
        }
        if(!files2.exists()){// 判断文件夹是否存在，如果不存在就创建一个文件夹
            files2.mkdirs();
        }
        if(!files3.exists()){// 判断文件夹是否存在，如果不存在就创建一个文件夹
            files3.mkdirs();
        }

        List<String> zhutus = goods.getZhutuList();
        List<String> skus = goods.getSkuList();
        List<String> details = goods.getDetailList();

        for(int i=0; i<zhutus.size();i++)
            ImgSpider.downloadImg(zhutuPath, (i+1) + ".jpg", zhutus.get(i));

        for(int i=0; i<skus.size();i++)
            ImgSpider.downloadImg(skuPath , (i+1) + ".jpg", skus.get(i));

        for(int i=0; i<details.size();i++)
            ImgSpider.downloadImg(detailPath , (i+1) + ".jpg", details.get(i));

        File f = new File(filePath);
        //zip压缩
        try{
            ZipUtil.zip(rootDir, f, new ZipOutputStream(new FileOutputStream(new File(filePath+".zip"))));
        }catch (Exception e1){
            e1.printStackTrace();
        }

        //压缩完成后删除文件夹
        deleteFile(f);

        String dir = fileDirPath + ".zip";
        try {
            response.setContentType("application/x-download");
            //设置响应头，控制浏览器下载该文件
            response.setHeader("content-disposition", "attachment;fileName=" + URLEncoder.encode( filename + ".zip", "UTF-8"));
            //读取要下载的文件，保存到文件输入流
            FileInputStream in = new FileInputStream(dir);
            //创建输出流
            OutputStream out = response.getOutputStream();
            //创建缓冲区
            byte buffer[] = new byte[1024];
            int len;
            //循环将输入流中的内容读取到缓冲区当中
            while ((len = in.read(buffer)) > 0) {
                //输出缓冲区的内容到浏览器，实现文件下载
                out.write(buffer, 0, len);
            }

            //关闭文件输入流
            in.close();
            //关闭输出流
            out.close();

            //下载完成后删除压缩文件
            File zipFile = new File(dir);
            zipFile.delete();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void deleteFile(File file){
        if(file.isFile())
            file.delete();
        else {
            File[] fs = file.listFiles();
            for(File f : fs){
                deleteFile(f);
            }
            if(file.listFiles().length ==0)
                file.delete();
        }
        logger.info(file.toString() + " 删除完成");
    }

    public static void main(String[] args) {
//        TBImgSpideController controller = new TBImgSpideController();
//        controller.test();
        File file = new File("d:" + File.separator + "img" + File.separator +"dankou.zip");
        deleteFile(file);
    }

}
