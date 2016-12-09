package services;

/**
 * Created by Tamsen on 16/12/5.
 */
import org.apache.commons.dbcp2.BasicDataSource;
import javax.sql.DataSource;

public class MyDataSource {
    static DataSource getDataSource(String connectURI){
        BasicDataSource ds = new BasicDataSource();
        //MySQL的jdbc驱动
        ds.setDriverClassName("com.mysql.jdbc.Driver");
        ds.setUsername("root");              //所要连接的数据库名
        ds.setPassword("");                //MySQL的登陆密码
        ds.setUrl(connectURI);
        return ds;
    }
}
