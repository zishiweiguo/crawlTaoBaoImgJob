package com.manman.test;

import com.manman.util.PyUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

/**
 * Created by xuchao on 2018-2-24.
 */
@Controller
//@SpringBootApplication
public class SpideImgController {

    @RequestMapping("/index")
    public String test(){
        return "index";
    }

    @RequestMapping("/downloadImg")
    public void downLoadImg(HttpServletRequest request, HttpServletResponse response, String url){
        String[] args = new String[]{"python", "/server/python/SpideImgTest.py", url};  //d:/python/test/SpideImgTest.py
        try {
            boolean isSuccess = PyUtil.invokePy(args);
            if (!isSuccess){
                //调用python成功并执行完成
                System.out.println("python 执行完成");
                String[] urls = url.split(",");
                String dir = "/server/img/" + urls[1] + ".zip"; //d:/img/
                //设置响应头，控制浏览器下载该文件
                response.setHeader("content-disposition", "attachment;fileName=" + URLEncoder.encode(urls[1] + ".zip","UTF-8"));
                //读取要下载的文件，保存到文件输入流
                FileInputStream in = new FileInputStream(dir);
                //创建输出流
                OutputStream out = response.getOutputStream();
                //创建缓冲区
                byte buffer[] = new byte[1024];
                int len = 0;
                //循环将输入流中的内容读取到缓冲区当中
                while((len=in.read(buffer))>0){
                    //输出缓冲区的内容到浏览器，实现文件下载
                    out.write(buffer, 0, len);
                }
                //关闭文件输入流
                in.close();
                //关闭输出流
                out.close();

                //下载完成后删除压缩文件
                File file = new File(dir);

                if (file.delete()){
                    System.out.println(dir + " 删除成功");
                }
            }
        }catch (IOException e){
            System.out.println("调用python发生异常 : " + e.getMessage());
        }
    }


}
