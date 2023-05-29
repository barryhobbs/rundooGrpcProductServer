package com.rundoo.product.server;

import com.rundoo.product.grpc.AddProductRequest;
import com.rundoo.product.grpc.Product;
import org.bson.Document;

public class ProductDocument {
    public static Product makeProductFromDocument(Document document){
        return Product.newBuilder().setSku(document.getString("sku")).setCategory(document.getString("category")).setName(document.getString("name")).build();
    }

    public static Document makeDocumentFromProduct(Product product){
        Document document = new Document()
                .append("sku", product.getSku())
                .append("name", product.getName())
                .append("category", product.getCategory());
        return document;
    }

    public static Document makeDocumentFromRequest(AddProductRequest request){
        final String sku = SkuGenerator.generateSku(request.getSkuComponents());
        Document document = new Document()
                .append("sku", sku)
                .append("name", request.getName())
                .append("category", request.getCategory());
        return document;
    }
}
