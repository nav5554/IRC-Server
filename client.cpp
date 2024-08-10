#include <ws2tcpip.h>
#include <string>
#include <iostream>
#include <thread>

#pragma comment(lib, "Ws2_32.lib")
using namespace std;

SOCKET clientSocket;
SOCKET clientSocket2;
string msg;

//Enter ip address of server running chatServer.java

string ipaddress = "34.201.99.152";

//Enter port of server running chatServer.java

int port = 8088;



//Compiler Command
//g++ client.cpp -lws2_32 -lwininet -mwindows



void printStringToServer(string character, SOCKET& socket)
{
	send(socket, character.c_str(), character.size(), 0);
}

bool IsSocketConnected(SOCKET socket)
{
	string heartbeat = "   ";
	//10057 - Socket Not Connected
	//10054 - Connection Reset by Peer
	if(send(socket, heartbeat.c_str(), heartbeat.size(), 0) == SOCKET_ERROR)
	{
		cout << WSAGetLastError() << endl;
		return false;
	}
	else
	{
		return true;
	}
	
}

void disable_echo() {
    HANDLE hStdin = GetStdHandle(STD_INPUT_HANDLE); 
    DWORD mode;
    GetConsoleMode(hStdin, &mode);
    SetConsoleMode(hStdin, mode & (~ENABLE_ECHO_INPUT));
}

string deHeartbeatString(string str)
{
	string heartbeat = "muzer!@#tyrz";

	int found = str.find("muzer!@#tyrz");
	string newString = "";

	if(found != string::npos)
	{
		newString += str.substr(0, found);
		newString += str.substr((found + heartbeat.length()), str.length());
		return newString;

	}
	else
	{
		return str;
	}	
}


DWORD WINAPI chatServer(LPVOID LpParam)
{
	
	// Create a socket
	clientSocket2 = socket(AF_INET, SOCK_STREAM, 0);

	// Fill in the address structure
	sockaddr_in hint;
	hint.sin_family = AF_INET;

	//Port Number
	hint.sin_port = htons(port);

	hint.sin_addr.s_addr = inet_addr(ipaddress.c_str());
	
	connect(clientSocket2, (sockaddr*)&hint, sizeof(hint));
	
	if(IsSocketConnected(clientSocket2))
	{
		cout << "[+]Successfully Connected to Chat Server" << endl;
		cout << endl;
	}
	else
	{
		cout << "[!]Failed to Connect to Chat Server, try re-launching" << endl;
		cout << endl;
		
	}


	//Received all messages from chat server and print them out
	while(true)
	{
		char buffer[1024];
		int bytesReceived = recv(clientSocket2, buffer, sizeof(buffer), 0);
		buffer[bytesReceived] = '\0';
		string message(buffer);
		string realMessage = deHeartbeatString(message);

		if(realMessage != "")
		{
			cout << realMessage << endl;
		}
		message = "";
		realMessage = "";
		
	}
	return 0;
				


}

int main()
{
	cout << "[+]V1.0 Basic IRC Chat Server" << endl; 

	//Network Initialization
	WSADATA wsaData;

	WORD wVersion = MAKEWORD(2, 2);

	WSAStartup(wVersion, &wsaData);

	//Parses input into readable chat format
	string message;
	string messageTemp;
	string nickname;
	
	cout << "[+]Please enter a nickname" << endl;
	cout << ">";
	cin >> nickname;
	message = nickname;
	message = message + ":";

	cout << "[+]Attempting to Connect to Server" << endl;

	HANDLE hThread2 = CreateThread(NULL, 0, chatServer, NULL, 0, NULL);
	
	//Counter to keep track of messages sent
	int count = 0;

	while (true)
	{
		
	
		getline(cin, messageTemp);
		message = message + messageTemp;
		printStringToServer(message, clientSocket2);
		cin.clear();
		message = "";
		message = nickname;
		message += ":";
		
		//Checks connectivity to chat server every message
		if(!IsSocketConnected(clientSocket2) && count > 0)
		{
			cout << "[!]Error, Disconnected from server, try restarting" << endl;
		}
		
		count++;
						
	}

}



