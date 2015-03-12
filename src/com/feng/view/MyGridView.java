package com.feng.view;




import com.smartlearning.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.GridView;

public class MyGridView extends GridView {

	private Bitmap background;

	public MyGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		background = BitmapFactory.decodeResource(getResources(),R.drawable.bookshelf_layer_center);
		//background = decodeResource(getResources(),R.drawable.bookshelf_layer_center);
	}

	
	private Bitmap decodeResource(Resources resources, int id) {
	    TypedValue value = new TypedValue();
	    resources.openRawResource(id, value);
	    BitmapFactory.Options opts = new BitmapFactory.Options();
	    opts.inTargetDensity = value.density;
	    return BitmapFactory.decodeResource(resources, id, opts);
	}
	@Override
	protected void dispatchDraw(Canvas canvas) {
		int count = getChildCount();
		int top = count > 0 ? getChildAt(0).getTop() : 0;
		int backgroundWidth = background.getWidth();
		int backgroundHeight = background.getHeight()+2;
		int width = getWidth();
		int height = getHeight();

		for (int y = top; y < height; y += backgroundHeight) {
			for (int x = 0; x < width; x += backgroundWidth) {
				canvas.drawBitmap(background, x, y, null);
			}
		}

		super.dispatchDraw(canvas);
	}

}
