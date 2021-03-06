package cc.duduhuo.timelinerecyclerview.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

import java.util.List;
import cc.duduhuo.timelinerecyclerview.R;
import cc.duduhuo.timelinerecyclerview.model.ExtListBean;


/**
 * @ClassName MultiImageView.java
 * @author shoyu
 * @version 
 * @Description: 显示1~N张图片的View
 */

public class MultiImageView extends LinearLayout {
	public static int MAX_WIDTH = 0;

	// 照片的Url列表
	private List<ExtListBean> mData;
	private List<String> mData1;

	/** 长度 单位为Pixel **/
	private int pxOneMaxWandH;  // 单张图最大允许宽高
	private int pxMoreWandH = 0;// 多张图的宽高
	private int pxImagePadding = dip2px(getContext(), 3);// 图片间的间距

	private int MAX_PER_ROW_COUNT = 3;// 每行显示最大数

	private LayoutParams onePicPara;
	private LayoutParams morePara, moreParaColumnFirst;
	private LayoutParams rowPara;

	private OnItemClickListener mOnItemClickListener;
	public void setOnItemClickListener(OnItemClickListener onItemClickListener){
		mOnItemClickListener = onItemClickListener;
	}

	public MultiImageView(Context context) {
		super(context);
	}

	public MultiImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setList(List<String> lists) throws IllegalArgumentException {
		if(lists==null){
			throw new IllegalArgumentException("imageList is null...");
		}
		mData1 = lists;

		if(MAX_WIDTH > 0){
			pxMoreWandH = (MAX_WIDTH - pxImagePadding*2 )/3; //解决右侧图片和内容对不齐问题
			pxOneMaxWandH = MAX_WIDTH * 2 / 3;
			initImageLayoutParams();
		}
		initView();
	}

