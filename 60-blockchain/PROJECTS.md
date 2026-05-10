# Blockchain Projects - Module 60

This module covers Hyperledger, Smart contracts, and Web3j for blockchain application development.

## Mini-Project: Hyperledger Fabric Chaincode (2-4 hours)

### Overview
Develop and deploy a smart contract (chaincode) on Hyperledger Fabric for supply chain tracking using Java.

### Project Structure
```
blockchain-demo/
├── chaincode/
│   ├── src/main/java/com/learning/chaincode/
│   │   ├── SupplyChainChaincode.java
│   │   ├── model/Product.java
│   │   └── model/Transaction.java
│   └── pom.xml
├── client/
│   ├── src/main/java/com/learning/client/
│   │   └── FabricClient.java
│   └── pom.xml
├── network/
│   ├── docker-compose.yml
│   └── configtx.yaml
└── scripts/
    └── deploy.sh
```

### Implementation

#### Chaincode pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>supply-chain-chaincode</artifactId>
    <version>1.0.0</version>
    
    <properties>
        <java.version>17</java.version>
        <fabric.version>2.4.7</fabric.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.hyperledger.fabric</groupId>
            <artifactId>fabric-chaincode-java</artifactId>
            <version>${fabric.version}</version>
        </dependency>
        <dependency>
            <groupId>org.hyperledger.fabric</groupId>
            <artifactId>fabric-chaincode-shim</artifactId>
            <version>${fabric.version}</version>
        </dependency>
        <dependency>
            <groupId>com.google.code.gson</groupId>
            <artifactId>gson</artifactId>
            <version>2.10.1</version>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>${java.version}</source>
                    <target>${java.version}</target>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>3.5.1</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

