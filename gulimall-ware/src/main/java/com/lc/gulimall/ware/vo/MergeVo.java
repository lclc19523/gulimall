package com.lc.gulimall.ware.vo;

import lombok.Data;

import java.util.List;

@Data
public class MergeVo {

//    purchaseId: 1, //整单id
//    items:[1,2,3,4] //合并项集合
    private Long purchaseId;
    private List<Long> items;

    public Long getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(Long purchaseId) {
        this.purchaseId = purchaseId;
    }

    public List<Long> getItems() {
        return items;
    }

    public void setItems(List<Long> items) {
        this.items = items;
    }
}
