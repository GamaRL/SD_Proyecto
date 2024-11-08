Based on the detailed content from the provided PDF document, here's a draft README file tailored to the project:

---

# Distributed Support Ticket System

## 📚 Course Information
- **Course**: Distributed Systems
- **Semester**: 2025-1
- **Institution**: Universidad Nacional Autónoma de México (UNAM)
- **Instructor**: Ing. José Abraham Bonilla Pastor

## 👥 Team Members
- **Peralta Rodriguez**, Juan Manuel
- **Ríos Lira**, Gamaliel
- **Sánchez Rosas**, Alexis Alejandro
- **Santiago Alejandro**, Aldo

---

## 📖 Project Overview

This project is a distributed support ticket management system designed to ensure efficient and fair distribution of support tickets across multiple branches (nodes). The system is built to maintain availability, consistency, and fault tolerance, ensuring that tickets are managed equitably even in the presence of node failures.

The system uses distributed databases, mutual exclusion mechanisms, and consensus algorithms to coordinate between nodes. It features a master node that manages the assignment of tasks and redistributes them dynamically in the event of node failures.

## 🎯 Objectives

The main objectives of the project are:
1. Design a **distributed system** that supports multiple nodes (branches).
2. Ensure **fault tolerance** and **load balancing** among nodes.
3. Implement a **ticket management system** with distributed data storage for engineers, users, devices, and tickets.
4. Guarantee **mutual exclusion** when assigning tickets to engineers.
5. Ensure **consensus** during database updates.
6. Implement a mechanism to elect a new master node in case of master node failure.

## 🛠️ Technologies Used
- **Java** (Spring Boot): Backend development and distributed communication.
- **Maven**: Dependency management.
- **MariaDB**: Distributed database system.
- **Docker & Docker Compose**: Containerization and deployment.
- **Sockets**: Communication between distributed nodes.
- **Multithreading & Semaphores**: Handling concurrency and mutual exclusion.

---

## ⚙️ System Architecture

### Key Components
- **Client Nodes**: Handle ticket creation, assignment, and closure.
- **Master Node**: Manages device distribution, handles consensus, and coordinates ticket assignment.
- **Distributed Database**: Stores data related to users, engineers, devices, and tickets across multiple nodes.
- **Heartbeat Mechanism**: Ensures nodes are alive and functioning, enabling fault detection.
- **Token Ring Algorithm**: Ensures mutual exclusion for critical sections (e.g., ticket assignment).

### Workflow
1. A **user** creates a support ticket from any branch.
2. The system assigns an **engineer** to handle the ticket, ensuring no two engineers receive the same ticket simultaneously.
3. If a **node fails**, tasks are reassigned to other active nodes.
4. In case the **master node fails**, an election algorithm selects a new master node to maintain system continuity.

---

## 🛠️ Setup and Installation

### Prerequisites
Make sure you have the following installed:
- [Java JDK 11+](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
- [Maven](https://maven.apache.org/download.cgi)
- [Docker](https://docs.docker.com/engine/install/)
- [Docker Compose](https://docs.docker.com/compose/install/)

### Step 1: Clone the Repository
```bash
git clone https://github.com/your-repository.git
cd your-repository
```

### Step 2: Build Docker Images
In the root directory (where `backend/` is located), run:
```bash
docker compose build
```
This will build the Docker images for both the client and server nodes.

### Step 3: Start the Application
Launch the distributed system using:
```bash
docker compose up
```

The system will automatically deploy multiple nodes, including a master node and client nodes. Access the system at [http://localhost:8080](http://localhost:8080).

---

## 🚀 Usage Guide

### Features
- **Create Tickets**: Users can create support tickets from any branch.
- **Automatic Engineer Assignment**: Ensures no two engineers are assigned the same ticket simultaneously.
- **Dynamic Load Redistribution**: If a node goes down, tasks are automatically redistributed.
- **Node Monitoring**: Heartbeat messages check the status of nodes continuously.
- **Master Node Election**: In the event of a master node failure, a new master is elected automatically.

### Example Usage
1. **Creating a Ticket**:
   - A user submits a ticket for device support.
   - The system assigns the ticket to an available engineer using mutual exclusion.
2. **Node Failure Handling**:
   - If a node fails, its tasks are reassigned, and its data is updated across remaining nodes.
3. **Master Node Election**:
   - The system continuously monitors the master node and initiates a re-election process if the master fails.

---

## 🧩 Known Issues and Future Improvements
- **Node Synchronization**: Further optimization is needed to reduce latency in synchronization across nodes.
- **Security Enhancements**: Implementing secure communication channels (e.g., TLS) for node communication.
- **Scalability**: Testing the system under high loads and adding horizontal scalability.
