package main;

import models.Book;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import services.MySQLControl;
import utils.URLFecter;

import java.util.List;

/**
 * Created by Tamsen on 16/12/5.
 */
public class Main {
    static  final Log logger= LogFactory.getLog(Main.class);
    public static void  main(String[] args)throws Exception{
        HttpClient client=new DefaultHttpClient();
        String url="http://search.jd.com/Search?keyword=Python&enc=utf-8&book=y&wq=Python&pvid=33xo9lni.p4a1qb";
        List<Book> bookList= URLFecter.URLParser(client,url);
        
            //循环输出抓取的数据
            for (Book jd:bookList) {
                logger.info("bookID:"+jd.getBookID()+"\t"+"bookPrice:"+jd.getBookPrice()+"\t"+"bookName:"+jd.getBookName());
            }
            //将抓取的数据插入数据库
            MySQLControl.executeInsert(bookList);
        
    }
}