#### Chaincode Implementation
```java
// SupplyChainChaincode.java
package com.learning.chaincode;

import org.hyperledger.fabric.contract.annotation.*;
import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.Response;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.learning.chaincode.model.Product;
import com.learning.chaincode.model.Transaction;

import java.util.*;
import java.util.stream.Collectors;

@Contract(name = "SupplyChainContract", info = @Info(title = "", description = ""))
public class SupplyChainChaincode extends ChaincodeBase {
    
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    
    @Override
    public Response init(ChaincodeStub stub) {
        System.out.println("Supply Chain Chaincode initialized");
        return newSuccessResponse("Chaincode initialized successfully");
    }
    
    @Transaction
    public Response createProduct(ChaincodeStub stub, String productId, 
                                  String name, String description, String manufacturer) {
        
        String productJson = stub.getStringState(productId);
        if (productJson != null && !productJson.isEmpty()) {
            return newErrorResponse("Product already exists: " + productId);
        }
        
        Product product = new Product(productId, name, description, manufacturer, "CREATED");
        product.setTimestamp(System.currentTimeMillis());
        
        stub.putStringState(productId, gson.toJson(product));
        
        // Create transaction record
        createTransaction(stub, productId, "PRODUCT_CREATED", 
            "Product created: " + name, "manufacturer");
        
        return newSuccessResponse("Product created: " + productId, 
            gson.toJson(product).getBytes());
    }
    
    @Transaction
    public Response updateProductStatus(ChaincodeStub stub, String productId, 
                                        String newStatus, String location) {
        
        String productJson = stub.getStringState(productId);
        if (productJson == null || productJson.isEmpty()) {
            return newErrorResponse("Product not found: " + productId);
        }
        
        Product product = gson.fromJson(productJson, Product.class);
        String oldStatus = product.getStatus();
        product.setStatus(newStatus);
        product.setLocation(location);
        product.setTimestamp(System.currentTimeMillis());
        
        stub.putStringState(productId, gson.toJson(product));
        
        // Record transaction
        createTransaction(stub, productId, "STATUS_UPDATED",
            "Status changed from " + oldStatus + " to " + newStatus + 
            " at " + location, "system");
        
        return newSuccessResponse("Product status updated", gson.toJson(product).getBytes());
    }
    
    @Transaction
    public Response transferProduct(ChaincodeStub stub, String productId, 
                                   String fromParty, String toParty, String notes) {
        
        String productJson = stub.getStringState(productId);
        if (productJson == null || productJson.isEmpty()) {
            return newErrorResponse("Product not found: " + productId);
        }
        
        Product product = gson.fromJson(productJson, Product.class);
        
        // Record transfer in transactions
        createTransaction(stub, productId, "PRODUCT_TRANSFERRED",
            "Transferred from " + fromParty + " to " + toParty + 
            (notes != null ? ". Notes: " + notes : ""), toParty);
        
        return newSuccessResponse("Product transferred", gson.toJson(product).getBytes());
    }
    
    @Transaction
    public Response getProduct(ChaincodeStub stub, String productId) {
        
        String productJson = stub.getStringState(productId);
        if (productJson == null || productJson.isEmpty()) {
            return newErrorResponse("Product not found: " + productId);
        }
        
        return newSuccessResponse(productJson.getBytes());
    }
    
    @Transaction
    public Response getProductHistory(ChaincodeStub stub, String productId) {
        
        QueryResultsIterator<KeyValue> history = stub.getHistoryForKey(productId);
        List<Map<String, Object>> historyList = new ArrayList<>();
        
        for (KeyValue kv : history) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("value", kv.getStringValue());
            entry.put("txId", kv.getTxId());
            historyList.add(entry);
        }
        
        return newSuccessResponse(gson.toJson(historyList).getBytes());
    }
    
    @Transaction
    public Response getProductsByStatus(ChaincodeStub stub, String status) {
        
        QueryResultsIterator<KeyValue> iterator = stub.getStateByRange("", "");
        List<Product> products = new ArrayList<>();
        
        for (KeyValue kv : iterator) {
            Product product = gson.fromJson(kv.getStringValue(), Product.class);
            if (status.equals(product.getStatus())) {
                products.add(product);
            }
        }
        
        return newSuccessResponse(gson.toJson(products).getBytes());
    }
    
    private void createTransaction(ChaincodeStub stub, String productId, 
                                   String type, String description, String party) {
        
        Transaction tx = new Transaction(
            stub.getTxId(), productId, type, description, party, System.currentTimeMillis()
        );
        
        String txKey = productId + "_" + System.currentTimeMillis();
        stub.putStringState(txKey, gson.toJson(tx));
    }
    
    private Response newSuccessResponse(String message) {
        return newSuccessResponse(message, null);
    }
    
    private Response newSuccessResponse(String message, byte[] payload) {
        if (payload != null) {
            return ResponseUtils.successResponse(message, payload);
        }
        return ResponseUtils.successResponse(message);
    }
    
    private Response newErrorResponse(String message) {
        return ResponseUtils.errorResponse(message);
    }
}

// Product.java
package com.learning.chaincode.model;

public class Product {
    private String id;
    private String name;
    private String description;
    private String manufacturer;
    private String status;
    private String location;
    private long timestamp;
    
    public Product() {}
    
    public Product(String id, String name, String description, 
                   String manufacturer, String status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.manufacturer = manufacturer;
        this.status = status;
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}

// Transaction.java
package com.learning.chaincode.model;

public class Transaction {
    private String txId;
    private String productId;
    private String type;
    private String description;
    private String party;
    private long timestamp;
    
    public Transaction() {}
    
    public Transaction(String txId, String productId, String type, 
                       String description, String party, long timestamp) {
        this.txId = txId;
        this.productId = productId;
        this.type = type;
        this.description = description;
        this.party = party;
        this.timestamp = timestamp;
    }
    
    public String getTxId() { return txId; }
    public void setTxId(String txId) { this.txId = txId; }
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getParty() { return party; }
    public void setParty(String party) { this.party = party; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
```

#### Hyperledger Fabric Network
```yaml
# network/docker-compose.yml
version: '2.4'

services:
  ca:
    image: hyperledger/fabric-ca:2.4.7
    environment:
      - FABRIC_CA_HOME=/etc/hyperledger/fabric-ca-server
      - FABRIC_CA_SERVER_CA_NAME=ca
    ports:
      - "7054:7054"
    command: sh -c 'fabric-ca-server start -b admin:adminpw -d'

  orderer:
    image: hyperledger/fabric-orderer:2.4.7
    environment:
      - ORDERER_GENERAL_LISTENADDRESS=0.0.0.0
      - ORDERER_GENERAL_GENESISMETHOD=file
      - ORDERER_GENERAL_GENESISFILE=/var/hyperledger/orderer/orderer.genesis.block
    ports:
      - "7050:7050"
    volumes:
      - ./genesis.block:/var/hyperledger/orderer/orderer.genesis.block

  peer0:
    image: hyperledger/fabric-peer:2.4.7
    environment:
      - CORE_PEER_ADDRESS=peer0:7051
      - CORE_PEER_GOSSIP_BOOTSTRAP=peer0:7051
      - CORE_PEER_LOCALMSPID=Org1MSP
      - CORE_VM_ENDPOINT=unix:///host/var/run/docker.sock
    ports:
      - "7051:7051"
    volumes:
      - /var/run:/host/var/run
    depends_on:
      - orderer
      - ca
```

