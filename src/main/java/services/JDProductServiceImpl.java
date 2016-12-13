package services;

/**
 * Created by minlai on 12/13/2016.
 */
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import entitys.ProductInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utils.Constants;
import utils.PriceCheckUtil;

public class JDProductServiceImpl implements  ProductService{
    private String productName;
    private String jdUrl;
    private static PriceCheckUtil pcu = PriceCheckUtil.getInstance();

    public JDProductServiceImpl(String jdUrl, String productName){
        this.jdUrl = jdUrl;
        this.productName = productName;
    }

    public List<ProductInfo> getProductList() {
        List<ProductInfo> jdProductList = new ArrayList<ProductInfo>();
        ProductInfo productInfo = null;
        String url = "";
        for(int i = 0; i < 10; i++){
            try {
                System.out.println("JD Product 第[" + (i + 1) + "]页");
                if(i == 0) {
                    url = jdUrl;
                }else{
                    url = Constants.JDURL + pcu.getGbk(productName) + Constants.JDENC + Constants.JDPAGE + (i + 1);
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
                        Element div = li.select("div[class=gl-i-wrap]").first();
                        Elements title = div.select("div[class=p-name p-name-type-2]>a");
                        String productName = title.attr("title"); //得到商品名称
                        Elements price = div.select(".p-price>strong");
                        String productPrice =price.attr("data-price"); //得到商品价格
                        productInfo = new ProductInfo();
                        productInfo.setProductName(productName);
                        productInfo.setProductPrice(productPrice);
                        System.out.println("productInfo:   "+productInfo.getProductName()+" "+productInfo.getProductPrice());
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
            String productName = "书包";
           // String jdUrl = Constants.JDURL + pcu.getGbk(productName)  + Constants.JDENC;
            String jdUrl = Constants.JDURL + productName  + Constants.JDENC;
            List<ProductInfo> list = new JDProductServiceImpl(jdUrl, productName).getProductList();
            System.out.println(list.size());
            for(ProductInfo pi : list){
                System.out.println(pi.getProductName() + "  " + pi.getProductPrice());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
