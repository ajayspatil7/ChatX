# MultiChat Application

Welcome to MultiChat Application, a powerful and flexible chat application built using Java. It provides users with a rich set of features for communication, including private messaging, blocking and unblocking users, and message formatting options. MultiChat Application aims to offer a seamless chat experience with an intuitive user interface and robust server-side management.


## Table of Contents

- [Features](#Features)
- [Usage](#Usage)
- [Commands](#Commands)
- [System Requirements](#System-Requirements)
- [Installation](#How-to-Use-This)


## Features

- **Private Messaging**: Create private groups for secure and direct communication.
- **Blocking and Unblocking**: Block or unblock other users as needed.
- **Message Formatting**: Choose message format modes like "small," "capital," or "both."
- **Responsive User Interface**: An easy-to-use client interface with chat area and input field.
- **Real-time Chat**: Experience fast and efficient chat capabilities with low latency.
- **Group Management**: Add or remove users from private groups easily.
- **Cross-Platform**: Compatible with multiple operating systems.

## Usage

1. **Server**:
   - Run the `Server.java` file to start the server.
   - The server will listen on port `8888`.

2. **Client**:
   - Run the `Client.java` file to launch the client.
   - Enter your name and connect to the server.

## Commands

In the chat application, you can use the following commands:

- `CMD:PM ENTER`: Enter private messaging mode.
- `CMD:PM LEAVE`: Leave private messaging mode.
- `CMD:PM ADD [client name(s)]`: Add clients to your private group.
- `CMD:PM REMOVE [client name(s)]`: Remove clients from your private group.
- `CMD:PM PRINT`: Print the current private mode and group members.
- `CMD:BLOCK [client name(s)]`: Block specified clients.
- `CMD:UNBLOCK [client name(s)]`: Unblock specified clients.
- `CMD:CLR`: Clear the chat area.
- `CMD:PORT`: Display the server port ID.

## System Requirements

- **Java 8** or later.
- **Operating System**: Any platform that supports Java.

## How to Use This

1. Clone this repository:
   ```git clone https://github.com/ajayspatil7/ChatX.git ```
2. Compile the source code:
   ```javac Server.java Client.java Handler.java ```
3. Run the server code first:
   ```java Server.java ```
4. Run the client code in new terminal:
   ```java Client.java ```
To use multi chat : Run ```Java Client.java ``` in new terminal
