package com.shakethetree.configuration.dto;

/**
 * @author xubing
 * @description //TODO 设计说明
 * @date 19-10-26
 * @copyright 中网易企秀
 */
public class User {

    private Long uid;

    private String openid;

    private String name;

    private Boolean td;

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getTd() {
        return td;
    }

    public void setTd(Boolean td) {
        this.td = td;
    }
}
