package edu.nanofab.coralapi.collections;


import edu.nanofab.coralapi.resource.Account;

public class Accounts extends AccountsProxySet {

	public static Accounts fromIdlAccountArray(org.opencoral.idl.Account[] allAccounts) {
		Accounts accountCollection = new Accounts();
		for(org.opencoral.idl.Account idlAccount : allAccounts) {
			Account acct = new Account();
			acct.populateFromIdlAccount(idlAccount);
			accountCollection.add(acct);
		}
    	return accountCollection;
	}

}
