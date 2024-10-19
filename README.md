# Secure Messaging Application
## Overview
This project is a secure messaging application designed to facilitate private group chat functionality through advanced encryption techniques. It ensures data confidentiality, integrity, and efficiency while enabling real-time communication among users.

## Features![SPGC_Single_View](https://github.com/user-attachments/assets/c9259742-28c0-4db0-9ca3-772169ff1629)

- Private Group Chat: Users can create and participate in secure group chats.
- Encryption:
  - RSA: Utilized for asymmetric encryption to securely exchange keys.
  - AES: Employed for symmetric encryption, ensuring data confidentiality and efficiency.
  - SHA-256: Used for hashing, providing an additional layer of security for user credentials and messages.

## Technical Stack
- Programming Language: Java
- Networking: Socket programming for real-time communication
- Cryptography: Implementation of RSA, AES, and SHA-256 algorithms

## Architecture
The application is designed using a robust client-server architecture, allowing users to communicate securely over a network. The server manages user connections and group chat functionalities, while clients interact with the server to send and receive messages.

## Getting Started
To run this application, ensure you have Java installed on your machine. Clone the repository, and from the home directory in the project1 folder, compile and run the ChatServer.java file.
```bash
javac server/ChatServer.java
java server/ChatServer
```
Then, from the home directory in the project2 folder, compile and run the AuthenticatorView.java file.
```bash
javac client/AuthenticatorView.java
java client/AuthenticatorView
```

## Prerequisites
Java Development Kit (JDK)
Basic knowledge of networking and cryptographic concepts
