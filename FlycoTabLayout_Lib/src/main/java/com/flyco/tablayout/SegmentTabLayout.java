package com.flyco.tablayout;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.tablayout.listener.OnTabSelectListener;
import com.flyco.tablayout.utils.DensityUtils;
import com.flyco.tablayout.utils.FragmentChangeManager;
import com.flyco.tablayout.utils.UnreadMsgUtils;
import com.flyco.tablayout.widget.MsgView;
import com.flyco.tablayout.widget.TabView;

import java.util.ArrayList;

/**
 * 分段Tab
 *
 * @author maple
 * @time 2019-07-03
 */
public class SegmentTabLayout extends FrameLayout implements ValueAnimator.AnimatorUpdateListener {
    private Context mContext;
    private String[] mTitles;
    private LinearLayout mTabsContainer;
    private int mCurrentTab;
    private int mLastTab;
    private int mTabCount;
    // 用于绘制显示器
    private TabRect mRect = new TabRect();

    public class TabRect {
        private int color;
        private int strokeColor;
        private float strokeWidth;

        GradientDrawable drawable = new GradientDrawable();
    }

    private float mTabPadding;
    private boolean mTabSpaceEqual;
    private float mTabWidth;

    private int mHeight;

    // indicator 指示器
    private TabIndicator mIndicator = new TabIndicator();

    public class TabIndicator {
        int color;
        float height;
        float cornerRadius;
        float marginLeft;
        float marginTop;
        float marginRight;
        float marginBottom;
        long animDuration;
        boolean animEnable;
        boolean bounceEnable;

        Rect rect = new Rect();
        GradientDrawable drawable = new GradientDrawable();
    }

    // divider
    private TabDivider mDivider = new TabDivider();

    public class TabDivider {
        int color;
        float width;
        float padding;

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    // title
    private static final int TEXT_BOLD_NONE = 0;
    private static final int TEXT_BOLD_WHEN_SELECT = 1;
    private static final int TEXT_BOLD_BOTH = 2;
    private float mTextSize;
    private int mTextSelectColor;
    private int mTextUnSelectColor;
    private int mTextBold;
    private boolean mTextAllCaps;

    // anim
    private ValueAnimator mValueAnimator;
    private OvershootInterpolator mInterpolator = new OvershootInterpolator(0.8f);

    private FragmentChangeManager mFragmentChangeManager;
    private float[] mRadiusArr = new float[8];

    public SegmentTabLayout(Context context) {
        this(context, null, 0);
    }

    public SegmentTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SegmentTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);//重写onDraw方法,需要调用这个方法来清除flag
        setClipChildren(false);
        setClipToPadding(false);

        this.mContext = context;
        mTabsContainer = new LinearLayout(context);
        addView(mTabsContainer);

        obtainAttributes(context, attrs);

        //get layout_height
        String height = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_height");

        //create ViewPager
        if (height.equals(ViewGroup.LayoutParams.MATCH_PARENT + "")) {
        } else if (height.equals(ViewGroup.LayoutParams.WRAP_CONTENT + "")) {
        } else {
            int[] systemAttrs = {android.R.attr.layout_height};
            TypedArray a = context.obtainStyledAttributes(attrs, systemAttrs);
            mHeight = a.getDimensionPixelSize(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            a.recycle();
        }

        mValueAnimator = ValueAnimator.ofObject(new PointEvaluator(), mLastP, mCurrentP);
        mValueAnimator.addUpdateListener(this);
    }

    private void obtainAttributes(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SegmentTabLayout);

