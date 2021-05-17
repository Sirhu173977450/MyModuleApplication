package cc.duduhuo.timelinerecyclerview.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/9/10.
 */

public class PhotoViewVO implements Serializable {

    private Integer width;
    private Integer height;
    private String imgurl;

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }
}
