package com.example.un_ps;

public class RowItem {
    private int imageId;
    private String title;
    private String desc;
    private String info;
 
    public RowItem(int imageId, String title, String desc, String info) {
        this.imageId = imageId;
        this.title = title;
        this.desc = desc;
        this.info= info;
    }
    public int getImageId() {
        return imageId;
    }
    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getInfo() {
        return info;
    }
    public void setInfo(String info) {
        this.info = info;
    }
    @Override
    public String toString() {
        return title + "\n" + desc;
    }
}