package com.smartlearning.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 日期工具�?
 * @author 
 */
public class DateUtil {

	/**
	 * 获取两个日期的相隔天�?
	 * @param firstDate 起始时间
	 * @param lastDate 结束时间
	 * @return 天数
	 */
	public static Long getDays(Date firstDate, Date lastDate) {
		
		Calendar   t1   =   Calendar.getInstance();   
		Calendar   t2   =   Calendar.getInstance();   
		t1.setTime(firstDate);   
		t2.setTime(lastDate);   
		return (t2.getTimeInMillis()-t1.getTimeInMillis())/(1000*60*60*24);
	}
	
	/**
	 * 获取两个日期的相隔秒�?
	 * @param firstDate 起始时间
	 * @param lastDate 结束时间
	 * @return 秒数
	 */
	public static Long getSeconds(Date firstDate, Date lastDate) {
		
		return (lastDate.getTime() - firstDate.getTime())/1000;
	}

	/**
	 * 获取两个日期的相隔分�?
	 * @param firstDate 起始时间
	 * @param lastDate 结束时间
	 * @return 分钟
	 */
	public static Long getMinutes(Date firstDate, Date lastDate) {
		
		return getSeconds(firstDate, lastDate)/60 + 1;
	}
	
	/**
	 * 获取两个日期的相隔小�?
	 * @param firstDate 起始时间
	 * @param lastDate 结束时间
	 * @return 小时
	 */
	public static Long getHours(Date firstDate, Date lastDate) {
		
		return getMinutes(firstDate, lastDate)/60;
	}
	
	/**
	 * @param date 日期
	 * @param hourOfDay 小时
	 * @param minute 分钟
	 * @return 日期�?
	 */
	public static Date stringToDate(String date,Integer hourOfDay,Integer minute){

		//初始化日期帮助类
		Calendar calendar = Calendar.getInstance();
	
		//将年月日分别放入数组
		String[] dateArray = date.split("-");
	
		//得到传过来的月份
		Integer initMonth = new Integer(dateArray[1]);
		
		//如果月份不是0，月份减1，因为Calendar中set的月份是�?0�?始的
		if (initMonth > 0) {
			
			initMonth = initMonth -1;
		}
		
		//设置年�?�月、日、时、分
		calendar.set(new Integer(dateArray[0]), initMonth, new Integer(dateArray[2]), hourOfDay, minute);
		
		//返回data类型
		return calendar.getTime();
		
		
	}
	/**
	 * @param date日期
	 * @param time时间
	 * @return日期型转为字符串
	 */
	public static String formateDateToString(Date date){
		
		String d = new SimpleDateFormat("yyyy-MM-dd").format(date);
	
		return d;
	}
	
	/**
	 * 得到当前年度
	 * @return
	 */
	public static Integer getCurYear() {
		
		GregorianCalendar g = new GregorianCalendar(); 
		Integer year=(int)g.get(Calendar.YEAR); 
		return year; 
	}
	
	/**
	 * 得到当前年度
	 * @return
	 */
	public static Integer getCurMonth() {
		
		GregorianCalendar g = new GregorianCalendar(); 
		Integer month=(int)g.get(Calendar.MONTH) + 1; 
		return month; 
	}
	
	/**
	 * 得到当前小时
	 * @return
	 */
	public static Integer getHour(){
		
		GregorianCalendar g = new GregorianCalendar(); 
		Integer hour=(int)g.get(Calendar.HOUR_OF_DAY); 
		return hour; 
	}
	
	/**
	 * 得到当前分钟
	 * @return
	 */
	public static Integer getMinute(){
		
		GregorianCalendar g = new GregorianCalendar(); 
		Integer minute=(int)g.get(Calendar.MINUTE); 
		return minute; 
	}
	
	/**
	 * 得到当前�?
	 * @return
	 */
	public static Integer getMonth(){
		
		Calendar cal = Calendar.getInstance();
		Integer month = (int)cal.get(Calendar.MONTH) + 1;
		return month; 
	}
	/**
	 * 以字符串yyyy-MM-dd形式返回当前日期
	 * @return
	 */
	public static String getDateByString(){
		
		Date d = new Date();
		
		String date = new SimpleDateFormat("yyyy-MM-dd").format(d);
		
		return date; 
	}
	
	/**
	 * 获取日期字符�?
	 * @param date
	 * @param bShort
	 * @return
	 */
	public static String dateToString(Date date, boolean bShort) {
		if(date!=null){
			if (bShort)
				return (new SimpleDateFormat("yyyy-MM-dd")).format(date);
			else
				return (new SimpleDateFormat("yyyy-MM-dd HH:mm")).format(date);
		}else{
			return "";
		}
	}
	
	public static Long dateString2Long(String date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date thisDate = null;
		try {
			thisDate = format.parse(date);
		} catch (ParseException e) {
			return null;
		}
		return new Long(thisDate.getTime());
	}
	
	
	public static Long date2Long(Date date){
		return dateString2Long(dateToString(date,true));
	}
	
	/**
	 * 获取中文日期字符�?
	 * @param date
	 * @param bShort
	 * @return
	 */
	public static String dateToChineseString(Date date, boolean bShort) {
		if(date!=null){
			if (bShort)
				return (new SimpleDateFormat("yyyy年MM月dd日")).format(date);
			else
				return (new SimpleDateFormat("yyyy年MM月dd�? HH�?:mm�?")).format(date);
		}else{
			return "";
		}
	}
	
	/**
	 * 在WebLogic环境中比较日�?
	 * @param firstDate 起始日期
	 * @param lastDate 结束日期
	 * @return
	 */
	public static Integer compareDateByWebLoic(Date firstDate, Date lastDate) {
		
		Long timeFirst = firstDate.getTime();
		Long timeLast = lastDate.getTime();
		return timeFirst.compareTo(timeLast);
	}
	
	/**
	 * 给日期增加天�?
	 * @param date 当期日期
	 * @param days 天数
	 * @return 增加之后的日�?
	 */
	public static Date addDays(Date date, Integer days) {
		
		Calendar now = Calendar.getInstance();
		now.setTime(date);
		now.add(Calendar.DATE, days);
		return now.getTime();
	}
	
	/**
	 * 给日期增加月�?
	 * @param date 当期日期
	 * @param days 天数
	 * @return 增加之后的日�?
	 */
	public static Date addMonths(Date date, Integer months) {
		
		Calendar now = Calendar.getInstance();
		now.setTime(date);
		now.add(Calendar.MONTH, months);
		return now.getTime();
	}
	
	/**
	 * 给日期增加年�?
	 * @param date 当期日期
	 * @param days 天数
	 * @return 增加之后的日�?
	 */
	public static Date addYears(Date date, Integer years) {
		
		Calendar now = Calendar.getInstance();
		now.setTime(date);
		now.add(Calendar.YEAR, years);
		return now.getTime();
	}
	
	/**
	 * @param date 日期
	 * @param hourOfDay 小时
	 * @param minute 分钟
	 * @return 日期�?
	 */
	public static Date stringsToDate(String date){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");  
		try {
			return  sdf.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args){
	    System.out.println(DateUtil.getCurMonth());
		
	}
		
}
