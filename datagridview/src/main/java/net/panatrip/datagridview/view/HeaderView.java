package net.panatrip.datagridview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import net.panatrip.datagridview.R;

import java.util.List;

/**
 * Created by pscj on 2016/11/30.
 */

public class HeaderView extends View {
    private List<String> headers = null;
    private List<Integer> widthList  = null;

    private int ITEM_PADDING = 0;
    private float DEFAULT_FONTSIZE = 0;
    private int lineHeight = 0;
    private  int measureWidth, measureHeight;

    private Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint.FontMetrics fm = textPaint.getFontMetrics();

    public HeaderView(Context context) {
        super(context);
        init(context, null);
    }

    public HeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public HeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public HeaderView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }
    public void setData(List<String> mData,  List<Integer> widthList){
        this.headers = mData;
        this.widthList = widthList;

        int nWidth = 0;
        for(Integer width : widthList){
            nWidth  = nWidth + 2* ITEM_PADDING + width;
        }
        measureWidth = nWidth;
        requestLayout();
    }

    private void init(Context context, AttributeSet attrs){
        ITEM_PADDING = getResources().getDimensionPixelSize(R.dimen.datagridview_item_padding);
        DEFAULT_FONTSIZE = getResources().getDimension(R.dimen.datagridview_default_font_size);

        textPaint.setColor(Color.WHITE);
        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setTextSize(DEFAULT_FONTSIZE);

        fillPaint.setColor(0xFF0088CC);
        fillPaint.setStyle(Paint.Style.FILL);

        linePaint.setColor(Color.BLACK);
        linePaint.setStyle(Paint.Style.STROKE);

        Rect txtRect = new Rect();
        textPaint.getTextBounds("测试Ag",0,4,txtRect);
        lineHeight = txtRect.height();
        measureHeight = lineHeight+ ITEM_PADDING *2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if( headers != null && headers.size() > 0 && widthList != null && headers.size() == widthList.size() ){
            drawHeader(canvas, 0, 0);
        }
    }
    private void drawHeader(Canvas canvas, int posX, int posY){
        for(int j=0; j< headers.size(); j++){
            canvas.drawRect(posX, posY, posX + widthList.get(j)+ ITEM_PADDING *2, posY + lineHeight+ ITEM_PADDING *2, fillPaint);
            canvas.drawRect(posX, posY, posX + widthList.get(j)+ ITEM_PADDING *2, posY + lineHeight+ ITEM_PADDING *2, linePaint);

            String txt = headers.get(j);
            if( !TextUtils.isEmpty(txt) ) {
                canvas.drawText(txt, posX + ITEM_PADDING, posY + ITEM_PADDING + Math.abs(fm.top) * lineHeight / (fm.bottom - fm.top), textPaint);
            }
            posX = posX + widthList.get(j) + ITEM_PADDING *2;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureWidth, measureHeight);
    }
}
