Coral API
===

The CoralAPI should wrap up all the boilerplate CORBA stuff for talking to coral.
    
Usage
-----

### Create a CoralAPI client

```
String coralUser = "coral";
String iorUrl = "http://mycoralhostname/IOR/";
String configUrl = "http://mycoralhostname/coral/lib/config.jar";

CoralAPI coral = new CoralAPI(coralUser, iorUrl, configUrl);
```

### Create a new coral account

```
Account a = new Account();
a.setName("New Account");
coral.createNewAccount(a);
```
### Update a Member in the system
To update a members info:

```
Member member = coral.getMemberByName("johndoe");
member.getFirstName();
member.setEmail("johndoe@example.com");
coral.updateMember(member);
```

### Getting Projects for a Member
To get all of the projects for a member:

```
Project[] projects = coral.getMemberProjects(member);
```

### Validate Account Credentials
To validate the credentials of a coral account:

```
boolean validCredentials = coral.authenticate("username", "password");
```

### Closing the CoralAPI
When you're done performing operations, don't forget to close the CoralAPI
client:

```
coral.close()
```

Installation
-----------------
This project uses maven for packaging and dependency resolution.  Since OpenCoral does not use maven, you will have to package your coral jars and put them into a maven repository.
You can do this with the following command (assuming opencoral source is in /home/coral/opencoral and you are using opencoral version 3.4.9):

    for i in $(find /home/coral/opencoral/dist -name '*jar') ; do
      FILEBASE=$(basename $i .jar);
      /usr/local/maven/bin/mvn install:install-file -Dfile=$i -DgroupId=org.opencoral -DartifactId=opencoral-$FILEBASE  -Dversion=3.4.9 -Dpackaging=jar
    done

Once those jars are in your local maven repo (~/.m2/...), you can package this project into a jar with:

    mvn package -Dmaven.test.skip=true

Tests are skipped because running the tests requires a specific coral test instance and it could potentially destroy data if run against a live coral instance.


Reference Material
------------------
We have some old code that does similar stuff [here][ref]

[ref]: http://nanoproject.eng.utah.edu/HardwareServerProxy/browser/HardwareServerProxy/HardwareServerProxy/branches/ObserverPatternRefactor/driver/src/main/java/edu/utah/nanofab/controller
