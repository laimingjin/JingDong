package services;

/**
 * Created by minlai on 12/13/2016.
 */
import entitys.ProductInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utils.Constants;
import utils.PriceCheckUtil;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class JDProductServiceImpl implements  ProductService{
    private String productKindName;
    private String jdUrl;
    private static PriceCheckUtil pcu = PriceCheckUtil.getInstance();

    public JDProductServiceImpl(String jdUrl, String productKindName){
        this.jdUrl = jdUrl;
        this.productKindName = productKindName;
    }

    public List<ProductInfo> getProductList() {
        List<ProductInfo> jdProductList = new ArrayList<ProductInfo>();
        ProductInfo productInfo = null;
        String productid;
        String productName;
        String productPrice;
        String productImageUrl;
        String tradeNum;
        String productUrl;
        String shopName;
        String ecName;
        Date date;

        String url = "";
        for(int i = 0; i < 10; i++){
            try {
                System.out.println("JD Product 第[" + (i + 1) + "]页");
                if(i == 0) {
                    url = jdUrl;
                }else{
//                    url = Constants.JDURL + pcu.getGbk(productKindName) + Constants.JDENC + Constants.JDPAGE + (i + 1);
                    url = Constants.JDURL + productKindName + Constants.JDENC + Constants.JDPAGE + (i + 1);
                }
                System.out.println(url);
                Document document = Jsoup.connect(url).timeout(5000).get();
                Elements uls = document.select("ul[class=gl-warp clearfix]");
                Iterator<Element> ulIter = uls.iterator();
                while(ulIter.hasNext()) {
                    Element ul = ulIter.next();
                    Elements lis = ul.select("li[data-sku]");
                    Iterator<Element> liIter = lis.iterator();
                    while(liIter.hasNext()) {
                        Element li = liIter.next();
                        productInfo = new ProductInfo();
                        productid=li.attr("data-sku");
                        Element div = li.select("div[class=gl-i-wrap]").first();
                        Elements image=div.select("div[class=p-img]>a");
                        String tempImage=image.select(">img").attr("src");
                        if(tempImage==null || tempImage.equals("")){
                            productImageUrl=image.select(">img").attr("data-lazy-img");
                        }else{
                            productImageUrl=image.select(">img").attr("src");
                        }
                        
                       
                        productUrl=image.attr("href");
                        Elements title = div.select("div[class=p-name p-name-type-2]>a");
                        productName = title.attr("title");
                        Elements price = div.select(".p-price>strong");
                        productPrice =price.attr("data-price"); //得到商品价格
                        Elements commit=div.select("div[class=p-commit]>strong>a");
                        tradeNum=commit.text();
                        Elements shop=div.select("div[class=p-icons]>img");
                        shopName=shop.attr("data-tips");

                        productInfo.setProductId(productid);
                        productInfo.setProductName(productName);
                        productInfo.setProductPrice(productPrice);
                        productInfo.setProductImageUrl(productImageUrl);
                        productInfo.setTradeNum(tradeNum);
                        productInfo.setProductUrl(productUrl);
                        productInfo.setShopName(shopName);
                        productInfo.setEcName("京东");
                        productInfo.setDate(new Date());
                        //System.out.println("productInfo:   "+productid+" "+productName+" "+productPrice+" "+productImageUrl+" "+tradeNum+" "+productUrl+shopName);
                        jdProductList.add(productInfo);
                    }
                }
            } catch(Exception e) {
                System.out.println("Get JD product has error [" + url + "]");
                System.out.println(e.getMessage());
            }
        }
        return jdProductList;
    }

    public static void main(String[] args) {
        try {
            String productKindName = "铅笔";
           // String jdUrl = Constants.JDURL + pcu.getGbk(productName)  + Constants.JDENC;
            String jdUrl = Constants.JDURL + productKindName  + Constants.JDENC;
            List<ProductInfo> list = new JDProductServiceImpl(jdUrl, productKindName).getProductList();
            System.out.println(list.size());
            DBHelper dbHelper=new DBHelper();
            Connection connection=dbHelper.connection;
            String sql="insert into jingdong values(?,?,?,?,?,?,?,?,?)";
            ArrayList<String> params=null;
            for(ProductInfo pi : list){
                params=new ArrayList<String>();
                params.add(pi.getProductId());
                params.add(pi.getProductName());
                params.add(pi.getProductPrice());
                params.add(pi.getProductImageUrl());
                params.add(pi.getTradeNum());
                params.add(pi.getProductUrl());
                params.add(pi.getShopName());
                params.add(pi.getEcName());
                params.add(pi.getDate().toString());
                //System.out.println(pi.getProductName() + "  " + pi.getProductPrice());
              dbHelper.add(connection,sql,params);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
