package com.lc.gulimall.ware.to;

import lombok.Data;

@Data
public class skuNameTo {

    private Long skuId;
    private String skuName;

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }
}
