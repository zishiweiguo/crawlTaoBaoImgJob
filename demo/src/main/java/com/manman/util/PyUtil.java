package com.manman.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

/**
 * Created by xuchao on 2018-2-26.
 */
public class PyUtil {

    private static Logger logger = LoggerFactory.getLogger(PyUtil.class);

    public static Boolean invokePy(String[] strs) throws Exception{
        Process process = Runtime.getRuntime().exec(strs);

        InputStreamReader ir = new InputStreamReader(process.getInputStream());
        LineNumberReader input = new LineNumberReader(ir);
        String line;
        while((line = input.readLine()) != null)
            logger.info(line);
        int status = process.waitFor();
        input.close();
        ir.close();

        return status == 0;
    }

}
