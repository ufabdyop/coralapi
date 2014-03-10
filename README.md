Coral API
===

This API should wrap up all the boilerplate CORBA stuff for talking to coral, so you can simply do something like:

    CoralAPI coral = new CoralAPI(...);
    Member member = coral.getMemberByName("johndoe");
    member.getFirstName();
    ...

