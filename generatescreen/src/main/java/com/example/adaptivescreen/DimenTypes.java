package com.example.adaptivescreen;


public enum DimenTypes {

    //适配Android 3.2以上   大部分手机的sw值集中在  300-460之间
    //300;320;340;360;400;480;520;600;720
    DP_sw__320(320),// values-sw300
    DP_sw__330(330),
    DP_sw__340(340),
    DP_sw__360(360),
    DP_sw__400(400),
    DP_sw__480(480),
    DP_sw__520(520),
    DP_sw__600(600),
    DP_sw__720(720);
    // 想生成多少自己以此类推


    /**
     * 屏幕最小宽度
     */
    private int swWidthDp;


    DimenTypes(int swWidthDp) {

        this.swWidthDp = swWidthDp;
    }

    public int getSwWidthDp() {
        return swWidthDp;
    }

    public void setSwWidthDp(int swWidthDp) {
        this.swWidthDp = swWidthDp;
    }

}