        mIndicator.color = ta.getColor(R.styleable.SegmentTabLayout_tl_indicator_color, Color.parseColor("#222831"));
        mIndicator.height = ta.getDimension(R.styleable.SegmentTabLayout_tl_indicator_height, -1);
        mIndicator.cornerRadius = ta.getDimension(R.styleable.SegmentTabLayout_tl_indicator_corner_radius, -1);
        mIndicator.marginLeft = ta.getDimension(R.styleable.SegmentTabLayout_tl_indicator_margin_left, dp2px(0));
        mIndicator.marginTop = ta.getDimension(R.styleable.SegmentTabLayout_tl_indicator_margin_top, 0);
        mIndicator.marginRight = ta.getDimension(R.styleable.SegmentTabLayout_tl_indicator_margin_right, dp2px(0));
        mIndicator.marginBottom = ta.getDimension(R.styleable.SegmentTabLayout_tl_indicator_margin_bottom, 0);
        mIndicator.animEnable = ta.getBoolean(R.styleable.SegmentTabLayout_tl_indicator_anim_enable, false);
        mIndicator.bounceEnable = ta.getBoolean(R.styleable.SegmentTabLayout_tl_indicator_bounce_enable, true);
        mIndicator.animDuration = ta.getInt(R.styleable.SegmentTabLayout_tl_indicator_anim_duration, -1);

        mDivider.color = ta.getColor(R.styleable.SegmentTabLayout_tl_divider_color, mIndicator.color);
        mDivider.width = ta.getDimension(R.styleable.SegmentTabLayout_tl_divider_width, dp2px(1));
        mDivider.padding = ta.getDimension(R.styleable.SegmentTabLayout_tl_divider_padding, 0);

        mTextSize = ta.getDimension(R.styleable.SegmentTabLayout_tl_textsize, sp2px(13f));
        mTextSelectColor = ta.getColor(R.styleable.SegmentTabLayout_tl_textSelectColor, Color.parseColor("#ffffff"));
        mTextUnSelectColor = ta.getColor(R.styleable.SegmentTabLayout_tl_textUnselectColor, mIndicator.color);
        mTextBold = ta.getInt(R.styleable.SegmentTabLayout_tl_textBold, TEXT_BOLD_NONE);
        mTextAllCaps = ta.getBoolean(R.styleable.SegmentTabLayout_tl_textAllCaps, false);

        mTabSpaceEqual = ta.getBoolean(R.styleable.SegmentTabLayout_tl_tab_space_equal, true);
        mTabWidth = ta.getDimension(R.styleable.SegmentTabLayout_tl_tab_width, dp2px(-1));
        mTabPadding = ta.getDimension(R.styleable.SegmentTabLayout_tl_tab_padding, mTabSpaceEqual || mTabWidth > 0 ? dp2px(0) : dp2px(10));

        mRect.color = ta.getColor(R.styleable.SegmentTabLayout_tl_bar_color, Color.TRANSPARENT);
        mRect.strokeColor = ta.getColor(R.styleable.SegmentTabLayout_tl_bar_stroke_color, mIndicator.color);
        mRect.strokeWidth = ta.getDimension(R.styleable.SegmentTabLayout_tl_bar_stroke_width, dp2px(1));

