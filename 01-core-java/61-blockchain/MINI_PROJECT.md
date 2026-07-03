# Module 61: Blockchain & Web3 in Java - Mini Project

**Project Name**: Blockchain Wallet & Transaction Viewer  
**Difficulty Level**: Advanced  
**Estimated Time**: 2-3 hours

---

## 🎯 Objective
Use the Web3j library to connect to the Ethereum blockchain, generate a new cryptographic wallet, check the ETH balance of an account, and listen for new blocks being added to the chain in real-time using Reactive Streams (RxJava).

## 📝 Requirements

### Core Features

1. **Project Setup**:
   - Create a Maven project and include the `org.web3j:core` dependency.
   - You will need a connection to an Ethereum node. For this project, register for a free account at Infura or Alchemy to get an HTTP/WebSocket endpoint for the Ethereum Mainnet or a Testnet (like Sepolia).

2. **Web3j Initialization**:
   - Initialize a `Web3j` instance connecting to your Infura/Alchemy endpoint using `HttpService`.

3. **Wallet Creation**:
   - Use `WalletUtils.generateFullNewWalletFile(...)` to generate a new Keystore file locally.
   - Load the credentials using `WalletUtils.loadCredentials(...)`.
   - Print the public address of the newly created wallet to the console.

4. **Balance Checking**:
   - Write a method to fetch the balance of a known Ethereum address (e.g., a massive exchange wallet or Ethereum founder Vitalik Buterin's public address).
   - Use `web3.ethGetBalance(address, DefaultBlockParameterName.LATEST).send()`.
   - The result is returned in *Wei* (the smallest unit of Ether). Use `Convert.fromWei(balance.toString(), Convert.Unit.ETHER)` to convert and print it in a human-readable ETH format.

5. **Real-time Block Listening (RxJava)**:
   - Web3j uses RxJava under the hood. 
   - Call `web3.blockFlowable(false).subscribe(...)`.
   - In the subscriber, print out the Block Number and the number of transactions contained in that block as they are mined in real-time.

---

## 💡 Solution Blueprint

1. **Dependency**:
   ```xml
   <dependency>
       <groupId>org.web3j</groupId>
       <artifactId>core</artifactId>
       <version>4.10.0</version>
   </dependency>
   ```

2. **The Implementation**:
   ```java
   import org.web3j.protocol.Web3j;
   import org.web3j.protocol.http.HttpService;
   import org.web3j.protocol.core.methods.response.EthGetBalance;
   import org.web3j.protocol.core.DefaultBlockParameterName;
   import org.web3j.utils.Convert;

   import java.math.BigDecimal;

   public class Web3Demo {
       public static void main(String[] args) throws Exception {
           // 1. Connect to node
           String infuraUrl = "https://mainnet.infura.io/v3/YOUR_PROJECT_ID";
           Web3j web3 = Web3j.build(new HttpService(infuraUrl));
           
           // Print Client Version
           System.out.println("Connected to: " + web3.web3ClientVersion().send().getWeb3ClientVersion());

           // 2. Check Balance of a known address
           String address = "0xd8dA6BF26964aF9D7eEd9e03E53415D37aA96045"; // Vitalik's address
           EthGetBalance balanceWei = web3.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();
           BigDecimal balanceEth = Convert.fromWei(balanceWei.getBalance().toString(), Convert.Unit.ETHER);
           System.out.println("Balance of " + address + ": " + balanceEth + " ETH");

           // 3. Listen for new blocks
           System.out.println("Listening for new blocks...");
           web3.blockFlowable(false).subscribe(block -> {
               System.out.println("New Block Mined! Number: " + block.getBlock().getNumber() + 
                                  " | Tx Count: " + block.getBlock().getTransactions().size());
           });
       }
   }
   ```