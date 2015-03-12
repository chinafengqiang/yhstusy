package com.smartlearning.utils;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Point;
import android.util.Log;

/**
 * 转换工具
 * @author Administrator
 */
public class PointToFloat {
	
	public static float[] convertPointListToFloat(List<Point> points) {

		List<Integer> pointsFloat = new ArrayList<Integer>();
		float[] pts =new float[(points.size()-1)*4] ;

		for (int i = 0; i < points.size(); i++) {
			if (i == 0) {
				pointsFloat.add(points.get(i).x);
				pointsFloat.add(points.get(i).y);

			} else if (i == points.size() - 1) {
				pointsFloat.add(points.get(i).x);
				pointsFloat.add(points.get(i).y);

			} else {
				pointsFloat.add(points.get(i).x);
				pointsFloat.add(points.get(i).y);
				pointsFloat.add(points.get(i).x);
				pointsFloat.add(points.get(i).y);
			}
		}
		
		for (int i = 0; i < pointsFloat.size(); i++) {
			pts[i] = pointsFloat.get(i);
		}
		
		return pts ;

	}
}
