package cc.duduhuo.timelinerecyclerview.adapter;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.blankj.utilcode.util.SizeUtils;
import java.util.List;
import cc.duduhuo.timelinerecyclerview.R;
import cc.duduhuo.timelinerecyclerview.base.BaseRecyclerAdapter;
import cc.duduhuo.timelinerecyclerview.base.BaseRecyclerHolder;
import cc.duduhuo.timelinerecyclerview.model.Trace;
import cc.duduhuo.timelinerecyclerview.view.MultiImageView;


/**
 * Created by YanShangGroup on 2017/9/15.
 */

public class ProjectProgressAdapter extends BaseRecyclerAdapter<Trace> {

    private Context mContext;

    public ProjectProgressAdapter(Context context, List<Trace> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
        this.mContext = context;
    }

    @Override
    public void convert(BaseRecyclerHolder holder, Trace bean, int position, boolean isScrolling) {
        MultiImageView imagePanel = holder.getView(R.id.imagePanel);
        TextView tvAcceptTime = holder.getView(R.id.tvAcceptTime);
        TextView tvAcceptStation = holder.getView(R.id.tvAcceptStation);
        if (bean != null) {
            // 第一行头的竖线不显示
            if (position == 0) {
                holder.getView(R.id.tvTopLine).setVisibility(View.INVISIBLE);
            } else {
                holder.getView(R.id.tvTopLine).setVisibility(View.VISIBLE);
            }
            tvAcceptTime.setText(bean.getAcceptTime());
            tvAcceptStation.setText(bean.getAcceptStation());

//            if (position == list.size() - 1) {
//                LinearLayout itemView = (LinearLayout) holder.itemView;
//                itemView.setPadding(SizeUtils.dp2px(15), SizeUtils.dp2px(0), SizeUtils.dp2px(15),
//                        SizeUtils.dp2px(15));
//            }

            imagePanel.setList(bean.mImgPathList);
            imagePanel.setOnItemClickListener(new MultiImageView.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
//                    Intent intent = new Intent(mContext, PhotoViewActivity.class);
//                    intent.putExtra("photo_list", (Serializable) bean.mImgPathList);
//                    intent.putExtra("selectIndex", position);
//                    mContext.startActivity(intent);
                }
            });
        }
    }
}
