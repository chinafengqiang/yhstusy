package com.smartlearning.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;

/**
 * ����ͼƬ�Ĺ�����.
 * 
 */
public class ImageTools {

	/** */
	/**
	 * ͼƬȥɫ,���ػҶ�ͼƬ
	 * 
	 * @param bmpOriginal
	 *            �����ͼƬ
	 * @return ȥɫ���ͼƬ
	 */
	public static Bitmap toGrayscale(Bitmap bmpOriginal) {
		int width, height;
		height = bmpOriginal.getHeight();
		width = bmpOriginal.getWidth();
		Bitmap bmpGrayscale = Bitmap.createBitmap(width, height,
				Bitmap.Config.RGB_565);
		Canvas c = new Canvas(bmpGrayscale);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		c.drawBitmap(bmpOriginal, 0, 0, paint);
		return bmpGrayscale;
	}

	/** */
	/**
	 * ȥɫͬʱ��Բ��
	 * 
	 * @param bmpOriginal
	 *            ԭͼ
	 * @param pixels
	 *            Բ�ǻ���
	 * @return �޸ĺ��ͼƬ
	 */
	public static Bitmap toGrayscale(Bitmap bmpOriginal, int pixels) {
		return toRoundCorner(toGrayscale(bmpOriginal), pixels);
	}

	/** */
	/**
	 * ��ͼƬ���Բ��
	 * 
	 * @param bitmap
	 *            ��Ҫ�޸ĵ�ͼƬ
	 * @param pixels
	 *            Բ�ǵĻ���
	 * @return Բ��ͼƬ
	 */
	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	/** */
	/**
	 * ʹԲ�ǹ���֧��BitampDrawable
	 * 
	 * @param bitmapDrawable
	 * @param pixels
	 * @return
	 */
	public static BitmapDrawable toRoundCorner(BitmapDrawable bitmapDrawable,
			int pixels) {
		Bitmap bitmap = bitmapDrawable.getBitmap();
		bitmapDrawable = new BitmapDrawable(toRoundCorner(bitmap, pixels));
		return bitmapDrawable;
	}

	/**
	 * ��ȡ·���е�ͼƬ��Ȼ����ת��Ϊ��ź��bitmap
	 * 
	 * @param path
	 */
	public static void saveBefore(String path) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		// ��ȡ���ͼƬ�Ŀ�͸�
		Bitmap bitmap = BitmapFactory.decodeFile(path, options); // ��ʱ����bmΪ��
		options.inJustDecodeBounds = false;
		// ������ű�
		int be = (int) (options.outHeight / (float) 200);
		if (be <= 0)
			be = 1;
		options.inSampleSize = 2; // ͼƬ�������С����֮һ
		// ���¶���ͼƬ��ע�����Ҫ��options.inJustDecodeBounds ��Ϊ falseŶ
		bitmap = BitmapFactory.decodeFile(path, options);
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		System.out.println(w + "   " + h);
		// savePNG_After(bitmap,path);
		saveJPGE_After(bitmap, path);
	}

	/**
	 * ����ͼƬΪPNG
	 * 
	 * @param bitmap
	 * @param name
	 */
	public static void savePNG_After(Bitmap bitmap, String name) {
		File file = new File(name);
		try {
			FileOutputStream out = new FileOutputStream(file);
			if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
				out.flush();
				out.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ����ͼƬΪJPEG
	 * 
	 * @param bitmap
	 * @param path
	 */
	public static void saveJPGE_After(Bitmap bitmap, String path) {
		File file = new File(path);
		try {
			FileOutputStream out = new FileOutputStream(file);
			if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
				out.flush();
				out.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ͼƬ�ϳ�
	 * 
	 * @param bitmap
	 * @return
	 */
	private Bitmap createBitmap(Bitmap src, Bitmap watermark) {
		if (src == null) {
			return null;
		}
		int w = src.getWidth();
		int h = src.getHeight();
		int ww = watermark.getWidth();
		int wh = watermark.getHeight();
		// create the new blank bitmap
		Bitmap newb = Bitmap.createBitmap(w, h, Config.ARGB_8888);// ����һ���µĺ�SRC���ȿ��һ���λͼ
		Canvas cv = new Canvas(newb);
		// draw src into
		cv.drawBitmap(src, 0, 0, null);// �� 0��0��꿪ʼ����src
		// draw watermark into
		cv.drawBitmap(watermark, w - ww + 5, h - wh + 5, null);// ��src�����½ǻ���ˮӡ
		// save all clip
		cv.save(Canvas.ALL_SAVE_FLAG);// ����
		// store
		cv.restore();// �洢
		return newb;
	}

	// ��ͼƬת����byte[]�Ա��ܽ���浽��ݿ�
	public static byte[] getByteFromBitmap(Bitmap bitmap) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
		try {
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			// Log.e(TAG, "transform byte exception");
		}
		return out.toByteArray();
	}

	// �õ��洢����ݿ��е�ͼƬ
	// eg imageView.setImageBitmap(bitmapobj);
	public static Bitmap getBitmapFromByte(byte[] temp) {
		if (temp != null) {
			Bitmap bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length);
			return bitmap;
		} else {
			// Bitmap bitmap=BitmapFactory.decodeResource(getResources(),
			// R.drawable.contact_add_icon);
			return null;
		}
	}
    //���ֻ��е��ļ�ת��ΪBitmap����
	public static Bitmap getBitemapFromFile(File f) {
		if (!f.exists())
			return null;
		try {
			return BitmapFactory.decodeFile(f.getAbsolutePath());
		} catch (Exception ex) {
			return null;
		}
	}
	//���ֻ��е��ļ�ת��ΪBitmap����(����+1)
	public static Bitmap getBitemapFromFile(String fileName) {
		
		try {
			return BitmapFactory.decodeFile(fileName);
		} catch (Exception ex) {
			return null;
		}
	}

}