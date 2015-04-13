package com.smartlearning.db;






/**
 *本地数据库
 */
public interface DB {
	
//	public static final String DATABASE_NAME= "elearning";
	
	public static final int DATABASE_VERSION = 2;
	
	public static final String dbPath = android.os.Environment.getExternalStorageDirectory().getPath() + "/elearningDB";
	public static final String DATABASE_NAME = dbPath + "/" + "elearning.db";
	
	
	/**
	 *本地表结构
	 */
	public interface TABLES{
		
		/**
		 * 信息通知
		 */
		public interface SYSMESSAGE {
			public static final String TABLENAME = "sysmessage";

			public interface FIELDS {
				public static final String ID = "id";
				public static final String _ID = "_id";// 通知的id
				public static final String TIME = "time";
				public static final String NAME = "name";
				public static final String _CONTENT = "_content";
				public static final String ISREAD="isRead";
				public static final String CREATOR="creator";
				public static final String CLASS_ID="class_id";
			}

			public interface SQL {
				public static final String CREATE = "create table if not exists sysmessage(id integer PRIMARY KEY ,_id int,isRead int,time varchar(50),name varchar(100),_content varchar(3000), creator varchar(100), class_id int)";
				public static final String DROP = "drop table if exists sysmessage";
				public static final String INSERT = "insert into sysmessage(_id,isRead,time,name,_content,creator, class_id) values(%s,%s,%s,'%s','%s','%s',%s) ";// 插入
				public static final String SELECT = "select * from sysmessage where %s";// 查询
				public static final String UPDATE = " update sysmessage set isRead = %s where _id = %s;";
				public static final String DELETE = " delete FROM sysmessage";
			}
		}
		
		
		/**
		 *试卷分类
		 */
		public interface TESTPAPER_CATEGORY {
			
			public static final String TABLENAME = "testpaper_catetory";

			public interface FIELDS {
				public static final String ID="id";
				public static final String _ID = "_id";// 试卷分类的id
				public static final String NAME = "name";
			}
			
			public interface SQL{                                                                                       
				public static final String CREATE="create table if not exists testpaper_catetory(id integer PRIMARY KEY AUTOINCREMENT, _id int, name varchar(100))";
				public static final String DROP ="drop table if exists testpaper_catetory";
				public static final String INSERT = "insert into testpaper_catetory (_id, name) values(%s, '%s') ";// 插入
				public static final String DELETE = "delect from testpaper_catetory";// 根据ID删除
				public static final String SELECT = "select * from testpaper_catetory where %s";// 查询
				public static final String COUNT = "select count(*) from testpaper_catetory";// 查询
				public static final String SELECT_COUNT = "select count(*) from testpaper_catetory where {0}";
			}
		}
		
		/**
		 *试卷
		 */
		public interface TESTPAPER{
			
			public static final String TABLENAME = "testpaper";

			public interface FIELDS {
				public static final String ID="id";
				public static final String _ID = "_id";// 试卷的id
				public static final String NAME = "name";
				public static final String PARENT_ID = "parent_id";
				public static final String PASS_SCORE = "pass_score";
				public static final String TOTAL_SCORE = "total_score";
				public static final String CLASS_ID="class_id";
				public static final String STAUTS="stauts";
				
			}
			
			public interface SQL{                                                                                       
				public static final String CREATE="create table if not exists testpaper(id integer PRIMARY KEY AUTOINCREMENT, _id int, name varchar(100)," +
						" parent_id int, pass_score varchar(10), total_score varchar(10), class_id int, stauts int)";
				public static final String DROP ="drop table if exists testpaper";
				public static final String INSERT = "insert into testpaper (_id, name, parent_id,pass_score, total_score, class_id, stauts) values(%s, '%s', %s, '%s','%s' ,%s ,%s) ";// 插入
				public static final String DELETE = "delect from testpaper";// 根据ID删除
				public static final String SELECT = "select * from testpaper where %s";// 查询
				public static final String COUNT = "select count(*) from testpaper where %s";// 查询
				public static final String SELECT_COUNT = "select count(*) from testpaper where {0}";
				public static final String UPDATE = " update testpaper set stauts = %s where _id= %s";
				
			}
		}
		
