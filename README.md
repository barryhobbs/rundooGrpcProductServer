Getting started:
This will need java 17, and a local mongoDb instance running on port 27017.  To set up a new mongo instance easily, visit https://www.mongodb.com/docs/manual/  
There is a web server that fronts this gRPC service here: https://github.com/barryhobbs/rundooProductWeb

To build:
```
mvn clean install
``` 

To execute:
```
mvn spring-boot:run
```

Command Line examples for local testing.  grpcurl is readily available via brew install.


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