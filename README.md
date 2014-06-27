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

For more examples, please refer to the online documentation.

### Reference Material
We have some old code that does similar stuff here:

Reference Code:
---
http://nanoproject.eng.utah.edu/HardwareServerProxy/browser/HardwareServerProxy/HardwareServerProxy/branches/ObserverPatternRefactor/driver/src/main/java/edu/utah/nanofab/controller
