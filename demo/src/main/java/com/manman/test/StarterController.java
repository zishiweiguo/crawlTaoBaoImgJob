package com.manman.test;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by xuchao on 2018-3-7.
 */
@Controller
public class StarterController {

    @RequestMapping("/")
    public String toStater(){
        return "/starter";
    }
}
