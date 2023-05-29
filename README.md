grpcurl command to hit addproduct:

```grpcurl -plaintext -d '{"skuComponents": {"full_sku": "BH01000002"}, "name": "Example Product", "category": "Electronics"}' -import-path src/main/proto -proto product.proto localhost:50051 product.ProductService/AddProduct```
yields:
```
{
  "product": {
    "sku": "{\"sku\": \"BH01000002\", \"name\": \"Example Product\", \"category\": \"Electronics\", \"_id\": {\"$oid\": \"6475189326456c6a07f3a456\"}}"
  }
}
```

grpcurl command to hit findproduct:

```
grpcurl -plaintext -d '{"search_string": "candy"}' -import-path src/main/proto -proto product.proto localhost:50051 product.ProductService/FindProduct
```
yields:
```
{
  "products": [
    {
      "sku": "BH01000005",
      "name": "Milky Way",
      "category": "Candy"
    },
    {
      "sku": "BH01000004",
      "name": "Snickers",
      "category": "Candy"
    }
  ]
}
```

```
grpcurl -plaintext -d '{"search_string": "bird"}' -import-path src/main/proto -proto product.proto localhost:50051 product.ProductService/FindProduct 
```

yields:
```
{
  "products": [
    {
      "sku": "BC01000005",
      "name": "Bird Feeder",
      "category": "Home Products"
    }
  ]
}
```