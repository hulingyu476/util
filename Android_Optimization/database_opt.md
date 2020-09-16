# **性能优化之数据库优化**  
[网址]http://www.trinea.cn/android/database-performance/  
---
* __适用于Sqlite Mysql Sql server等数据库__
## 1. **索引**  
数据库中索引可以快速找到数据，而不用全表扫描，合适的索引可以大大提高数据库查询的效率。  
### （1）**优点**
加快了数据库检索速度，对单表、连表、分组查询、排序查询。   
经常是一到两个数量级的性能提升，且随数据级增长。
### （2）**缺点**
索引的创建和维护存在消耗，索引会占用物理空间，且随数据量的增加而增加。   
在对数据库进行增删改时需要维护索引，所以会对增删改的性能存在影响。
### （3）**分类**
a. 直接创建索引和间接创建索引
直接创建: 使用sql语句创建，Android中可以在SQLiteOpenHelper的onCreate或是onUpgrade中直接excuSql创建语句，语句如  
<code>CREATE INDEX mycolumn_index ON mytable (myclumn)</code>  
间接创建: 定义主键约束或者唯一性键约束，可以间接创建索引，主键默认为唯一索引。
b. 普通索引和唯一性索引
普通索引：  
<code>CREATE INDEX mycolumn_index ON mytable (myclumn)</code>  
唯一性索引：保证在索引列中的全部数据是唯一的，对聚簇索引和非聚簇索引都可以使用，语句为  
<code>CREATE UNIQUE COUSTERED INDEX myclumn_cindex ON mytable(mycolumn)</code>  
c. 单个索引和复合索引
d. 聚簇索引和非聚簇索引(聚集索引，群集索引)

### (4). **使用场景**
在上面讲到了优缺点，那么肯定会对何时使用索引既有点明白又有点糊涂吧，那么下面总结下：  
a.  当某字段数据更新频率较低，查询频率较高，经常有范围查询(>, <, =, >=, <=)或order by、group by发生时建议使用索引。并且选择度越大，建索引越有优势，这里选择度指一个字段中唯一值的数量/总的数量。  
b.  经常同时存取多列，且每列都含有重复值可考虑建立复合索引   
### (5). **索引使用规则**

## 使用事物
使用事务的两大好处是原子提交和更优性能。  
### (1)**原子提交**
原子提交意味着同一事物内的所有修改要买都完成要么都不完成，如果某个修改失败，会自动回滚使得所有修改不生效。  
### （2）**更优性能**
Sqlite默认会为每个插入、更新操作创建一个事物，并且在每次插入、更新后立即提交。  
这样如果连续插入100次数据实际是创建事物->执行语句->提交这个过程被重复执行了100次。如果我们显示的创建事物->执行100条语句->提交会使得这个创建事务和提交这个过程只做了一次，通过这种一次性事务可以使得性能大幅提升。尤其当数据库位于sd卡时，时间上能节省两个数量级左右。  
Sqlite显示使用事务，示例代码如下：  
```
public void insertWithOneTransaction() {
    SQLiteDatabase db = sqliteOpenHelper.getWritableDatabase();
    // Begins a transaction
    db.beginTransaction();
    try {
        // your sqls
        for (int i = 0; i < 100; i++) {
            db.insert(yourTableName, null, value);
        }
        // marks the current transaction as successful
        db.setTransactionSuccessful();
    } catch (Exception e) {
        // process it
        e.printStackTrace();
    } finally {
        // end a transaction
        db.endTransaction();
    }
}
```
其中sqliteOpenHelper.getWritableDatabase()表示得到写表权限。  

## **3、其他针对SQlite的优化**
### （1）语句的拼接使用StringBuilder代替String
简单的的string相加会导致创建多个临时对象消耗性能。StringBuilder的空间预分配性能好得多。如果你对字符串的长度有大致了解，如100字符左右，可以直接new StringBuilder(128)指定初始大小，减少空间不够时的再次分配。
### （2）查询时返回更少的结果级及更少的字段
### （3）少用cursor.getColumnIndex
根据性能调优过程中的观察cuusor.getColumnIndex的时间消耗跟cursor.getInt相差无几。可以在建表的时候用static变量记住某列的index，直接调用相应的index而不是每次查询。
```java
public static final String       HTTP_RESPONSE_TABLE_ID                  = android.provider.BaseColumns._ID;
public static final String       HTTP_RESPONSE_TABLE_RESPONSE            = "response";
public List<Object> getData() {
	……
	cursor.getString(cursor.getColumnIndex(HTTP_RESPONSE_TABLE_RESPONSE));
	……
}
```
优化为
```java
public static final String       HTTP_RESPONSE_TABLE_ID                  = android.provider.BaseColumns._ID;
public static final String       HTTP_RESPONSE_TABLE_RESPONSE            = "response";
public static final int          HTTP_RESPONSE_TABLE_ID_INDEX            = 0;
public static final int          HTTP_RESPONSE_TABLE_URL_INDEX           = 1;
public List<Object> getData() {
	……
	cursor.getString(HTTP_RESPONSE_TABLE_RESPONSE_INDEX);
	……
}
```
## **4、异步**
**Sqlite是一个内嵌式的数据库、数据库服务器就在你的程序中，无需网络配置和管理，数据库服务器端和客户端运行在同一进程内。**减少了网络访问的消耗，简化了数据库管理。不过Sqlite在并发、数据库大小、网络方面存在局限性，并且表现为级锁，所以也没要多线程操作。  
Android中的数据不多时查询可能耗时不多，不会导致anr，不会大于100ms时候不会感觉到延时和卡顿，可以放在线程中运行，但Sqlite在并发方面存在局限性，多线程控制较麻烦，这时候**可以使用单线程池，在任务中执行db操作，通过handler返回结果和ui线程交互。**即不影响UI线程，同时也能防止并发带来的异常。  
可使用Android提供的[AsyncQueryHandler](http://developer.android.com/reference/android/content/AsyncQueryHandler.html)或类似如下代码完成：
```java
ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
singleThreadExecutor.execute(new Runnable() {
 
	@Override
	public void run() {
		// db operetions, u can use handler to send message after
		db.insert(yourTableName, null, value);
		handler.sendEmptyMessage(xx);
	}
});
```