        ta.recycle();
    }

    public void setTabData(String[] titles) {
        if (titles == null || titles.length == 0) {
            throw new IllegalStateException("Titles can not be NULL or EMPTY !");
        }

        this.mTitles = titles;

        notifyDataSetChanged();
    }

    /**
     * 关联数据支持同时切换fragments
     */
    public void setTabData(String[] titles, FragmentActivity fa, int containerViewId, ArrayList<Fragment> fragments) {
        mFragmentChangeManager = new FragmentChangeManager(fa.getSupportFragmentManager(), containerViewId, fragments);
        setTabData(titles);
    }

    /**
     * 更新数据
     */
    public void notifyDataSetChanged() {
        mTabsContainer.removeAllViews();
        this.mTabCount = mTitles.length;
        TabView tabView;
        for (int i = 0; i < mTabCount; i++) {
            tabView = new TabView(mContext);
            tabView.setTag(i);
            addTab(i, tabView);
        }

        updateTabStyles();
    }

    /**
     * 创建并添加tab
     */
    private void addTab(final int position, TabView tabView) {
        tabView.setTitle(mTitles[position]);
        tabView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (Integer) v.getTag();
                if (mCurrentTab != position) {
                    setCurrentTab(position);
                    if (mListener != null) {
                        mListener.onTabSelect(position);
                    }
                } else {
                    if (mListener != null) {
                        mListener.onTabReselect(position);
                    }
                }
            }
        });

        /** 每一个Tab的布局参数 */
        LinearLayout.LayoutParams lp_tab = mTabSpaceEqual ?
                new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f) :
                new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        if (mTabWidth > 0) {
            lp_tab = new LinearLayout.LayoutParams((int) mTabWidth, LayoutParams.MATCH_PARENT);
        }
        mTabsContainer.addView(tabView, position, lp_tab);
    }

    private void updateTabStyles() {
        for (int i = 0; i < mTabCount; i++) {
            TabView tabView = getTabView(i);
            tabView.setPadding((int) mTabPadding, 0, (int) mTabPadding, 0);
            tabView.tvTitle.setTextColor(i == mCurrentTab ? mTextSelectColor : mTextUnSelectColor);
            tabView.tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
            // tv_tab_title.setPadding((int) mTabPadding, 0, (int) mTabPadding, 0);
            if (mTextAllCaps) {
                tabView.tvTitle.setText(tabView.tvTitle.getText().toString().toUpperCase());
            }

            if (mTextBold == TEXT_BOLD_BOTH) {
                tabView.tvTitle.getPaint().setFakeBoldText(true);
            } else if (mTextBold == TEXT_BOLD_NONE) {
                tabView.tvTitle.getPaint().setFakeBoldText(false);
            }
        }
    }

    private void updateTabSelection(int position) {
        for (int i = 0; i < mTabCount; ++i) {
            TextView tvTitle = getTitleView(i);
            final boolean isSelect = i == position;
            tvTitle.setTextColor(isSelect ? mTextSelectColor : mTextUnSelectColor);
            if (mTextBold == TEXT_BOLD_WHEN_SELECT) {
                tvTitle.getPaint().setFakeBoldText(isSelect);
            }
        }
    }

    private void calcOffset() {
        final TabView currentTabView = getTabView(this.mCurrentTab);
        mCurrentP.left = currentTabView.getLeft();
        mCurrentP.right = currentTabView.getRight();

        final TabView lastTabView = getTabView(this.mLastTab);
        mLastP.left = lastTabView.getLeft();
        mLastP.right = lastTabView.getRight();

        if (mLastP.left == mCurrentP.left && mLastP.right == mCurrentP.right) {
            invalidate();
        } else {
            mValueAnimator.setObjectValues(mLastP, mCurrentP);
            if (mIndicator.bounceEnable) {
                mValueAnimator.setInterpolator(mInterpolator);
            }

            if (mIndicator.animDuration < 0) {
                mIndicator.animDuration = mIndicator.bounceEnable ? 500 : 250;
            }
            mValueAnimator.setDuration(mIndicator.animDuration);
            mValueAnimator.start();
        }
    }

    private void calcIndicatorRect() {
        TabView currentTabView = getTabView(this.mCurrentTab);

        mIndicator.rect.left = currentTabView.getLeft();
        mIndicator.rect.right = currentTabView.getRight();

        if (!mIndicator.animEnable) {
            if (mCurrentTab == 0) {
                setRadius(mIndicator.cornerRadius, 0);
            } else if (mCurrentTab == mTabCount - 1) {
                setRadius(0, mIndicator.cornerRadius);
            } else {
                setRadius(0, 0);
            }
        } else {
            setRadius(mIndicator.cornerRadius, mIndicator.cornerRadius);
        }
    }

    /**
     * The corners are ordered top-left, top-right, bottom-right, bottom-left
     */
    private void setRadius(float leftRadius, float rightRadius) {
        mRadiusArr[0] = leftRadius;
        mRadiusArr[1] = leftRadius;
        mRadiusArr[2] = rightRadius;
        mRadiusArr[3] = rightRadius;
        mRadiusArr[4] = rightRadius;
        mRadiusArr[5] = rightRadius;
        mRadiusArr[6] = leftRadius;
        mRadiusArr[7] = leftRadius;
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        IndicatorPoint p = (IndicatorPoint) animation.getAnimatedValue();
        mIndicator.rect.left = (int) p.left;
        mIndicator.rect.right = (int) p.right;
        invalidate();
    }

    private boolean mIsFirstDraw = true;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isInEditMode() || mTabCount <= 0) {
            return;
        }

        int height = getHeight();
        int paddingLeft = getPaddingLeft();

        if (mIndicator.height < 0) {
            mIndicator.height = height - mIndicator.marginTop - mIndicator.marginBottom;
        }

        if (mIndicator.cornerRadius < 0 || mIndicator.cornerRadius > mIndicator.height / 2) {
            mIndicator.cornerRadius = mIndicator.height / 2;
        }

        //draw mRect 填充色
        mRect.drawable.setColor(mRect.color);
        mRect.drawable.setStroke((int) mRect.strokeWidth, mRect.strokeColor);
        mRect.drawable.setCornerRadius(mIndicator.cornerRadius);
        mRect.drawable.setBounds(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
        mRect.drawable.draw(canvas);

        // draw divider 分割线
        if (!mIndicator.animEnable && mDivider.width > 0) {
            mDivider.paint.setStrokeWidth(mDivider.width);
            mDivider.paint.setColor(mDivider.color);
            for (int i = 0; i < mTabCount - 1; i++) {
                View tab = getTabView(i);
                canvas.drawLine(paddingLeft + tab.getRight(), mDivider.padding,
                        paddingLeft + tab.getRight(), height - mDivider.padding,
                        mDivider.paint);
            }
        }


        //draw indicator line
        if (mIndicator.animEnable) {
            if (mIsFirstDraw) {
                mIsFirstDraw = false;
                calcIndicatorRect();
            }
        } else {
            calcIndicatorRect();
        }

        mIndicator.drawable.setColor(mIndicator.color);
        mIndicator.drawable.setBounds(paddingLeft + (int) mIndicator.marginLeft + mIndicator.rect.left,
                (int) mIndicator.marginTop, (int) (paddingLeft + mIndicator.rect.right - mIndicator.marginRight),
                (int) (mIndicator.marginTop + mIndicator.height));
        mIndicator.drawable.setCornerRadii(mRadiusArr);
        mIndicator.drawable.draw(canvas);
    }

    //setter and getter
    public void setCurrentTab(int currentTab) {
        mLastTab = this.mCurrentTab;
        this.mCurrentTab = currentTab;
        updateTabSelection(currentTab);
        if (mFragmentChangeManager != null) {
            mFragmentChangeManager.setFragments(currentTab);
        }
        if (mIndicator.animEnable) {
            calcOffset();
        } else {
            invalidate();
        }
    }


    //setter and getter

    public void setTabPadding(float tabPadding) {
        this.mTabPadding = dp2px(tabPadding);
        updateTabStyles();
    }

    public void setTabSpaceEqual(boolean tabSpaceEqual) {
        this.mTabSpaceEqual = tabSpaceEqual;
        updateTabStyles();
    }

    public void setTabWidth(float tabWidth) {
        this.mTabWidth = dp2px(tabWidth);
        updateTabStyles();
    }

    public void setIndicatorColor(int indicatorColor) {
        this.mIndicator.color = indicatorColor;
        invalidate();
    }

    public void setIndicatorHeight(float indicatorHeight) {
        this.mIndicator.height = dp2px(indicatorHeight);
        invalidate();
    }

    public void setIndicatorCornerRadius(float indicatorCornerRadius) {
        this.mIndicator.cornerRadius = dp2px(indicatorCornerRadius);
        invalidate();
    }

    public void setIndicatorMargin(float indicatorMarginLeft, float indicatorMarginTop,
                                   float indicatorMarginRight, float indicatorMarginBottom) {
        this.mIndicator.marginLeft = dp2px(indicatorMarginLeft);
        this.mIndicator.marginTop = dp2px(indicatorMarginTop);
        this.mIndicator.marginRight = dp2px(indicatorMarginRight);
        this.mIndicator.marginBottom = dp2px(indicatorMarginBottom);
        invalidate();
    }

    public void setIndicatorAnimDuration(long indicatorAnimDuration) {
        this.mIndicator.animDuration = indicatorAnimDuration;
    }

    public void setIndicatorAnimEnable(boolean indicatorAnimEnable) {
        this.mIndicator.animEnable = indicatorAnimEnable;
    }

    public void setIndicatorBounceEnable(boolean indicatorBounceEnable) {
        this.mIndicator.bounceEnable = indicatorBounceEnable;
    }

    public void setDividerColor(int dividerColor) {
        this.mDivider.color = dividerColor;
        invalidate();
    }

    public void setDividerWidth(float dividerWidth) {
        this.mDivider.width = dp2px(dividerWidth);
        invalidate();
    }

    public void setDividerPadding(float dividerPadding) {
        this.mDivider.padding = dp2px(dividerPadding);
        invalidate();
    }

    public void setTextsize(float textsize) {
        this.mTextSize = sp2px(textsize);
        updateTabStyles();
    }

    public void setTextSelectColor(int textSelectColor) {
        this.mTextSelectColor = textSelectColor;
        updateTabStyles();
    }

    public void setTextUnselectColor(int textUnselectColor) {
        this.mTextUnSelectColor = textUnselectColor;
        updateTabStyles();
    }

    public void setTextBold(int textBold) {
        this.mTextBold = textBold;
        updateTabStyles();
    }

    public void setTextAllCaps(boolean textAllCaps) {
        this.mTextAllCaps = textAllCaps;
        updateTabStyles();
    }

    public int getTabCount() {
        return mTabCount;
    }

    public int getCurrentTab() {
        return mCurrentTab;
    }

    public float getTabPadding() {
        return mTabPadding;
    }

    public boolean isTabSpaceEqual() {
        return mTabSpaceEqual;
    }

    public float getTabWidth() {
        return mTabWidth;
    }

    public int getIndicatorColor() {
        return mIndicator.color;
    }

    public float getIndicatorHeight() {
        return mIndicator.height;
    }

    public float getIndicatorCornerRadius() {
        return mIndicator.cornerRadius;
    }

    public float getIndicatorMarginLeft() {
        return mIndicator.marginLeft;
    }

    public float getIndicatorMarginTop() {
        return mIndicator.marginTop;
    }

    public float getIndicatorMarginRight() {
        return mIndicator.marginRight;
    }

    public float getIndicatorMarginBottom() {
        return mIndicator.marginBottom;
    }

    public long getIndicatorAnimDuration() {
        return mIndicator.animDuration;
    }

    public boolean isIndicatorAnimEnable() {
        return mIndicator.animEnable;
    }

    public boolean isIndicatorBounceEnable() {
        return mIndicator.bounceEnable;
    }

    public int getDividerColor() {
        return mDivider.color;
    }

    public float getDividerWidth() {
        return mDivider.width;
    }

    public float getDividerPadding() {
        return mDivider.padding;
    }

    public float getTextsize() {
        return mTextSize;
    }

    public int getTextSelectColor() {
        return mTextSelectColor;
    }

    public int getTextUnselectColor() {
        return mTextUnSelectColor;
    }

    public int getTextBold() {
        return mTextBold;
    }

    public boolean isTextAllCaps() {
        return mTextAllCaps;
    }

    public TabView getTabView(int position) {
        if (position >= mTabCount) {
            position = mTabCount - 1;
        }
        return (TabView) mTabsContainer.getChildAt(position);
    }

    public TextView getTitleView(int position) {
        return getTabView(position).tvTitle;
    }

    public MsgView getMsgView(int position) {
        return getTabView(position).rtvMsgTip;
    }

    // show MsgTipView
    private Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private SparseArray<Boolean> mInitSetMap = new SparseArray<>();

    /**
     * 显示未读消息
     *
     * @param position 显示tab位置
     * @param num      num小于等于0显示红点,num大于0显示数字
     */
    public void showMsg(int position, int num) {
        if (position >= mTabCount) {
            position = mTabCount - 1;
        }

        MsgView tipView = getMsgView(position);
        if (tipView != null) {
            UnreadMsgUtils.show(tipView, num);

            if (mInitSetMap.get(position) != null && mInitSetMap.get(position)) {
                return;
            }

            setMsgMargin(position, 2, 2);

            mInitSetMap.put(position, true);
        }
    }

    /**
     * 显示未读红点
     *
     * @param position 显示tab位置
     */
    public void showDot(int position) {
        if (position >= mTabCount) {
            position = mTabCount - 1;
        }
        showMsg(position, 0);
    }

    public void hideMsg(int position) {
        if (position >= mTabCount) {
            position = mTabCount - 1;
        }

        MsgView tipView = getMsgView(position);
        if (tipView != null) {
            tipView.setVisibility(View.GONE);
        }
    }

    /**
     * 设置提示红点偏移,注意
     * 1.控件为固定高度:参照点为tab内容的右上角
     * 2.控件高度不固定(WRAP_CONTENT):参照点为tab内容的右上角,此时高度已是红点的最高显示范围,所以这时bottomPadding其实就是topPadding
     */
    public void setMsgMargin(int position, float leftPadding, float bottomPadding) {
        MsgView tipView = getMsgView(position);
        if (tipView != null) {
            mTextPaint.setTextSize(mTextSize);
            float textWidth = mTextPaint.measureText(getTitleView(position).getText().toString());
            float textHeight = mTextPaint.descent() - mTextPaint.ascent();
            MarginLayoutParams lp = (MarginLayoutParams) tipView.getLayoutParams();

            lp.leftMargin = dp2px(leftPadding);
            lp.topMargin = mHeight > 0 ? (int) (mHeight - textHeight) / 2 - dp2px(bottomPadding) : dp2px(bottomPadding);

            tipView.setLayoutParams(lp);
        }
    }


    private OnTabSelectListener mListener;

    public void setOnTabSelectListener(OnTabSelectListener listener) {
        this.mListener = listener;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putInt("mCurrentTab", mCurrentTab);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mCurrentTab = bundle.getInt("mCurrentTab");
            state = bundle.getParcelable("instanceState");
            if (mCurrentTab != 0 && mTabsContainer.getChildCount() > 0) {
                updateTabSelection(mCurrentTab);
            }
        }
        super.onRestoreInstanceState(state);
    }

    class IndicatorPoint {
        public float left;
        public float right;
    }

    private IndicatorPoint mCurrentP = new IndicatorPoint();
    private IndicatorPoint mLastP = new IndicatorPoint();

    class PointEvaluator implements TypeEvaluator<IndicatorPoint> {
        @Override
        public IndicatorPoint evaluate(float fraction, IndicatorPoint startValue, IndicatorPoint endValue) {
            float left = startValue.left + fraction * (endValue.left - startValue.left);
            float right = startValue.right + fraction * (endValue.right - startValue.right);
            IndicatorPoint point = new IndicatorPoint();
            point.left = left;
            point.right = right;
            return point;
        }
    }

    protected int dp2px(float dp) {
        return DensityUtils.dp2px(mContext, dp);
    }

    protected int sp2px(float sp) {
        return DensityUtils.sp2px(mContext, sp);
    }
}
