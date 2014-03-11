Coral API
===

This API should wrap up all the boilerplate CORBA stuff for talking to coral, so you can simply do something like:

    coralApi = new CoralAPI("coral", "http://mycoralhostname/IOR/", "http://mycoralhostname/coral/lib/config.jar");
    Account a = new Account();
    a.setName("My New Account");
    coralApi.createNewAccount(a);

    Member member = coral.getMemberByName("johndoe");
    member.getFirstName();
    member.setEmail("johndoe@yahoo.com");
    coralApi.updateMember(member);

