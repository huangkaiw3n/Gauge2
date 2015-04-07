Gauge V2 test server app
=============================

<img src="https://codeship.com/projects/YOUR_PROJECT_UUID/status?branch=master" />


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
    

