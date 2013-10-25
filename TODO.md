* TDD: Create web interface for routing http requests to api calls.
* TDD: getMembersForProject should only include active members
* TDD: signed in user not allowed to delete account/project user is on 
* Remove direct DB queries from tests

Interface Change:

It seems a bit odd to be returning objects of type org.opencoral.idl.Member/Project/Account.  There may be some
advantages to creating wrapper objects (either edu.nanofab.coralapi.Member/Project/Account, or ApiMember, ApiProject...).
It would perhaps make the code more loosely coupled, and allow a different backend/persistence model. Also, client software would no longer need to know about the specifics of the opencoral codebase.  And we could potentially create
smarter linking between Member and Project, for example: ApiMember.getProjects(), ApiMember.setProjects(...), etc.


