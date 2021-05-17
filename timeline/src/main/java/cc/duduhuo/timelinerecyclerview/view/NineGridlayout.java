package cc.duduhuo.timelinerecyclerview.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import cc.duduhuo.timelinerecyclerview.R;
import cc.duduhuo.timelinerecyclerview.model.ExtListBean;
import cc.duduhuo.timelinerecyclerview.model.PhotoViewVO;


/**
 * Created by Pan_ on 2015/2/2.
 */
public class NineGridlayout extends ViewGroup {

    /**
     * 图片之间的间隔
     */
    private int gap = 5;
    private int columns;//
    private int rows;//
    private List<ExtListBean> listData;
    private int totalWidth;
    private Context mContext;

    List<PhotoViewVO> list = new ArrayList<PhotoViewVO>();

    public NineGridlayout(Context context) {
        this(context,null);
    }

    public NineGridlayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        totalWidth=getScreenWidth()-dip2px(60);
    }

    public int getScreenWidth() {
        return mContext.getResources().getDisplayMetrics().widthPixels;
    }

    public int dip2px(int i) {
        return (int) (0.5D + (double) (getDensity(mContext) * (float) i));
    }

    public float getDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }
    private void layoutChildrenView(){
        int childrenCount = listData.size();

        int singleWidth = (totalWidth - gap * (3 - 1)) / 4;
        int singleHeight = singleWidth;

        //根据子view数量确定高度
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = singleHeight * rows + gap * (rows - 1);
        setLayoutParams(params);
        if (childrenCount == 1) {
            ImageView childrenView = (ImageView) getChildAt(0);
            if (!TextUtils.isEmpty(listData.get(0).getImgurl())) {
                System.out.println("Uri________1"+listData.get(0).getImgurl());
                Glide.with(mContext).load(listData.get(0).getImgurl()).into(childrenView);
            }
            childrenView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onCheckDetails(listData.get(0));
                    }
                }
            });
            int[] position = findPosition(1);
            int left = (singleWidth + gap) * position[1];
            int top = (singleHeight + gap) * position[0];
            int right = left + singleWidth;
            int bottom = top + singleHeight;
            childrenView.layout(left, top, right, bottom);

        }else {
            int index = 0;
            for (int i = 0; i < childrenCount; i++) {
                ImageView childrenView = (ImageView) getChildAt(i);
                if (!TextUtils.isEmpty(listData.get(i).getImgurl())) {
                    Glide.with(mContext).load(listData.get(i).getImgurl()).into(childrenView);
                }
                index = i;
                childrenView.setTag(R.id.tag_first, index);
                System.out.println("position:_"+i);
                childrenView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (listener != null) {
                            listener.onCheckDetails(listData.get(R.id.tag_first));
                        }
                    }
                });
                int[] position = findPosition(i);
                int left = (singleWidth + gap) * position[1];
                int top = (singleHeight + gap) * position[0];
                int right = left + singleWidth;
                int bottom = top + singleHeight;
                childrenView.layout(left, top, right, bottom);
                index++;
            }
        }
    }



    private int[] findPosition(int childNum) {
        int[] position = new int[2];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if ((i * columns + j) == childNum) {
                    position[0] = i;//行
                    position[1] = j;//列
                    break;
                }
            }
        }
        return position;
    }

    public int getGap() {
        return gap;
    }

    public void setGap(int gap) {
        this.gap = gap;
    }


    public void setImagesData(List<ExtListBean> lists) {
        if (lists == null || lists.isEmpty()) {
            return;
        }
        //初始化布局
        generateChildrenLayout(lists.size());
        //这里做一个重用view的处理
        if (listData == null) {
            int i = 0;
            while (i < lists.size()) {
                ImageView iv = new ImageView(mContext);
                iv.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (listener != null) {
                            listener.onCheckDetails(listData.get(0));
                        }
                    }
                });
                addView(iv,generateDefaultLayoutParams());
                i++;
            }
        } else {
            int oldViewCount = listData.size();
            int newViewCount = lists.size();
            if (oldViewCount > newViewCount) {
                removeViews(newViewCount - 1, oldViewCount - newViewCount);
            } else if (oldViewCount < newViewCount) {
                for (int i = 0; i < newViewCount - oldViewCount; i++) {
                    ImageView iv = new ImageView(mContext);
                    addView(iv,generateDefaultLayoutParams());
                }
            }
        }
        listData = lists;
        layoutChildrenView();
    }


    /**
     * 根据图片个数确定行列数量
     * 对应关系如下
     * num	row	column
     * 1	   1	1
     * 2	   1	2
     * 3	   1	3
     * 4	   2	2
     * 5	   2	3
     * 6	   2	3
     * 7	   3	3
     * 8	   3	3
     * 9	   3	3
     *
     * @param length
     */
    private void generateChildrenLayout(int length) {
        if (length <= 3) {
            rows = 1;
            columns = length;
        } else if (length <= 6) {
            rows = 2;
            columns = 3;
            if (length == 4) {
                columns = 2;
            }
        } else {
            rows = 3;
            columns = 3;
        }
    }

    public OnIamgeClickedListener listener;

    public void setImageClickedListener(OnIamgeClickedListener l){
        listener=l;
    }

    public  interface OnIamgeClickedListener {
        void onCheckDetails(ExtListBean bean);
    }
}
