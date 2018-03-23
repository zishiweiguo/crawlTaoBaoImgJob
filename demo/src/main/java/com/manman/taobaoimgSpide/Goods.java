package com.manman.taobaoimgSpide;

import java.util.List;

/**
 * Created by mm on 2018/3/20.
 */
public class Goods {

    private String title;
    private List<String> zhutuList;
    private List<String> skuList;
    private List<String> detailList;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getZhutuList() {
        return zhutuList;
    }

    public void setZhutuList(List<String> zhutuList) {
        this.zhutuList = zhutuList;
    }

    public List<String> getSkuList() {
        return skuList;
    }

    public void setSkuList(List<String> skuList) {
        this.skuList = skuList;
    }

    public List<String> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<String> detailList) {
        this.detailList = detailList;
    }
}
