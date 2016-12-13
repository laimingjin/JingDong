package services;

import entitys.ProductInfo;

import java.util.List;

/**
 * Created by minlai on 12/13/2016.
 */
public interface ProductService {
    /**
     * 爬取商品列表
     * @return
     */
    public List<ProductInfo> getProductList();
}
