Gauge V2 test server app
=============================

[ ![Codeship Status for myrtleTree33/Gauge2](https://codeship.com/projects/416f6d60-bf4b-0132-3732-360c0bcd4f13/status?branch=master)](https://codeship.com/projects/72921)

[ ![Codeship Status for myrtleTree33/Gauge2](https://codeship.com/projects/416f6d60-bf4b-0132-3732-360c0bcd4f13/status?branch=develop)](https://codeship.com/projects/72921)

TONG Haowen Joel

Lim Anli

Huang Kaiwen


## Installation

Gauge v2 is built on Maven, however the repository packages ready builds.  To execute, run:


**Server**

    $ java -jar build/server-1.0.1-SNAPSHOT-jar-with-dependencies.jar

**Client**

    $ java -jar build/client-1.0.1-SNAPSHOT-jar-with-dependencies.jar
    
    
## Directory structure

GaugeV2 comprises the following sub-projects:

- Server
  - Central server-related info
- Client
  - Client related-info
- Core
  - Central Core-related info
  
### Core
  
**Packet**

All communication (TCP or UDP) comprises a Packet instance.

Packet instances have a destination field, header, and payload fields.  Field lengths are
flexible, with the lengths of all 3 sections transmitted first.

    [destinationField length][headerField length][payload length][destination][header][payload]
    
The instance is constructible from `byte[]` and converts to `byte[]`.


### Client

**PeerDaemon**

Creates a peer-to-peer UDP PeerDaemon server and client.

**GaugeClientDaemonTCP**

TCP connection to synchronise user and chatroom listing data with central server.

**Client**

Packages PeerDaemon and GaugeClientDaemonTCP into a nicely packaged standalone headless
chat client, to be used by the GUI frontend.

**ChatServer**

A central-server-based ChatServer ensures that user and chatroom listing data are concurrent across
all clients.


### Server

**WebServer**

The HTTP central server used for login.


### GUI

**Login**

Used for the login dialog

**MainView**

Used to generate the mainview


  
## Overview

GaugeV2 comprises a central TCP server, and supports multiple group chats via UDP.  All data is sent 
as Packet instances, defined in the core sub-project, as a JSON.  It features a HTTP user registration page.


### Features

- Lightweight
- Ability to support multiple simultaneous chatrooms at once
- Low dependence on a central server
- Ability to establish a peer-to-peer based chat


## Info

Uses maven for dependencies, junit testing, JAR executable deployment and creation.


## Steps to getting into development

1. Fork the project on Github.
2. Clone the fork

```
    $ git clone <path-to-git-fork-repository>
```

3. Link it back to main repository `upstream`

```
    $ git remote add upstream git@github.com:myrtleTree33/Gauge2.git
```
    
    
4. To retrieve updates from main repository daily, use the following commands


```
    $ git fetch upstream              ## fetch changes from upstream
    $ git checkout master             ## checkout master branch
    $ git rebase upstream master      ## Syncs changes
```
   
    
5. Contribute to development on separate branch @ `origin/<branch-name>` ; merge with `origin/develop` when done.
6. To contribute code, first push to forked repository.  Then submit a pull request on Github.
7. Download Maven.


## Build targets

Features the following sub-projects, compiled in the following order:

- gauge (root)
- core
- server
- client

To execute goals in a certain project, in this case the `test` goal in `client`, run

    $ mvn -pl :client test


## Running unit tests

To execute all tests, run

    $ mvn test
    
    
## Creating JAR executable

To create a JAR with dependencies included, run:

    $ mvn install

The created `jars` will be in the respective sub-project `target/` directories.


## Backlog

- Implemented HTTP server for GET and POST requests
- Implemented post and get request wrappers
- Implemented framework to support different layers of abstraction among packets
    