		/**
		 *试卷题目
		 */
		public interface TEST_PAPER_QUESTION{
			
			public static final String TABLENAME = "test_paper_question";

			public interface FIELDS {
				public static final String ID="id";
				public static final String _ID = "_id";// 试卷题目的id
				public static final String NAME = "name";
				public static final String TEST_PAPER_ID = "test_paper_id";
				public static final String QUESTION_ID = "question_id";
				public static final String QUESTION_TYPE = "question_type";
				public static final String OPTIONS = "options";
				public static final String ANSWER = "answer";
				public static final String KEN = "ken";
				public static final String DIFFICULTY = "difficulty";
				public static final String SCORE = "score";
				public static final String NOTE = "note";
				public static final String favStauts = "favStauts";
				
			}
			
			public interface SQL{                                                                                       
				public static final String CREATE="create table if not exists test_paper_question" +
						"(id integer PRIMARY KEY AUTOINCREMENT," +
						" _id int, " +
						" name varchar(100)," +
						" test_paper_id int, " +
						" question_id int, " +
						" question_type int, " +
						" options varchar(100), " +
						" answer varchar(100), " +
						" ken varchar(100), " +
						" difficulty int, " +
						" score float, " +
						" note blob,  favStauts int)";
				public static final String DROP ="drop table if exists test_paper_question";
				public static final String INSERT = "insert into test_paper_question " +
						"(_id, name, test_paper_id, question_id, question_type, options, answer, ken, difficulty, score, note) " +
						"values(%s, '%s', %s, %s, %s, '%s', '%s', '%s', %s, %s, %s, %s) ";
				public static final String DELETE = "delect from test_paper_question";// 根据ID删除
			//	public static final String SELECT = "select * from test_paper_question where %s";// 查询
				public static final String UPDATE = " update test_paper_question set favStauts = %s where question_id = %s and test_paper_id= %s";
				public static final String SELECT = "select * from test_paper_question where {0}";
				public static final String SELECT_COUNT = "select count(*) from test_paper_question where {0}";
				
			}
		}
		
		
		/**
		 *记录试卷成绩
		 */
		public interface USER_TEST_PAPER{
			
			public static final String TABLENAME = "user_test_paper";

			public interface FIELDS {
				public static final String ID="id";
				public static final String USER_ID = "user_id";
				public static final String QUESTION_ID = "question_id";
				public static final String TIME = "time";
				public static final String SCORE = "score";
				public static final String OPTIONS = "options";
				public static final String IS_CORRECT = "is_correct";
				public static final String TEST_PAPER_ID = "test_paper_id";
				public static final String NO_SELECT_ANSWER = "no_select_answer";
			}
			
			public interface SQL{                                                                                       
				public static final String CREATE="create table if not exists user_test_paper" +
						"(id integer PRIMARY KEY AUTOINCREMENT," +
						" user_id int, " +
						" question_id int, " +
						" time varchar(50), " +
						" score varchar(50), " +
						" options varchar(10), " +
						" is_correct varchar(10), " +
						" test_paper_id int," +
						" no_select_answer varchar(100))";
				public static final String DROP ="drop table if exists user_test_paper";
				public static final String INSERT = "insert into user_test_paper " +
						"(user_id, question_id, time, score, options, is_correct, test_paper_id, no_select_answer) " +
						"values(%s, %s, '%s', '%s', '%s', '%s', %s, '%s') ";
				public static final String DELETE = "delect from user_test_paper";// 根据ID删除
				public static final String SELECT = "select * from user_test_paper where %s";
				
			}
		}
		
