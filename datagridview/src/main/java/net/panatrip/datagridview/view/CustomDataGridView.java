package net.panatrip.datagridview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;

import net.panatrip.datagridview.R;
import net.panatrip.datagridview.interfaces.DataGridListener;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by pscj on 2016/11/29.
 */

public class CustomDataGridView extends View{
    private int ITEM_PADDING = 0;
    private int TEXT_MAX_LENGTH = 0;
    private float DEFAULT_FONTSIZE = 0;

    private List<List<String>> mData = null;
    private List<Integer> widthList = new ArrayList<>();
    private int lineHeight = 0;
    private Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint.FontMetrics fm = textPaint.getFontMetrics();
    private  int measureWidth, measureHeight;
    private boolean showHeader;
    private float textSize = DEFAULT_FONTSIZE;
    private DataGridListener listener = null;


    public CustomDataGridView(Context context) {
        super(context);
        init(context);
    }

    public CustomDataGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomDataGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CustomDataGridView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context){
        ITEM_PADDING = getResources().getDimensionPixelSize(R.dimen.datagridview_item_padding);
        DEFAULT_FONTSIZE = getResources().getDimension(R.dimen.datagridview_default_font_size);
        TEXT_MAX_LENGTH = getResources().getInteger(R.integer.datagridview_text_max_length);

        textPaint.setColor(Color.BLACK);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(DEFAULT_FONTSIZE);

        linePaint.setColor(Color.BLACK);
        linePaint.setStyle(Paint.Style.STROKE);

        Rect txtRect = new Rect();
        textPaint.getTextBounds("测试Ag",0,4,txtRect);
        lineHeight = txtRect.height();

        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        measureWidth  = wm.getDefaultDisplay().getWidth();
        measureHeight = wm.getDefaultDisplay().getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if( mData != null && mData.size() > 0 ){
            drawData(canvas, listener.getScrollPosX(), listener.getScrollPosY());
        }else{
            String str = "Loading...";
            int txtWidth = (int)textPaint.measureText(str);
            WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            int screenHeight = wm.getDefaultDisplay().getHeight();
            canvas.drawText(str, (getWidth() - txtWidth) /2,  screenHeight/2, textPaint);
        }
    }

    private void drawData(Canvas canvas, int startPosX, int startPosY){
        int itemHeight = lineHeight+ ITEM_PADDING *2;

        if( startPosY < 0 ){
            startPosY = 0;
        }
        int posY = startPosY - (startPosY % itemHeight);
        if( posY < 0 ){
            posY = 0;
        }

        Pair<Integer, Integer> offsetPair = getOffsetX(startPosX);
        int posX = startPosX - offsetPair.first;
        if( posX < 0 ){
            posX = 0;
        }

        int startRow = getStartRow(startPosY, itemHeight);
        int endRow   = getEndRow(startPosY, itemHeight, startRow);
        //Log.i("drawData", "startPosX:"+startPosX+" posX:"+posX);
        //Log.i("drawData","startPosY:"+startPosY + " PosY:"+ posY + " itemHeight:"+itemHeight);
        //Log.i("drawData", "rows:"+ mData.size() +  " startRow:"+startRow + " endRow:"+ endRow);

        for(int i=startRow; i< endRow; i++){
            List<String> lineItem = mData.get(i);
            drawRow(canvas, lineItem, posX, posY, offsetPair);
            posY += lineHeight + ITEM_PADDING *2;
        }
    }

    //pair<offset, index>
    private Pair<Integer, Integer> getOffsetX(int startPosX){
        int totalWidth = 0;
        for(int i=0; i<widthList.size(); i++){
            int w = widthList.get(i);
            if( totalWidth +  2* ITEM_PADDING + w > startPosX){
                return  new Pair<>(startPosX - totalWidth, i);
            }else if(totalWidth +  2* ITEM_PADDING + w  == startPosX) {
                return new Pair<>(0, i+1);
            }else{
                totalWidth  = totalWidth + 2* ITEM_PADDING + w;
            }
        }
        return new Pair<>(0, 0);
    }
    private int getStartRow(int startPosY,int itemHeight){
        int startRow = 0;
        if(startPosY % itemHeight == 0){
            startRow = startPosY / itemHeight;
        }else{
            if(startPosY < itemHeight){
                startRow = startPosY / itemHeight - 1;
            }else{
                startRow = startPosY / itemHeight;
            }
        }

        if( startRow < 0 ){
            startRow = 0;
        }
        if( showHeader){
            startRow++;
        }
        return startRow;
    }
    private int getEndRow(int startPosY,int itemHeight, int startRow) {
        int endRow = 0;

        int containHeight = ((View)getParent()).getHeight();
        if( containHeight % itemHeight == 0 ){
            endRow = startRow + containHeight / itemHeight;
        }else{
            endRow = startRow + containHeight / itemHeight + 2;
        }
        if( endRow > mData.size()){
            endRow = mData.size();
        }
        return endRow;
    }

    private void drawRow(Canvas canvas, List<String> headers, int startPosX, int posY, Pair<Integer, Integer> dataPair){
        int posX = startPosX;
        for(int j= dataPair.second; j< headers.size(); j++){
            if( checkColumnInScreen(posX , startPosX + dataPair.first, j)){
                canvas.drawRect(posX, posY, posX + widthList.get(j)+ ITEM_PADDING *2, posY + lineHeight+ ITEM_PADDING *2, linePaint);
                String txt = headers.get(j);
                if( !TextUtils.isEmpty(txt) ) {
                    if( txt.length() > TEXT_MAX_LENGTH){
                        txt = txt.substring(0, TEXT_MAX_LENGTH)+ "...";
                    }
                    canvas.drawText(txt, posX + ITEM_PADDING, posY + ITEM_PADDING + Math.abs(fm.top) * lineHeight / (fm.bottom - fm.top), textPaint);
                }
                posX = posX + widthList.get(j) + ITEM_PADDING *2;
            }else{
                break;
            }

        }
    }
    private boolean checkColumnInScreen(int posX, int startPosX, int index){
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        int containWidth  = wm.getDefaultDisplay().getWidth();

        if( posX + widthList.get(index) + 2*ITEM_PADDING <= startPosX + containWidth){
            Log.i("drawData", "posX:"+ posX+" index:"+index+" containWidth:"+containWidth);
            return true;
        }else if( posX < startPosX + containWidth){
            Log.i("drawData", "posX:"+ posX+" index:"+index+" containWidth:"+containWidth);
            return  true;
        }
        return false;
    }
    private int calcWidth(int start, int end){
        int nWidth = 0;
        if(mData != null &&  mData.size() > 0 ) {
            for(int i=start; i< end; i++){
                List<String> lineItem = mData.get(i);
                for(int j=0; j<lineItem.size(); j++){
                    String txt = lineItem.get(j);
                    if( !TextUtils.isEmpty(txt)){
                        if( txt.length() > TEXT_MAX_LENGTH){
                            txt = txt.substring(0, TEXT_MAX_LENGTH) + "...";
                        }
                        int txtWidth = (int)textPaint.measureText(txt);
                        if(widthList.get(j) <  txtWidth){
                            widthList.set(j, txtWidth);
                        }
                    }
                }
            }

            for(Integer w : widthList){
                nWidth  = nWidth + 2* ITEM_PADDING + w;
            }
        }
        return nWidth;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if( measureWidth < getWidth()){
            measureWidth = getWidth();
        }
//        if( measureHeight < getHeight()){
//            measureHeight = getHeight();
//        }
        setMeasuredDimension(measureWidth, measureHeight);
    }

    public void setData(final List<List<String>>  mData, boolean showHeader, DataGridListener listener){
        this.mData = mData;
        this.showHeader = showHeader;
        this.listener = listener;

        if(mData != null &&  mData.size() > 0 ) {
            //init width array
            widthList.clear();
            for (int i = 0; i < mData.get(0).size(); i++) {
                widthList.add(0);
            }

            //init firstscreen measureWidth & measureHeight
            measureHeight = (lineHeight + ITEM_PADDING * 2) * mData.size();
            int visibleLine = getHeight() /  (lineHeight + ITEM_PADDING * 2);
            if(visibleLine > 0) {
                int count = Math.min(visibleLine, mData.size());
                measureWidth = calcWidth(0, count);
                if(showHeader){
                    listener.showHeader(mData.get(0), widthList);
                }else{
                    listener.hideHeader();
                }
                invalidate();
            }

            //calc total width
            GetWidthTask task = new GetWidthTask();
            task.execute();
        }
    }

    private class GetWidthTask extends AsyncTask<Void,Void, Integer>{

        @Override
        protected Integer doInBackground(Void... params) {
            return calcWidth(0, mData.size());
        }

        protected void onPostExecute(Integer result) {
            if( result > measureWidth ){
                measureWidth = result;
                if(showHeader){
                    listener.showHeader(mData.get(0), widthList);
                }
                requestLayout();
            }
        }
    }
}
