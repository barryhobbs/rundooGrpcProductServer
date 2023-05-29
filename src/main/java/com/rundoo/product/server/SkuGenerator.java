package com.rundoo.product.server;

import com.rundoo.product.grpc.SkuComponents;

public class SkuGenerator {
    public static String generateSku(SkuComponents skuComponents){
        if(skuComponents == null){
            //should really protect against this sooner
            return "";
        }
        if(skuComponents.hasFullSku()){
            return skuComponents.getFullSku();
        }
        return String.format("%s%02d%06d", skuComponents.getCompanyId(),
                                         skuComponents.getCategoryId(),
                                         skuComponents.getSequenceId());
    }
}