### Build and Deploy
```bash
# Build chaincode
cd chaincode
mvn clean package

# Build Docker image for chaincode
docker build -t supply-chain-chaincode:1.0 .

# Start Fabric network
cd network
docker-compose up -d

# Install chaincode on peer
peer lifecycle chaincode install supply-chain-chaincode.jar

# Package chaincode
peer lifecycle chaincode package supply-chain.tar.gz \
    --path /path/to/chaincode \
    --lang java \
    --label supply-chain-chaincode

# Approve chaincode definition
peer lifecycle chaincode approveformorg1 \
    --channelID supply-chain \
    --name supply-chain \
    --version 1.0 \
    --package-id <package-id> \
    --sequence 1

# Commit chaincode
peer lifecycle chaincode commit \
    --channelID supply-chain \
    --name supply-chain \
    --version 1.0 \
    --sequence 1

# Invoke chaincode
peer chaincode invoke \
    -C supply-chain \
    -n supply-chain \
    -c '{"function":"createProduct","args":["P001","Laptop","High-end laptop","TechCorp"]}'

# Query chaincode
peer chaincode query \
    -C supply-chain \
    -n supply-chain \
    -c '{"function":"getProduct","args":["P001"]}'
```

---

## Real-World Project: Enterprise Asset Tracking System (8+ hours)

### Overview
Build a complete enterprise asset tracking system using Hyperledger Fabric, Web3j for Ethereum integration, and a Spring Boot application for asset management.

### Project Structure
```
enterprise-asset-tracking/
├── fabric-network/
│   ├── organizations/
│   ├── chaincode/
│   │   └── asset-chaincode/
│   └── docker-compose.yaml
├── ethereum/
│   ├── contracts/
│   ├── web3j-integration/
│   └── pom.xml
├── application/
│   ├── src/main/java/
│   └── pom.xml
├── rest-api/
│   └── pom.xml
└── tests/
```

### Implementation

#### Web3j Ethereum Integration
```java
// pom.xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>asset-tracking</artifactId>
    <version>1.0.0</version>
    
    <properties>
        <java.version>17</java.version>
        <web3j.version>4.8.7</web3j.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.web3j</groupId>
            <artifactId>core</artifactId>
            <version>${web3j.version}</version>
        </dependency>
        <dependency>
            <groupId>org.web3j</groupId>
            <artifactId>abi</artifactId>
            <version>${web3j.version}</version>
        </dependency>
        <dependency>
            <groupId>org.web3j</groupId>
            <artifactId>crypto</artifactId>
            <version>${web3j.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    </dependencies>
</project>

// AssetContract.java (Generated from Solidity)
package com.learning.contracts;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

public class AssetContract extends Contract {
    
    public static final String BINARY = "";
    public static final Func<String> FUNC_REGISTERASSET = new Func<String>(
        "registerAsset",
        Arrays.asList(new TypeReference<Utf8String>() {}, 
                     new TypeReference<Utf8String>() {}),
        Collections.emptyList()
    );
    
    public static Func<BigInteger> FUNC_GETASSETCOUNT = new Func<BigInteger>(
        "getAssetCount",
        Collections.emptyList(),
        Collections.singletonList(new TypeReference<Uint256>() {})
    );
    
    protected AssetContract(String contractAddress, Web3j web3j, 
                           Credentials credentials, ContractGasProvider gasProvider) {
        super(BINARY, contractAddress, web3j, credentials, gasProvider);
    }
    
    public static AssetContract load(String contractAddress, Web3j web3j, 
                                     Credentials credentials, 
                                     BigInteger gasPrice, BigInteger gasLimit) {
        return new AssetContract(contractAddress, web3j, credentials, 
            new DefaultGasProvider(gasPrice, gasLimit));
    }
    
    public RemoteCall<TransactionReceipt> registerAsset(String assetId, 
                                                        String metadata) {
        return executeAsync(FUNC_REGISTERASSET, assetId, metadata);
    }
    
    public RemoteCall<BigInteger> getAssetCount() {
        return executeAsync(FUNC_GETASSETCOUNT);
    }
}

// AssetTrackingService.java
package com.learning.blockchain.service;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;
import org.springframework.stereotype.Service;
import com.learning.contracts.AssetContract;

import java.math.BigInteger;
import java.util.*;

@Service
public class AssetTrackingService {
    
    private final Web3j web3j;
    private final Credentials credentials;
    private final String contractAddress;
    
    public AssetTrackingService() {
        this.web3j = Web3j.build(new HttpService("http://localhost:8545"));
        this.credentials = Credentials.create(
            "private_key_here"
        );
        this.contractAddress = "0x...";
    }
    
    public AssetTrackingService(String rpcUrl, String privateKey, 
                                String contractAddress) {
        this.web3j = Web3j.build(new HttpService(rpcUrl));
        this.credentials = Credentials.create(privateKey);
        this.contractAddress = contractAddress;
    }
    
    public String registerAsset(String assetId, String metadata) throws Exception {
        
        AssetContract contract = AssetContract.load(
            contractAddress, 
            web3j, 
            credentials, 
            new DefaultGasProvider()
        );
        
        return contract.registerAsset(assetId, metadata)
            .send()
            .getTransactionHash();
    }
    
    public BigInteger getAssetCount() throws Exception {
        AssetContract contract = AssetContract.load(
            contractAddress, 
            web3j, 
            credentials, 
            new DefaultGasProvider()
        );
        
        return contract.getAssetCount().send();
    }
    
    public String transferAsset(String assetId, String from, 
                               String to, String data) throws Exception {
        
        // Implementation for asset transfer
        // Similar to registerAsset but calls transfer function
        return "tx_hash";
    }
    
    public List<AssetEvent> getAssetHistory(String assetId) throws Exception {
        // Query events from the blockchain
        List<AssetEvent> events = new ArrayList<>();
        
        // Use Web3j filter to get past events
        // EthFilter filter = new EthFilter(DefaultBlockParameterName.EARLIEST, 
        //     DefaultBlockParameterName.LATEST, contractAddress);
        
        return events;
    }
    
    public String getBalance(String address) throws Exception {
        return web3j.ethGetBalance(address, 
            DefaultBlockParameterName.LATEST).send().getBalance().toString();
    }
}

record AssetEvent(String eventType, String assetId, String from, 
                  String to, long timestamp, String transactionHash) {}
```

