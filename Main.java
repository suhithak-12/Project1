/*Suhithareddy Kantareddy 
 * 09/30/2024
 * CSE 3053
 * Project 1 
 * Main file
 * errors: I did this project as instructed and was able most of the code working. 
 *         The only problem I am running through is when Node C runs, it prints out the data lines from confA.txt instead of confB.txt.
 *         I think this is becasue when the program runs, nodeB is set to read cofA.txt and i think that nodeB is now cosidering data lines from confA to be in confB. 
 *         I am not really sure what is causing this error as I have checked my code over and over againmultiple times adn cannot figure it out. 
 */
import java.io.*;
import java.net.*;


class NodeA implements Runnable {
    private String configFile;

    public NodeA(String configFile) {
        this.configFile = configFile;
    }

    public void run() {
        try (BufferedReader br = new BufferedReader(new FileReader(configFile))) {
            String portStr = br.readLine();
            if (portStr == null || portStr.isEmpty()) {
                throw new NumberFormatException("Invalid port number in " + configFile);
            }
            int portB = Integer.parseInt(portStr);  // Reading port number from configuration file
            

            Socket socket = new Socket("localhost", portB);      // creating socket to connect to Node B
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            String line;
            
            
            while ((line = br.readLine()) != null) {    // reading data lines from configuration file A and send to Node B
                out.println(line);
            }
            
            out.println("stop"); // message printing that node B is finihsed getting data lines from A.
            socket.close();
        } catch (FileNotFoundException e) {
            System.err.println("Configuration file not found: " + configFile); //if Configuration file is missing or not found.
        } catch (IOException e) {
            System.err.println("IO Error in NodeA: " + e.getMessage()); // if there are errors in node a 
        } catch (NumberFormatException e) {
            System.err.println("Invalid number format in NodeA: " + e.getMessage()); //if node A has format errors.
        }
    }
}

class NodeB implements Runnable {
    private String configFile;

    public NodeB(String configFile) {
        this.configFile = configFile;
    }

   
    public void run() {
        try (BufferedReader br = new BufferedReader(new FileReader(configFile))) {
            String portBStr = br.readLine();
            String portCStr = br.readLine();
            if (portBStr == null || portBStr.isEmpty() || portCStr == null || portCStr.isEmpty()) {
                throw new NumberFormatException("Invalid port number in " + configFile);
            }
            // Read port numbers Node B is listening on and Node C
            int portB = Integer.parseInt(portBStr);
            int portC = Integer.parseInt(portCStr);
            
            // Create server socket for Node B to listen on
            ServerSocket serverSocket = new ServerSocket(portB);
            Socket socketFromA = serverSocket.accept(); // Accept connection from Node A
            BufferedReader in = new BufferedReader(new InputStreamReader(socketFromA.getInputStream()));
            
            // Create socket to connect to Node C
            Socket socketToC = new Socket("localhost", portC);
            PrintWriter outToC = new PrintWriter(socketToC.getOutputStream(), true);
            
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println("Node B received: " + line);
                if (line.equals("stop")) break;
                outToC.println(line);
            }
            outToC.println("stop");
            socketFromA.close();
            socketToC.close();
            serverSocket.close();
        } catch (FileNotFoundException e) {
            System.err.println("Configuration file not found: " + configFile);
        } catch (IOException e) {
            System.err.println("IO Error in NodeB: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Invalid number format in NodeB: " + e.getMessage());
        }
    }
}

class NodeC implements Runnable {
    private String configFile;

    public NodeC(String configFile) {
        this.configFile = configFile;
    }

    @Override
    public void run() {
        try (BufferedReader br = new BufferedReader(new FileReader(configFile))) {
            String portCStr = br.readLine();
            if (portCStr == null || portCStr.isEmpty()) {
                throw new NumberFormatException("Invalid port number in " + configFile);
            }
            
            int portC = Integer.parseInt(portCStr); // reading port number Node C is listening on
            
            
            ServerSocket serverSocket = new ServerSocket(portC);    // creating server socket for Node C to listen on
            Socket socketFromB = serverSocket.accept();             // accepting connection from Node B
            BufferedReader in = new BufferedReader(new InputStreamReader(socketFromB.getInputStream()));
            
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println("Node C received: " + line);
                if (line.equals("stop")) break;
            }
            socketFromB.close();
            serverSocket.close();
        } catch (FileNotFoundException e) {
            System.err.println("Configuration file not found: " + configFile);
        } catch (IOException e) {
            System.err.println("IO Error in NodeC: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Invalid number format in NodeC: " + e.getMessage());
        }
    }
}

public class Main {
    public static void main(String[] args) {
        //threads for all nodess
        Thread nodeA = new Thread(new NodeA("confA.txt"));
        Thread nodeB = new Thread(new NodeB("confB.txt"));
        Thread nodeC = new Thread(new NodeC("confC.txt"));

        // Start all nodes
        nodeA.start();
        nodeB.start();
        nodeC.start();

        // Wait for threads to finish
        try {
            nodeA.join();
            nodeB.join();
            nodeC.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
