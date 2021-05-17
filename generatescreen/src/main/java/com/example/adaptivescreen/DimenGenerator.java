package com.example.adaptivescreen;

public class DimenGenerator {

	 /**
     * 设计稿尺寸(根据自己设计师的设计稿的宽度填入)
     */
    private static final int DESIGN_WIDTH = 375;

    /**
     * 设计稿高度  没用到
     */
    private static final int DESIGN_HEIGHT = 667;

    private static final String XML_DIMEN_TEMPLETE = "<dimen name=\"dp%1$s%2$d\">%3$.2fdp</dimen>\r\n";
    public static void main(String[] args) {

        DimenTypes[] values = DimenTypes.values();
        for (DimenTypes value : values) {
            MakeUtils.makeAll(DESIGN_WIDTH, value, "E:/Project/MyApplication_Module/adaptivescreen");
        }

    }

    //打印基本的dimen
    public static void px2dip() {
        String temp = "";
        for (int i = 0; i < 721; i++) {
            temp = String.format(XML_DIMEN_TEMPLETE,"", i, Float.valueOf(i));
            System.out.print(temp);
        }
    }
}