		public interface USERINFO{
			 public static final String TABLENAME = "user_info";
			   public interface FIELDS{
				   public static final String ID="id";
				   public static final String USER_ID = "user_id";
				   public static final String USER_NAME = "user_name";
				   public static final String USER_PASSWORD = "user_password";
				   public static final String CLASS_ID = "class_id";
			   } 
			   public interface SQL{
				   public static final String CREATE ="Create  TABLE if not exists user_info("+
					   	" [id] integer PRIMARY KEY AUTOINCREMENT"+
					   	",[user_id] varchar(20) NOT NULL"+
					   	",[user_name] varchar(50) NOT NULL"+
					   	",[user_password] varchar(50)"+
					   	",[class_id] int"+
					   	");";
				   public static final String DROP = "DROP TABLE IF EXISTS user_info";
				   public static final String SELECT_ALL = "select * from user_info";
				   public static final String SELECT_COUNT = "select count(*) from user_info where {0}";
				   public static final String DELETE = "delete from user_info";
			   }
		}
		
		/**
		 * 电子书
		 */
		public interface EBOOK {
			public static final String TABLENAME = "ebook";

			public interface FIELDS {
				public static final String ID = "id";
				public static final String _ID = "_id";
				public static final String TIME = "time";
				public static final String NAME = "name";
				public static final String PDF_URL = "pdf_url";
				public static final String IMAGE_URL="image_url";
				public static final String CATEGORY_NAME="category_name";
				public static final String CLASS_ID="class_id";
			}

			public interface SQL {
				public static final String CREATE = "create table if not exists ebook (" +
						"id integer PRIMARY KEY AUTOINCREMENT, " +
						"_id int," +
						"time varchar(50), " +
						"name varchar(100), " +
						"pdf_url varchar(200), " +
						"image_url blob, " +
						"category_name varchar(200), " +
						"class_id int)";
				public static final String DROP = "drop table if exists ebook";
				public static final String INSERT = "insert into ebook " +
						"(_id, " +
						"time, name, pdf_url, image_url, category_name, class_id) values(%s, '%s', '%s', '%s',%s, '%s', %s) ";// 插入
				public static final String SELECT = "select * from ebook where {0}";// 查询
				public static final String SELECT1 = "select * from ebook where %s";// 查询
				public static final String SELECT_NEW = "select id,_id,time,name,pdf_url,category_name,class_id from ebook where %s";// 查询
				public static final String SELECT_ALL = "select * from ebook order by _id";// 查询
			//	public static final String UPDATE = " update ebook set isRead = %s where _id = %s;";
				public static final String DELETE = " delete FROM ebook where %s";
				public static final String SELECT_COUNT = "select count(*) from ebook where {0}";
				public static final String DELETE_ALL = " delete FROM ebook";
				public static final String GET_CATEGORY = "select distinct category_name from ebook";
				public static final String SELECT_BOOK_RES = "select id,_id,name,time,pdf_url from ebook where %s";
				
			}
		}
		
		
		/**
		 * 电子书
		 */
		public interface EVIDEOS {
			public static final String TABLENAME = "evideos";

			public interface FIELDS {
				public static final String ID = "id";
				public static final String _ID = "_id";
				public static final String TIME = "time";
				public static final String NAME = "name";
				public static final String VIDEO_URL = "video_url";
				public static final String IMAGE_URL="image_url";
				public static final String CATEGORY_NAME="category_name";
				public static final String LECTUER="lectuer";
				public static final String HOUR="hour";
				public static final String DESCRIPTION="description";
				public static final String TEACHERDESCRIPTION="teacherDescription";
				public static final String VOIDE_SIZE="voide_size";
			}

			public interface SQL {
				public static final String CREATE = "create table if not exists evideos (" +
						"id integer PRIMARY KEY AUTOINCREMENT, " +
						"_id int," +
						"time varchar(50), " +
						"name varchar(100), " +
						"video_url varchar(200), " +
						"image_url blob, " +
						"category_name varchar(200), " +
						"lectuer varchar(100), " +
						"hour int, " +
						"description varchar(2000), " +
						"teacherDescription varchar(2000), " +
						"voide_size varchar(100));" ;
				