#### Spring Boot REST API
```java
// AssetController.java
package com.learning.blockchain.controller;

import com.learning.blockchain.model.Asset;
import com.learning.blockchain.service.AssetTrackingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/assets")
public class AssetController {
    
    private final AssetTrackingService assetTrackingService;
    
    public AssetController(AssetTrackingService assetTrackingService) {
        this.assetTrackingService = assetTrackingService;
    }
    
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerAsset(
            @RequestBody Asset asset) {
        
        try {
            String txHash = assetTrackingService.registerAsset(
                asset.getAssetId(), 
                asset.getMetadata()
            );
            return ResponseEntity.ok(Map.of(
                "status", "registered",
                "transactionHash", txHash
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getAssetCount() {
        try {
            var count = assetTrackingService.getAssetCount();
            return ResponseEntity.ok(Map.of("count", count));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/transfer")
    public ResponseEntity<Map<String, String>> transferAsset(
            @RequestBody TransferRequest request) {
        
        try {
            String txHash = assetTrackingService.transferAsset(
                request.getAssetId(),
                request.getFrom(),
                request.getTo(),
                request.getData()
            );
            return ResponseEntity.ok(Map.of(
                "status", "transferred",
                "transactionHash", txHash
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/{assetId}/history")
    public ResponseEntity<List<AssetEvent>> getAssetHistory(
            @PathVariable String assetId) {
        
        try {
            var history = assetTrackingService.getAssetHistory(assetId);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

record TransferRequest(String assetId, String from, String to, String data) {}

// Asset.java
package com.learning.blockchain.model;

public class Asset {
    private String assetId;
    private String name;
    private String description;
    private String metadata;
    private String owner;
    private long timestamp;
    
    public String getAssetId() { return assetId; }
    public void setAssetId(String assetId) { this.assetId = assetId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
```

### Build and Deploy
```bash
# Build chaincode
cd fabric-network/chaincode/asset-chaincode
mvn clean package -DskipTests

# Deploy Fabric network
cd fabric-network
docker-compose up -d

# Install and approve chaincode
peer lifecycle chaincode install asset-chaincode.jar
peer lifecycle chaincode approveformorg1 \
    --channelID asset-channel \
    --name asset-chaincode \
    --version 1.0 \
    --sequence 1

# Deploy smart contract to Ethereum
# Compile Solidity contract
solc AssetContract.sol --bin --abi --optimize -o output/

# Generate Java wrapper
web3j generate-solidity -b output/AssetContract.bin \
    -a output/AssetContract.abi \
    -o src/main/java \
    -p com.learning.contracts

# Build application
cd application
mvn clean package -DskipTests

# Run application
java -jar target/asset-tracking-app.jar

# Test API
curl -X POST http://localhost:8080/api/assets/register \
    -H "Content-Type: application/json" \
    -d '{"assetId":"ASSET001","name":"Laptop","metadata":"{\"serial\":\"SN123\"}"}'
```

### Learning Outcomes
- Develop Hyperledger Fabric chaincode in Java
- Integrate with Ethereum using Web3j
- Build REST APIs for blockchain interaction
- Implement asset tracking and transfer
- Configure multi-node Fabric network
- Handle blockchain events and transactions