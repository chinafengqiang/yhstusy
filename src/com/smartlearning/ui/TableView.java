package com.smartlearning.ui;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smartlearning.biz.LessonManager;
import com.smartlearning.model.LessonExtend;
import com.smartlearning.model.LessonVO;

public class TableView extends ViewGroup {

	private static final int STARTX = 0;// ��ʼX���
    private static final int STARTY = 0;// ��ʼY���
    private static final int BORDER = 1;// ���߿���
    private static final String[] LESSON_NUM= {"第一节","第二节","第三节","第四节","第五节","第六节","第七节","第八节"};
    private int mRow;// ����
    private int mCol;// ����
    private long classId;
    private String serverIP;
    Context context = null;
    private List<LessonVO> lessonList;
     
    public TableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mRow = 3;// Ĭ������Ϊ3
        this.mCol = 3;// Ĭ������Ϊ3 
        // ����ӿؼ�
        this.context = context;
        this.addOtherView(context);
    }
     
    public TableView(Context context, int row,int col, long classId, String serverIP) {
        super(context);
        if(row>20 || col>20){
            this.mRow = 20;// ����20��ʱ����������Ϊ20��
            this.mCol = 20;// ����20��ʱ����������Ϊ20��
        }else if(row==0 || col==0){
            this.mRow = 3;
            this.mCol = 3;
        }
        else{
            this.mRow = row;
            this.mCol = col;
        }
        this.classId = classId;
        this.serverIP = serverIP;
        // ����ӿؼ�
        this.context = context;
        this.addOtherView(context);
    }
    
    public TableView(Context context, int row,int col,List<LessonVO> lessonList) {
        super(context);
        if(row>20 || col>20){
            this.mRow = 20;
            this.mCol = 20;
        }else if(row==0 || col==0){
            this.mRow = 3;
            this.mCol = 3;
        }
        else{
            this.mRow = row;
            this.mCol = col;
        }

        this.context = context;
        this.lessonList = lessonList;
        this.addOtherView(context);
    }
    
    private void initLessonLayout(int row,int col){
    	TextView view = getTextView(context, "序号");	
    	view.setTextSize(25);
    	addView(view);
    	view = getTextView(context, "星期一");
    	view.setTextSize(25);
    	addView(view);
    	view = getTextView(context, "星期二");	
    	view.setTextSize(25);
    	addView(view);
    	view = getTextView(context, "星期三");
    	view.setTextSize(25);
    	addView(view);
    	view = getTextView(context, "星期四");	
    	view.setTextSize(25);
    	addView(view);
    	view = getTextView(context, "星期五");	
    	view.setTextSize(25);
    	addView(view);
    	
    	/*int count = col * (row-1);
    	for(int i = 0;i < count; i++){
    		if(i % col == 0){
    			view = getTextView(context, "");	
    		}else{
    			view = getTextView(context, "");	
    		}
        	addView(view);
    	}*/
    	if(lessonList != null){
    		for(int n = 0;n<lessonList.size();n++){
    			LessonVO lesson = lessonList.get(n);
    			String num = LESSON_NUM[lesson.getLnum()-1];
    			String time = lesson.getLtime();
    			TextView view1 = getTextView(context, num+"\n"+time);
    			view1.setTextSize(20);
    			TextView view2 = getTextView(context, lesson.getLwone());
    			view2.setTextSize(20);
    			TextView view3 = getTextView(context, lesson.getLwtwo());
    			view3.setTextSize(20);
    			TextView view4 = getTextView(context, lesson.getLwthree());
    			view4.setTextSize(20);
    			TextView view5 = getTextView(context, lesson.getLwfour());
    			view5.setTextSize(20);
    			TextView view6 = getTextView(context, lesson.getLwfive());
    			view6.setTextSize(20);
    			addView(view1);
    			addView(view2);
    			addView(view3);
    			addView(view4);
    			addView(view5);
    			addView(view6);
    		}
    	}
    }
    
	Handler handlerLesson = new Handler() {
		public void handleMessage(android.os.Message msg) {
			@SuppressWarnings("unchecked")
			List<LessonExtend> list = (List<LessonExtend>) msg.obj;
			
				TextView view = getTextView(context, "序号");	
		    	addView(view);
		    	view = getTextView(context, "星期一");	
		    	addView(view);
		    	view = getTextView(context, "星期二");	
		    	addView(view);
		    	view = getTextView(context, "星期三");	
		    	addView(view);
		    	view = getTextView(context, "星期四");	
		    	addView(view);
		    	view = getTextView(context, "星期五");	
		    	addView(view);
		    	view = getTextView(context, "星期六");	
		    	addView(view);
		    	int count = 0;
		    	if(list != null){
		    	for(int i = 0; i < list.size(); i ++){
		    		if(i % 6 == 0){
		    			if(count == 0){
		    				view = getTextView(context, "第一节");	
		    			}
		    			else if(count == 1){
		    				view = getTextView(context, "第二节");	
		    			}
		    			else if(count == 2){
		    				view = getTextView(context, "第三节");	
		    			}
		    			else if(count == 3){
		    				view = getTextView(context, "第四节");	
		    			}
		    			else if(count == 4){
		    				view = getTextView(context, "第五节");	
		    			}
		    			else if(count == 5){
		    				view = getTextView(context, "第六节");	
		    			}
		    			else if(count == 6){
		    				view = getTextView(context, "第七节");	
		    			}
		    			else if(count == 7){
		    				view = getTextView(context, "第八节");	
		    			}
		    	    	addView(view);
		    	    	count ++;
		    		}
		    		LessonExtend lesson = list.get(i);

					view = getTextView(context, lesson.getSubjectName());
					if (count % 2 != 0) {
						view.setBackgroundColor(Color.rgb(223, 242, 248));
					} else {
						view.setBackgroundColor(Color.rgb(255, 255, 255));
					}
					addView(view);
		    	}
			} 
			
		}
	};
	
	//创建新线程从本地试卷列表
	public void loadLessonList(final String serverIP, final long classId) {
		
		Thread thread = new Thread() {
			@Override
			public void run() {
				LessonManager manager = new LessonManager();
		    	List<LessonExtend> list = null;
				try {
					list = manager.getLessonsByClass(serverIP, classId);
				} catch (Exception e) {
				}
				Message message = Message.obtain();
				message.obj = list;
				handlerLesson.sendMessage(message);
			}
		};
		
		thread.start();
		thread = null;

	}
	
    public void addOtherView(Context context){
    	
    	//loadLessonList(this.serverIP, this.classId);
    	initLessonLayout(mRow,mCol);
    }
    
    private TextView getTextView(Context context,String text){
    	TextView view = new TextView(context);
        view.setText(text);
        view.setTextColor(Color.rgb(79, 129, 189));
        view.setPadding(0, 20, 0, 0);
        view.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        return view;
    }
     
    @Override
    protected void dispatchDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setStrokeWidth(BORDER);
        paint.setColor(Color.rgb(223, 242, 248));
        paint.setStyle(Style.STROKE);
        // �����ⲿ�߿�
        canvas.drawRect(STARTX, STARTY, getWidth()-STARTX, getHeight()-STARTY, paint);
        // ���зָ���
        for(int i=1;i<mCol;i++){
            canvas.drawLine((getWidth()/mCol)*i, STARTY, (getWidth()/mCol)*i, getHeight()-STARTY, paint);
        }
        // ���зָ���
        for(int j=1;j<mRow;j++){
            canvas.drawLine(STARTX, (getHeight()/mRow)*j, getWidth()-STARTX, (getHeight()/mRow)*j, paint);
        }
        super.dispatchDraw(canvas);
    }
     
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int x = STARTX+BORDER;
        int y = STARTY+BORDER;
        int i = 0;
        int count = getChildCount();
        for(int j=0; j<count; j++){
            View child = getChildAt(j);
            child.layout(x, y, x+getWidth()/mCol-BORDER*2, y+getHeight()/mRow-BORDER*2);
            if(i >=(mCol-1)){
                i = 0;
                x = STARTX+BORDER;
                y += getHeight()/mRow;
            }else{
                i++;
                x += getWidth()/mCol;
            }
        }
    }
     
    public void setRow(int row){
        this.mRow = row;
    }
     
    public void setCol(int col){
        this.mCol = col;
    }


}