				public static final String DROP = "drop table if exists evideos";
				public static final String INSERT = "insert into evideos " +
						"(_id, " +
						"time, name, video_url, image_url, category_name, lectuer, hour, description, teacherDescription, voide_size) " +
						"values(%s, '%s', '%s', '%s',%s, '%s', '%s', %s, '%s', '%s', '%s') ";// 插入
				public static final String SELECT = "select * from evideos where {0}";// 查询
				public static final String SELECT1 = "select * from evideos where %s";// 查询
				public static final String UPDATE = " update evideos set voide_size = %s where _id = %s;";
				public static final String DELETE = " delete FROM evideos where %s";
				public static final String SELECT_COUNT = "select count(*) from evideos where {0}";
				public static final String DELETE_ALL = " delete FROM evideos";
			}
		}
		
		
		/**
		 * 电子书分册
		 */
		public interface BOOKPART {
			public static final String TABLENAME = "book_part";

			public interface FIELDS {
				public static final String ID = "id";
				public static final String _ID = "_id";
				public static final String NAME = "name";
				public static final String CATEGORY_ID = "category_id";
				public static final String CATEGORY_NAME="category_name";
				public static final String USER_ID = "user_id";
				public static final String EXT="ext";
			}

			public interface SQL {
				public static final String CREATE = "create table if not exists book_part (" +
						"id integer PRIMARY KEY AUTOINCREMENT, " +
						"_id int," +
						"name varchar(100), " +
						"category_id int, " +
						"category_name varchar(200), " +
						"user_id int, " +
						"ext varchar(100));" ;
				
				public static final String DROP = "drop table if exists book_part";
				public static final String INSERT = "insert into book_part " +
						"(_id, " +
						"name,category_id,category_name,user_id,ext ) " +
						"values(%s, '%s', %s, '%s',%s,'%s') ";// 插入
				public static final String SELECT = "select * from book_part where {0}";// 查询
				public static final String SELECT1 = "select * from book_part where %s";// 查询
				public static final String DELETE = " delete FROM book_part where %s";
				public static final String SELECT_COUNT = "select count(*) from book_part where {0}";
				public static final String DELETE_ALL = " delete FROM book_part";
				public static final String SELECT_ONLY_ID = "select "+FIELDS._ID+" FROM "+BOOKPART.TABLENAME+" where {0}";
				public static final String SELECT_CATEGORY = "SELECT DISTINCT category_id,category_name from book_part where %s";
				public static final String SELECT_PART = "SELECT * from book_part where %s";
				
				
			}
		}
		
		/**
		 * 电子书章节
		 */
		public interface BOOKCHAPTER {
			public static final String TABLENAME = "book_chapter";

			public interface FIELDS {
				public static final String ID = "id";
				public static final String _ID = "_id";
				public static final String NAME = "name";
				public static final String PID = "pid";
				public static final String PART_ID = "part_id";
				public static final String PART_NAME="part_name";
				public static final String CAN_ADD_RES = "can_add_res";
				public static final String EXT="ext";
			}

			public interface SQL {
				public static final String CREATE = "create table if not exists book_chapter (" +
						"id integer PRIMARY KEY AUTOINCREMENT, " +
						"_id int," +
						"name varchar(100), " +
						"pid int, " +
						"part_id int, " +
						"part_name varchar(200), " +
						"can_add_res int, " +
						"ext varchar(100));" ;
				
				public static final String DROP = "drop table if exists book_chapter";
				public static final String INSERT = "insert into book_chapter " +
						"(_id, " +
						"name,pid,part_id,part_name,can_add_res,ext ) " +
						"values(%s, '%s', %s,%s,'%s',%s,'%s') ";// 插入
				public static final String SELECT = "select * from book_chapter where {0}";// 查询
				public static final String SELECT1 = "select * from book_chapter where %s";// 查询
				public static final String DELETE = " delete FROM book_chapter where %s";
				public static final String SELECT_COUNT = "select count(*) from book_chapter where {0}";
				public static final String DELETE_ALL = " delete FROM book_chapter";
				public static final String SELECT_ONLY_ID = "select "+FIELDS._ID+" FROM "+BOOKCHAPTER.TABLENAME+" where {0}";
			}
		}
		
	}

}