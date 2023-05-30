## This is the gRPC server for Rundoo's take-home interview problem.

This will need java 17, and a local mongoDb instance running on port 27017.  To set up a new mongo instance easily, 
visit https://www.mongodb.com/docs/manual/  

There is a very basic web server that fronts this gRPC service here: https://github.com/barryhobbs/rundooProductWeb

# How to nose through the code:
This is a basic gRPC server executed via ProductServiceServer#main().  It contains an internal class, ProductService, 
that fulfills the product.proto contract for AddProduct and FindProduct.  In reality, this of course would be backed by 
significantly larger and more stable services that are more oriented around cloud deployments that we can scale/etc, 
but for this demo, it's completely sufficient.  

There is intentionally very little protective logic around the creation of new products, as I wanted to keep it 
relatively clean and obvious, without the usual aspect-oriented code you would add for authentication, 
duplicate protection, sku-format enforcement, retry/error-handling, multithreading, caching, etc.  
I have also avoided the usual fast properties  and other configuration files that you would use in reality, hence 
simple, hard-coded values for port, host, etc.

Start at ProductServiceServer, and find the main() method.  It will initialize the database connection and start the 
gRPC service, listening on port 50051.  The two interesting methods are addProduct() and findProduct(), and they do 
pretty much what you would expect for a simple CRUD interface.  

The SkuGenerator class is a LITTLE overbuilt for what we minimally require per 
the requirements, but because there was a reference in the requirements around good sku-building practices, I added 
some logic that would create a sku from the expected components if the user doesn't provide their own custom sku.

MongoDB supports compound indexes and a generic text search across the fields you specify.  It's not 100% out of the box, but good enough for basic
demonstration purposes, so I leverage this feature as a cheap and cheerful 80% solution prior to digging in and 
hyper-customizing the search method.

The proto specification is pretty generic, but I did add a few compound objects around the components of building a 
well-formed sku as part of the interface, as well as a proper Product scheme, as that makes the return on the search 
trivial as a repeated element.  In reality, I would probably not overspecify this proto spec unless we had an absolute 
need.  In real production with real external customers, I prefer to misuse gRPC in places, and then build my own parser
for a blob of data, as this allows the flexibility of adding on-the-fly rules to a production system without a 
deployment, and provides an easy mechanism for reporting when we see things go awry, versus the normal errors you get 
from gRPC where you just never know why it didn't like whatever format you sent it.

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