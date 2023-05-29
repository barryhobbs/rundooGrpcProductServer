package com.rundoo.product.server;

import com.rundoo.product.grpc.SkuComponents;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SkuGeneratorTest {

    @Test
    void generateSkuShouldReturnFullSkuIfExists() {
        assertEquals("fullSku", SkuGenerator.generateSku(SkuComponents.newBuilder().setFullSku("fullSku").build()));
    }

    @Test
    void generateSkuShouldComposeSku() {
        assertEquals("RD01000001", SkuGenerator.generateSku(SkuComponents.newBuilder().setCategoryId(1).setCompanyId("RD").setSequenceId(1).build()));
    }

    // should work on enforcing the size limits here, but that's for later.
}