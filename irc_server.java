import java.net.*;
import java.util.ArrayList;
import java.io.*;
import java.util.Iterator;
import java.util.Scanner;




public class irc_server {

    private ArrayList<Socket> clientStorage;
    private ServerSocket server;
  
    private boolean showMessages;

    //ENTER YOUR PORT TO HOST THE SERVER ON HERE
    //ENTER YOUR PORT TO HOST THE SERVER ON HERE
    //ENTER YOUR PORT TO HOST THE SERVER ON HERE
    public int port = 1111;
    //ENTER YOUR PORT TO HOST THE SERVER ON HERE
    //ENTER YOUR PORT TO HOST THE SERVER ON HERE
    //ENTER YOUR PORT TO HOST THE SERVER ON HERE

    
    //Constructor
    public irc_server(ArrayList<Socket> clientStorage, ServerSocket server)
    {
        this.clientStorage = clientStorage;
        this.server = server;
        this.showMessages = false;
            
    }

    public void showAllClients() throws IOException {
        for (int i = 0; i < clientStorage.size(); i++) {
            System.out.println("[+] " + i + " " + clientStorage.get(i).getRemoteSocketAddress().toString());
        }
        for(int i = 0; i <= 2; i++)
        {
            System.out.println();
        }
        System.out.print(">");

    }

    //Thread that is started for each client as they join
    public void handleClient(Socket s) throws IOException, InterruptedException
        {
            byte[] buffer = new byte[1024];
            InputStream inputStream = s.getInputStream();
            int bytesRead;
            boolean run = true;


            while(run)
            {
                try {

                    bytesRead = inputStream.read(buffer);

                   
                    
                    String msg = new String(buffer, "UTF-8");
                    if(this.showMessages)
                    {
                        System.out.println(msg);
                    }
                    
                    sendAll(s, msg);
                    buffer = new byte[1024];
                    bytesRead = 0;

                    
                } 
                catch (Exception e) {
                    
                }


            }


        }
  

    //This method checks for incoming connections
    public Socket acceptConnection() throws IOException {
        //Socket connection = null;

        Socket connection = this.server.accept();

        System.out.println();
        System.out.println("[+]Connection from " + connection.getRemoteSocketAddress() );
        System.out.print(">");
        sendAll(connection, "[+]User " + connection.getRemoteSocketAddress() + " has joined");
        new Thread(() -> {
            try {
                handleClient(connection);
            } catch (IOException e) {
                System.out.println("[+]Error Starting Thread");
                e.printStackTrace();
            } catch (InterruptedException e) {
                System.out.println("[+]Error Starting Thread");
                e.printStackTrace();
            }
        }).start();
        return connection;
    }

    //Iterates through all connected users and sends each message sent to all other users
    public void sendAll(Socket socket, String msg) throws IOException{

        Socket sendSocket;
        OutputStream outputStream;
        DataOutputStream dataOutputStream;

        //Synchronized and Iterator to prevent concurrent modification error
        synchronized (clientStorage) {
            Iterator<Socket> iterator = clientStorage.iterator();
            while(iterator.hasNext()) {

                sendSocket = iterator.next();

                //Making sure it doesnt send the message back to the sender
                if(!sendSocket.equals(socket))
                {
                    //Send Message
                    try {
                        outputStream = sendSocket.getOutputStream();
                        dataOutputStream = new DataOutputStream(outputStream);
                        dataOutputStream.write(msg.getBytes());
                        dataOutputStream.flush();
                    } catch (Exception e) {
                        System.out.println("[!]Error while sending message to socket");
                    }
                    
                }

            }
        }

    


    }

    public static void main(String[] args) throws IOException { 
        

        System.out.println("[+]V1.0 Basic IRC Chat Server");
        System.out.println("[+]Creating Chat Server");

        //Created socket server
        ServerSocket server = new ServerSocket(8088);

        //Created object container to store all clients
        ArrayList<Socket> clients = new ArrayList<Socket>();

        //Initializing chatServer object
        irc_server chatServer = new irc_server(clients, server);

        //This thread adds incoming connections to client storage container
        Thread manageConnections = new Thread(() -> {

            while (true) {
                try {
                    chatServer.clientStorage.add(chatServer.acceptConnection());
                } catch (Exception e) {
                    System.out.println(e);
                }

            }

        });

        //Heartbeat thread
        Thread heartbeat = new Thread(() -> {
            while (true) {

                //Iterator for all connected clients
                Iterator<Socket> iterator = chatServer.clientStorage.iterator();
                while (iterator.hasNext()) {

                    Socket s = iterator.next();

                    //Attempts to send "heartbeat" which is random string
                    //If unable to send, removes client from array and declares client disconnected
                    try {
                        OutputStream heartbeatStream = s.getOutputStream();
                        heartbeatStream.write("muzer!@#tyrz".getBytes());
                        heartbeatStream.flush();
                    } catch (IOException e) {
                        System.out.println("[+] Client " + s.getRemoteSocketAddress() + " disconnected");
                        System.out.print(">");
                        try {
                            chatServer.sendAll(s, "[+] " + s.getRemoteSocketAddress() + " has left the server");
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        iterator.remove(); // Remove the current element from the list

                    }

                }
                
                //Stops thread for 5 seconds to decrease computing usage
                try {
                    Thread.sleep(5000);
                } catch (Exception e) {
                    System.out.println(e);
                }

            }
        });


        
        Scanner userScanner = new Scanner(System.in);
        
        System.out.println("[+]Listening for incoming connections");
        manageConnections.start();
        heartbeat.start();

        for(int i = 0; i <= 6; i++)
        {
            System.out.println();
        }

        
        
        while(true)
        {
            System.out.println("[+]What would you like to do: ");
            System.out.println("a) View all clients connected");
            System.out.println("b) Toggle Live Chat");
            System.out.println("c) Disconnect User from chat");
            System.out.println();
            System.out.print(">");

            String response = userScanner.nextLine();

            if(response.equals("a"))
            {
                chatServer.showAllClients();
            }

            if(response.equals("b"))
            {
                if(!chatServer.showMessages)
                {
                    chatServer.showMessages = true;
                }
                else
                {
                    chatServer.showMessages = false;
                }
                
            }

            if(response.equals("c"))
            {
                System.out.println("[+]Which client would you like to disconnect");
                System.out.println();
                chatServer.showAllClients();
                System.out.print(">");

                response = userScanner.nextLine();
                int clientIndex = Integer.valueOf(response);

                Socket clientToRemove = chatServer.clientStorage.get(clientIndex);
                clientToRemove.close();
                chatServer.clientStorage.remove(clientIndex);
                System.out.println("[+]Sucessfully Closed Client");
                chatServer.sendAll(null, "[+]" + clientToRemove.getRemoteSocketAddress() + " Has been forcibly removed from server by administrator");



            }

        }
        

        
    }

    
    
}



