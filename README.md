Coral API
===

We are designing an API to get access to Coral's core functionality
through standard http queries.  

Design Goals: 
-------------

-   RESTful
    ([http://mvccontrib.codeplex.com/wikipage?title=SimplyRestfulRouting&referringTitle=Documentation&ProjectName=mvccontrib](http://mvccontrib.codeplex.com/wikipage?title=SimplyRestfulRouting&referringTitle=Documentation&ProjectName=mvccontrib))
-   JSON based
-   HTTP based

Requirements:
-------------

We need the API to meet the following requirements:

-   All Communication is Encrypted over SSL
-   Authentication is required (http basic auth should work:
    [http://stackoverflow.com/questions/319530/restful-authentication](http://stackoverflow.com/questions/319530/restful-authentication))
-   Must Provide Distinct URLs for each resource ([See: RESTful Routing](http://mvccontrib.codeplex.com/wikipage?title=SimplyRestfulRouting&referringTitle=Documentation&ProjectName=mvccontrib) )
-   Must support the following functionality (pseudo-code method names that would map to RESTful api calls):

    -   addEquipmentRoleToMember(String member, String roleName, String resource)
    -   removeEquipmentRoleFromMember(String member, String roleName, String resource)
    -   addProjectRoleToMember(String member, String roleName, String resource)
    -   removeProjectRoleFromMember(String member, String roleName, String resource)
    -   addSafetyFlagToMember(String member )
    -   removeSafetyFlagFromMember(String member)
    -   addMemberProjects(String member, String[] projects)
    -   removeMemberProjects(String member, String[] projects)
    -   createNewMember(Member member)
    -   createNewProject(Project project)
    -   addProjectMembers(String project, String[] members)
    -   removeProjectMembers(String project, String[] members)
    -   enable( tool, agent, member, project, account )
    -   disable(tool)
    -   qualify(tool, member, role)
    -   disqualify(tool, member, role)
    -   reserve( tool, agent, member, project, account, begin time, end time(or length) )
    -   deleteReservation( tool, member, time, length )
    -   costRecovery (month, year)          
    -   more, eventually

-   Resources(nouns) in the system:

    -   Member
    -   Tool
    -   Reservation
    -   Enable
    -   Project
    -   Account
    -   EquipmentRole
    -   perhaps more eventually, like Lab, Supply, LabRole, Rate, Rundata, ...

-   Actions(verbs) supported on the resources:
    -   Member
        -   Create (PUT)
        -   Read (or View or Show) (GET)
        -   Update (POST)
        -   Delete (DELETE) (Do we need this one?)
        -   List (GET)

    -   Tool
        -   Read (View or Show) (GET)
        -   List (GET)

    -   Reservation
        -   Create (PUT)
        -   Read (or View or Show) (GET)
        -   Update (POST)
        -   Delete (DELETE)
        -   List (GET)

    -   Enable
        -   Create (PUT)
        -   Read (or View or Show) (GET)
        -   Update (POST)
        -   Delete (DELETE)
        -   List (GET)

    -   Project
        -   Create (PUT)
        -   Read (or View or Show) (GET)
        -   Update (POST)
        -   Delete (DELETE) (Do we need this one?)
        -   List (GET)

    -   Account
        -   Create (PUT)
        -   Read (or View or Show) (GET)
        -   Update (POST)
        -   Delete (DELETE) (Do we need this one?)
        -   List (GET)

    -   EquipmentRole
        -   Create (PUT)
        -   Read (or View or Show) (GET)
        -   Update (POST)
        -   Delete (DELETE)
        -   List (GET)

  -   URLs (examples)
    -   https://server/coral-api/v0.1/member
        -   supporting PUT, GET(list)
    -   https://server/coral-api/v0.1/member?name=ryant
        -   supporting POST, GET
        -   POST and GET would send/receive json representations of member
    -   https://server/coral-api/v0.1/reservation
        -   supporting POST, GET (list of reservations)
    -   https://server/coral-api/v0.1/reservation?id=someID (base64 encoding of DB id?)
        -   supporting POST, GET, DELETE

-   The server should respond with appropriate messages for errors or
    successes (404s, 201s, etc. See
    [http://stackoverflow.com/questions/9345620](http://stackoverflow.com/questions/9345620/if-a-rest-api-method-fails-should-i-return-a-200-400-or-500-http-status-messa) ).
     Errors might be permission denied, or invalid data, or other things
    too.  Permission denied errors should give some explanation of what
    happened.
