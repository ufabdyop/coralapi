Coral API
===

This API should wrap up all the boilerplate CORBA stuff for talking to coral, so you can simply do something like:

    coral = new CoralAPI("coral", "http://mycoralhostname/IOR/", "http://mycoralhostname/coral/lib/config.jar");
    Account a = new Account();
    a.setName("My New Account");
    coral.createNewAccount(a);

    Member member = coral.getMemberByName("johndoe");
    member.getFirstName();
    member.setEmail("johndoe@yahoo.com");
    coral.updateMember(member);

    coral.close();