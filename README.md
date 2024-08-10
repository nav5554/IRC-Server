# IRC-Server
A simple IRC Chat Server written in Java and C++

## Introduction
This project is an simple lightweight *Internet Relay Chat* (IRC) server for educational purposes written with C++ and Java. It is relatively easy to configure, only using a client side and a server side script.

*Below is a detailed description on how to set up the IRC server using a free AWS EC2 Server instance*

## Setup

### Creating the Server

1. Create a free AWS account (https://aws.amazon.com/free/)
2. Once you have created the account, navigate to your dashboard click services -> Compute -> EC2
3. From here, click *Launch Instance* and you will be greeted with the EC2 setup configuration
4. For your application and OS Images, choose `ubuntu`
5. For your instance type, choose t2.micro. It should say Free tier eligible meaning you will not be charged as long as you stay under 750 hours. You can choose and instance type.
6. Under key pair, hit Create new key pair and choose that key pair as the key pair name required to connect to the instance. The key pair should automatically download a .pem file.
7. For your network settings, hit `edit`. Then under Inbound Security Group Rules, add a custom TCP security group rule and choose your desired port you would like to use to connect as the port range.
8. Then `Launch Instance`

### Connecting to and Configuring the Server

1. To connect to the server, navigate to the directory in which your key pair is stored and open a terminal.
2. Enter the command `ssh ubuntu@[ip address of server] -i [name of key pair file] `
3. Once connected to the server, enter the following commands to install the java runtime environment on your server
4. `sudo apt install defaul-jre`

### Starting IRC Server
1. Enter the command `nano server.java` to create a new java file.
2. Copy and past the java code from irc_server.java into the java file you just created
3. Now edit the variable called port at the top of the code and change the number to the port you entered in your inbound security group rule while creating the EC2 server
4. Save and exit the file
5. Enter the command `java server.java` to run the server

### Setting up client
1. The following steps requires the GNU g++ compiler
2. Download the client.cpp file from the repository and open the file in any text or code editor
3. At the top of the file, change variables `ipaddress` and `port` to the ipaddress of your EC2 instance and the port used in the new inbound security rule
4. Now navigate to the directory in which the cpp file is and open a terminal
5. Enter the command `g++ client.cpp -lws2_32 -lwininet -o client`
6. Now locate the .exe file generated. When you run the file you will be prompted to enter a nickname. Once you enter your nickname, you will then automatically connect to the IRC server.
