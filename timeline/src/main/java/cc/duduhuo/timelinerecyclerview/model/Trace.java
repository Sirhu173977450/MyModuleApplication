package cc.duduhuo.timelinerecyclerview.model;

import java.util.List;

/**
 * =======================================================
 * 版权：Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2016/11/22 16:58
 * 版本：1.0
 * 描述：物流信息类
 * 备注：
 * =======================================================
 */
public class Trace {
    /** 时间 */
    private String acceptTime;
    /** 描述 */
    private String acceptStation;

    public List<String> mImgPathList;

    public Trace(String acceptTime, String acceptStation,List<String> list) {
        this.acceptTime = acceptTime;
        this.acceptStation = acceptStation;
        this.mImgPathList = list;
    }

    public String getAcceptTime() {
        return acceptTime;
    }

    public void setAcceptTime(String acceptTime) {
        this.acceptTime = acceptTime;
    }

    public String getAcceptStation() {
        return acceptStation;
    }

    public void setAcceptStation(String acceptStation) {
        this.acceptStation = acceptStation;
    }

    public List<String> getmImgPathList() {
        return mImgPathList;
    }

    public void setmImgPathList(List<String> mImgPathList) {
        this.mImgPathList = mImgPathList;
    }
}
