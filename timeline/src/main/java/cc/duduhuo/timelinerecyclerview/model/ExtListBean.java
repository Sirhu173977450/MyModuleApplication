package cc.duduhuo.timelinerecyclerview.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/9/10.
 */

public class ExtListBean implements Serializable {

    private int width;
    private int height;
    private String imgurl;
    public List<String> mImgPathList;
    public int mWidth;
    public int mHeight;

    public ExtListBean(String url, int width, int height,List<String> list) {
        this.imgurl = url;
        this.width = width;
        this.height = height;
        this.mImgPathList = list;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }
}
