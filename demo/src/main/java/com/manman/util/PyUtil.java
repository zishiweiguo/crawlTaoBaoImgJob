package com.manman.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

/**
 * Created by xuchao on 2018-2-26.
 */
public class PyUtil {

    public static Boolean invokePy(String[] strs) throws IOException{
        String result;
        System.out.println("url:" + strs[2]);
        Process process = Runtime.getRuntime().exec(strs);

        InputStreamReader ir = new InputStreamReader(process.getInputStream());
        LineNumberReader input = new LineNumberReader(ir);
        result = input.readLine();
        System.out.println(result);
        input.close();
        ir.close();

        return "ok".equals(result);
    }

}