	public void setList2(List<ExtListBean> lists) throws IllegalArgumentException {
		if(lists==null){
			throw new IllegalArgumentException("imageList is null...");
		}
		mData = lists;

		if(MAX_WIDTH > 0){
			pxMoreWandH = (MAX_WIDTH - pxImagePadding*2 )/3; //解决右侧图片和内容对不齐问题
			pxOneMaxWandH = MAX_WIDTH * 2 / 3;
			initImageLayoutParams();
		}
		initView2();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if(MAX_WIDTH == 0){
			int width = measureWidth(widthMeasureSpec);
			if(width>0){
				MAX_WIDTH = width;
				if(mData1!=null && mData1.size()>0){
					setList(mData1);
				}
				if(mData!=null && mData.size()>0){
					setList2(mData);
				}
			}
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	/**
	 * Determines the width of this view
	 * 
	 * @param measureSpec
	 *            A measureSpec packed into an int
	 * @return The width of the view, honoring constraints from measureSpec
	 */
	private int measureWidth(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be
			result = specSize;
		} else {
			// Measure the ProductIntroduceListBean
			// result = (int) mTextPaint.measureText(mText) + getPaddingLeft()
			// + getPaddingRight();
			if (specMode == MeasureSpec.AT_MOST) {
				// Respect AT_MOST value if that was what is called for by
				// measureSpec
				result = Math.min(result, specSize);
			}
		}
		return result;
	}

	private void initImageLayoutParams() {
		int wrap = LayoutParams.WRAP_CONTENT;
		int match = LayoutParams.MATCH_PARENT;

		onePicPara = new LayoutParams(wrap, wrap);

		moreParaColumnFirst = new LayoutParams(pxMoreWandH, pxMoreWandH);
		morePara = new LayoutParams(pxMoreWandH, pxMoreWandH);
		morePara.setMargins(pxImagePadding, 0, 0, 0);

		rowPara = new LayoutParams(match, wrap);
	}


	// 根据imageView的数量初始化不同的View布局,还要为每一个View作点击效果
	private void initView() {
		this.setOrientation(VERTICAL);
		this.removeAllViews();
		if(MAX_WIDTH == 0){
			//为了触发onMeasure()来测量MultiImageView的最大宽度，MultiImageView的宽设置为match_parent
			addView(new View(getContext()));
			return;
		}

		if (mData1 == null || mData1.size() == 0) {
			return;
		}

		if (mData1.size() == 1) {
			addView(createImageView(0, false));
		} else {
			int allCount = mData1.size();
			if(allCount == 4){
				MAX_PER_ROW_COUNT = 2;
			}else{
				MAX_PER_ROW_COUNT = 3;
			}
			int rowCount = allCount / MAX_PER_ROW_COUNT
					+ (allCount % MAX_PER_ROW_COUNT > 0 ? 1 : 0);// 行数
			for (int rowCursor = 0; rowCursor < rowCount; rowCursor++) {
				LinearLayout rowLayout = new LinearLayout(getContext());
				rowLayout.setOrientation(LinearLayout.HORIZONTAL);

				rowLayout.setLayoutParams(rowPara);
				if (rowCursor != 0) {
					rowLayout.setPadding(0, pxImagePadding, 0, 0);
				}

				int columnCount = allCount % MAX_PER_ROW_COUNT == 0 ? MAX_PER_ROW_COUNT
						: allCount % MAX_PER_ROW_COUNT;//每行的列数
				if (rowCursor != rowCount - 1) {
					columnCount = MAX_PER_ROW_COUNT;
				}
				addView(rowLayout);

				int rowOffset = rowCursor * MAX_PER_ROW_COUNT;// 行偏移
				for (int columnCursor = 0; columnCursor < columnCount; columnCursor++) {
					int position = columnCursor + rowOffset;
					rowLayout.addView(createImageView(position, true));
				}
			}
		}
	}

	// 根据imageView的数量初始化不同的View布局,还要为每一个View作点击效果
	private void initView2() {
		this.setOrientation(VERTICAL);
		this.removeAllViews();
		if(MAX_WIDTH == 0){
			//为了触发onMeasure()来测量MultiImageView的最大宽度，MultiImageView的宽设置为match_parent
			addView(new View(getContext()));
			return;
		}

		if (mData == null || mData.size() == 0) {
			return;
		}

		if (mData.size() == 1) {
			addView(createImageView2(0, false));
		} else {
			int allCount = mData.size();
			if(allCount == 4){
				MAX_PER_ROW_COUNT = 2;
			}else{
				MAX_PER_ROW_COUNT = 3;
			}
			int rowCount = allCount / MAX_PER_ROW_COUNT
					+ (allCount % MAX_PER_ROW_COUNT > 0 ? 1 : 0);// 行数
			for (int rowCursor = 0; rowCursor < rowCount; rowCursor++) {
				LinearLayout rowLayout = new LinearLayout(getContext());
				rowLayout.setOrientation(LinearLayout.HORIZONTAL);

				rowLayout.setLayoutParams(rowPara);
				if (rowCursor != 0) {
					rowLayout.setPadding(0, pxImagePadding, 0, 0);
				}

				int columnCount = allCount % MAX_PER_ROW_COUNT == 0 ? MAX_PER_ROW_COUNT
						: allCount % MAX_PER_ROW_COUNT;//每行的列数
				if (rowCursor != rowCount - 1) {
					columnCount = MAX_PER_ROW_COUNT;
				}
				addView(rowLayout);

				int rowOffset = rowCursor * MAX_PER_ROW_COUNT;// 行偏移
				for (int columnCursor = 0; columnCursor < columnCount; columnCursor++) {
					int position = columnCursor + rowOffset;
					rowLayout.addView(createImageView2(position, true));
				}
			}
		}
	}

	private ImageView createImageView(int position, final boolean isMultiImage) {
		String uri = mData1.get(position);
		ImageView imageView = new ColorFilterImageView(getContext());
		if(isMultiImage){
			imageView.setScaleType(ScaleType.CENTER_CROP);
			imageView.setLayoutParams(position % MAX_PER_ROW_COUNT == 0 ?moreParaColumnFirst : morePara);
		}else {
			imageView.setAdjustViewBounds(true);
			imageView.setScaleType(ScaleType.CENTER_INSIDE);

			int expectW = 100;
			int expectH = 100;

			if(expectW == 0 || expectH == 0){
				imageView.setLayoutParams(onePicPara);
			}else{
				int actualW = 0;
				int actualH = 0;
				float scale = ((float) expectH)/((float) expectW);
				if(expectW > pxOneMaxWandH){
					actualW = pxOneMaxWandH;
					actualH = (int)(actualW * scale);
				} else if(expectW < pxMoreWandH){
					actualW = pxMoreWandH;
					actualH = (int)(actualW * scale);
				}else{
					actualW = expectW;
					actualH = expectH;
				}
				imageView.setLayoutParams(new LayoutParams(actualW, actualH));
			}
		}

		imageView.setId(uri.hashCode());
		imageView.setOnClickListener(new ImageOnClickListener(position));
		imageView.setBackgroundColor(getResources().getColor(R.color.white));
		//TODO:加载图片
		Glide.with(getContext()).load(uri).into(imageView);
		return imageView;
	}


	private ImageView createImageView2(int position, final boolean isMultiImage) {
		ExtListBean productProgress = mData.get(position);
		ImageView imageView = new ColorFilterImageView(getContext());
		if(isMultiImage){
			imageView.setScaleType(ScaleType.CENTER_CROP);
			imageView.setLayoutParams(position % MAX_PER_ROW_COUNT == 0 ?moreParaColumnFirst : morePara);
		}else {
			imageView.setAdjustViewBounds(true);
			imageView.setScaleType(ScaleType.CENTER_INSIDE);

			int expectW = productProgress.mWidth == 0 ? 30:30;
			int expectH = productProgress.mHeight == 0 ? 30:30;

			if(expectW == 0 || expectH == 0){
				imageView.setLayoutParams(onePicPara);
			}else{
				int actualW = 0;
				int actualH = 0;
				float scale = ((float) expectH)/((float) expectW);
				if(expectW > pxOneMaxWandH){
					actualW = pxOneMaxWandH;
					actualH = (int)(actualW * scale);
				} else if(expectW < pxMoreWandH){
					actualW = pxMoreWandH;
					actualH = (int)(actualW * scale);
				}else{
					actualW = expectW;
					actualH = expectH;
				}
				imageView.setLayoutParams(new LayoutParams(actualW, actualH));
			}
		}

		imageView.setId(productProgress.mImgPathList.get(position).hashCode());
		imageView.setOnClickListener(new ImageOnClickListener(position));
		imageView.setBackgroundColor(getResources().getColor(R.color.white));
		//TODO:加载图片
		Glide.with(getContext()).load(productProgress.mImgPathList.get(position)).into(imageView);
		return imageView;
	}

	private class ImageOnClickListener implements OnClickListener {

		private int position;
		public ImageOnClickListener(int position){
			this.position = position;
		}

		@Override
		public void onClick(View view) {
			if(mOnItemClickListener != null){
				mOnItemClickListener.onItemClick(view, position);
			}
		}
	}

	public interface OnItemClickListener{
		 void onItemClick(View view, int position);
	}

	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
}