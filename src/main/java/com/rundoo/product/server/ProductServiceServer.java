package com.rundoo.product.server;

import com.mongodb.MongoCommandException;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.rundoo.product.grpc.*;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import org.bson.conversions.Bson;

import java.io.IOException;

public class ProductServiceServer {

    private final int port;
    private final Server server;
    private final MongoCollection<Document> collection;

    public ProductServiceServer(int port) {
        this.port = port;
        this.server = ServerBuilder.forPort(port).addService(new ProductService()).build();
        this.collection = initializeMongoDB();
    }

    private MongoCollection<Document> initializeMongoDB() {
        String connectionString = "mongodb://localhost:27017";
        MongoCollection<Document> internalCollection = null;
        try {
            MongoDatabase database;
            com.mongodb.client.MongoClient mongoClient = MongoClients.create(connectionString);
            database = mongoClient.getDatabase("rundooProduct");
            internalCollection = database.getCollection("products");
            // This is terrible, and you never do this in this place the real world.
            try {
                internalCollection.createIndex(new BsonDocument()
                        .append("sku", new BsonString("text"))
                        .append("name", new BsonString("text"))
                        .append("category", new BsonString("text")));
            } catch(MongoCommandException e){
                //this likely already exists, carry on.
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return internalCollection;
    }

    public void start() throws IOException {
        server.start();
        System.out.println("gRPC server started on port " + port);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("*** Shutting down gRPC server since JVM is shutting down");
            ProductServiceServer.this.stop();
            System.err.println("*** Server shut down");
        }));
    }

    public void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    private class ProductService extends ProductServiceGrpc.ProductServiceImplBase {

        @Override
        public void addProduct(AddProductRequest request, StreamObserver<AddProductResponse> responseObserver) {
            try {
                Document document = ProductDocument.makeDocumentFromRequest(request);

                System.out.println("Hi there");
                assert collection != null;
                collection.insertOne(document);
                System.out.println("Hi there 2");
                // This is a little wasteful, but ya know, shortcuts.
                responseObserver.onNext(AddProductResponse.newBuilder().setProduct(ProductDocument.makeProductFromDocument(document)).build());
                responseObserver.onCompleted();
            } catch (Exception e) {
                responseObserver.onError(e);
            }
        }

        @Override
        public void findProduct(FindProductRequest request, StreamObserver<FindProductResponse> responseObserver) {
            try {
                FindProductResponse.Builder response = FindProductResponse.newBuilder();
                Bson filter = Filters.text(request.getSearchString());
                collection.find(filter).forEach(doc -> response.addProducts(ProductDocument.makeProductFromDocument(doc)));
                responseObserver.onNext(response.build());
                responseObserver.onCompleted();
            } catch (Exception e) {
                responseObserver.onError(e);
            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        int port = 50051;
        ProductServiceServer server = new ProductServiceServer(port);
        server.start();
        server.server.awaitTermination();
    }
}
