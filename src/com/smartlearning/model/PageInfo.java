package com.smartlearning.model;

/**
 * PageInfo 的使用：
 * 		PageInfo 类是作为需要用到分页查询的函数的参数使用。首先创建一个PageInfo对象，设置currentPage值（需要的第几页）
 *  和pageSize(页大小)，将对象作为参数传递到查询调用中，查询调用会将此对象传递到DAO模板中，模板根据查询条
 *  件统计出查询结果记录统计数，然后将该值写入到此对象的totalResult值中。然后调用calculate()函数
 *  计算出(totalPage)总页数、查询结果的起始行（beginResutl)。DAO模板会使用beginResult和
 *  pageSize查询出相应的结果。
 *  	使用PageInfo作为查询函数的参数调用，函数在返回查询结果的同时也修改了PageInfo的内容，因此显示分页信息，可以直接
 *  使用查询调用后的PageInfo对象。
 */
public class PageInfo {
	private int pageSize = 11;    
	private int currentPage =1; 
	private int beginResult = 0;
	private int totalResult;
	private int totalPage = 1;
	private int endResult = 0;
	
	public PageInfo(){}

	public PageInfo(int pageSize, int currentPage) {
		super();
		this.pageSize = pageSize;
		this.currentPage = currentPage;
		
		int tempBeginResult = (currentPage-1)*pageSize;
		this.beginResult = tempBeginResult<0?0:tempBeginResult;
		this.endResult = this.beginResult + pageSize;
	}
	
	public PageInfo(int currentPage) {
		super();
		this.currentPage = currentPage;
		
		int tempBeginResult = (currentPage-1)*pageSize;
		this.beginResult = tempBeginResult<0?0:tempBeginResult;
		this.endResult = this.beginResult + pageSize;
	}

	/*
	 * get to fetch data
	 */
    public int getBeginResult() {
        return beginResult;
    }
    

	public int getEndResult() {
		return endResult;
	}

	
	/*
     * get to show
     */
    public int getCurrentPage() {
        return currentPage;
    }
    /*
     * set to calculate beginResult and total page
     */
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
    /*
     * get to show and to fetch data
     */
    public int getPageSize() {
        return pageSize;
    }
    /*
     * set to calculate beginResult and totalPage
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
    /*
     * get to show
     */
    public int getTotalPage(){
 	   totalPage = totalResult / pageSize;
       if (totalResult % pageSize > 0 ) totalPage++;
       return totalPage;
    }
    /*
     * get to show
     */
    public int getTotalResult() {
        return totalResult;
    }
    /*
     * set to calculate beginResult and totalPage
     */
    public void setTotalResult(int totalResult) {
        this.totalResult = totalResult;
    }
    
    public void calculate(){
        //calculate totalPage
        totalPage = totalResult / pageSize;
        if (totalResult % pageSize > 0 ) totalPage++;
        
        //set the currentPage
        if (currentPage<=0){
        }
        if (currentPage*pageSize>totalResult){
            currentPage = totalPage;
        }

        //calculate the beginResult
        beginResult = (currentPage-1)*pageSize;
        if (beginResult<0) beginResult = 0;
    }
    
    


}
