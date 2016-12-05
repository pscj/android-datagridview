package net.panatrip.datagridview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import net.panatrip.datagridview.interfaces.DataGridListener;
import net.panatrip.datagridview.interfaces.ScrollViewListener;
import net.panatrip.datagridview.view.CustomDataGridView;
import net.panatrip.datagridview.view.HeaderView;
import net.panatrip.datagridview.view.MyHorizontalScrollView;
import net.panatrip.datagridview.view.MyScrollView;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by pscj on 2016/11/24.
 */

public class DataGridView extends LinearLayout {
    private MyHorizontalScrollView hScrollView;
    private MyScrollView scrollView;
    private CustomDataGridView dataView;
    private HeaderView headerView;
    private InnerListener listener = new InnerListener();
    public DataGridView(Context context) {
        super(context);
        init(context);
    }

    public DataGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DataGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public DataGridView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context){
        View.inflate(context, R.layout.datagrid_view, this);
        hScrollView = (MyHorizontalScrollView)findViewById(R.id.hScrollView);
        hScrollView.setScrollViewListener(new ScrollViewListener() {
            @Override
            public void onScrollChanged(View scrollView, int x, int y, int oldx, int oldy) {
                dataView.invalidate();
                headerView.invalidate();
            }
        });
        scrollView = (MyScrollView) findViewById(R.id.scrollView);
        scrollView.setScrollViewListener(new ScrollViewListener() {
            @Override
            public void onScrollChanged(View scrollView, int x, int y, int oldx, int oldy) {
                dataView.invalidate();
                headerView.invalidate();
            }
        });
        dataView    = (CustomDataGridView)findViewById(R.id.custom_datagridview);
        headerView = (HeaderView)findViewById(R.id.headerView);
    }

    public void setData(List<List<String>>  mData, boolean showHeader){
        dataView.setData(mData, showHeader, listener);
    }



    private class InnerListener implements DataGridListener {
        @Override
        public void showHeader(List<String> mData, List<Integer> widthList) {
            headerView.setVisibility(VISIBLE);
            headerView.setData(mData, widthList);
        }

        @Override
        public void hideHeader() {
            //add paddingTop
            int padding = scrollView.getPaddingBottom();
            scrollView.setPadding(padding,padding,padding,padding);
            headerView.setVisibility(GONE);
        }

        @Override
        public int getScrollPosY() {
            return scrollView.getScrollY() - scrollView.getPaddingTop();

        }

        @Override
        public int getScrollPosX() {
            return hScrollView.getScrollX() - hScrollView.getPaddingLeft();
        }
    }
}
