package edu.utah.nanofab.coralapi.resource;

public final class Machine 
{
	  private String name = null;
	  private String id = null;
	  private String area = null;
	  private String lab = null;
	  private String type = null;
	  private String agent = null;
	  private int granularity = 0;
	  private boolean interlocked = false;
	  private boolean enabled = false;
	  private boolean collectRundataAtEnable = false;
	  private String description = null;
	  private String location = null;
	  private String manualURL = null;
	  private String historyURL = null;
	  private String hostName = null;
	  private int port = 0;
	  private int cardNumber = 0;
	  private int evenPort = 0;
	  private int oddPort = 0;
	  private int channel = 0;
	  private boolean hidden = false;
	  private String qualify = null;
	  private String safety = null;
	  private int maint = 0;
	  private int warnings = 0;
	  private int problems = 0;
	  private int shutdowns = 0;
	  private int reqdSupport = 0;
	  private int optSupport = 0;
	  private int numInterlocks = 0;
	  
	public void populateFromIdlMachine(org.opencoral.idl.Machine idlMachine) {
		this.name  = idlMachine.name ;
		this.id  = idlMachine.id ;
		this.area  = idlMachine.area ;
		this.lab  = idlMachine.lab ;
		this.type  = idlMachine.type ;
		this.agent  = idlMachine.agent ;
		this.granularity  = idlMachine.granularity ;
		this.interlocked  = idlMachine.interlocked ;
		this.enabled  = idlMachine.enabled ;
		this.collectRundataAtEnable  = idlMachine.collectRundataAtEnable ;
		this.description  = idlMachine.description ;
		this.location  = idlMachine.location ;
		this.manualURL  = idlMachine.manualURL ;
		this.historyURL  = idlMachine.historyURL ;
		this.hostName  = idlMachine.hostName ;
		this.port  = idlMachine.port ;
		this.cardNumber  = idlMachine.cardNumber ;
		this.evenPort  = idlMachine.evenPort ;
		this.oddPort  = idlMachine.oddPort ;
		this.channel  = idlMachine.channel ;
		this.hidden  = idlMachine.hidden ;
		this.qualify  = idlMachine.qualify ;
		this.safety  = idlMachine.safety ;
		this.maint  = idlMachine.maint ;
		this.warnings  = idlMachine.warnings ;
		this.problems  = idlMachine.problems ;
		this.shutdowns  = idlMachine.shutdowns ;
		this.reqdSupport  = idlMachine.reqdSupport ;
		this.optSupport  = idlMachine.optSupport ;
		this.numInterlocks  = idlMachine.numInterlocks ;		
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getLab() {
		return lab;
	}
	public void setLab(String lab) {
		this.lab = lab;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getAgent() {
		return agent;
	}
	public void setAgent(String agent) {
		this.agent = agent;
	}
	public int getGranularity() {
		return granularity;
	}
	public void setGranularity(int granularity) {
		this.granularity = granularity;
	}
	public boolean isInterlocked() {
		return interlocked;
	}
	public void setInterlocked(boolean interlocked) {
		this.interlocked = interlocked;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public boolean isCollectRundataAtEnable() {
		return collectRundataAtEnable;
	}
	public void setCollectRundataAtEnable(boolean collectRundataAtEnable) {
		this.collectRundataAtEnable = collectRundataAtEnable;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getManualURL() {
		return manualURL;
	}
	public void setManualURL(String manualURL) {
		this.manualURL = manualURL;
	}
	public String getHistoryURL() {
		return historyURL;
	}
	public void setHistoryURL(String historyURL) {
		this.historyURL = historyURL;
	}
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public int getCardNumber() {
		return cardNumber;
	}
	public void setCardNumber(int cardNumber) {
		this.cardNumber = cardNumber;
	}
	public int getEvenPort() {
		return evenPort;
	}
	public void setEvenPort(int evenPort) {
		this.evenPort = evenPort;
	}
	public int getOddPort() {
		return oddPort;
	}
	public void setOddPort(int oddPort) {
		this.oddPort = oddPort;
	}
	public int getChannel() {
		return channel;
	}
	public void setChannel(int channel) {
		this.channel = channel;
	}
	public boolean isHidden() {
		return hidden;
	}
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
	public String getQualify() {
		return qualify;
	}
	public void setQualify(String qualify) {
		this.qualify = qualify;
	}
	public String getSafety() {
		return safety;
	}
	public void setSafety(String safety) {
		this.safety = safety;
	}
	public int getMaint() {
		return maint;
	}
	public void setMaint(int maint) {
		this.maint = maint;
	}
	public int getWarnings() {
		return warnings;
	}
	public void setWarnings(int warnings) {
		this.warnings = warnings;
	}
	public int getProblems() {
		return problems;
	}
	public void setProblems(int problems) {
		this.problems = problems;
	}
	public int getShutdowns() {
		return shutdowns;
	}
	public void setShutdowns(int shutdowns) {
		this.shutdowns = shutdowns;
	}
	public int getReqdSupport() {
		return reqdSupport;
	}
	public void setReqdSupport(int reqdSupport) {
		this.reqdSupport = reqdSupport;
	}
	public int getOptSupport() {
		return optSupport;
	}
	public void setOptSupport(int optSupport) {
		this.optSupport = optSupport;
	}
	public int getNumInterlocks() {
		return numInterlocks;
	}
	public void setNumInterlocks(int numInterlocks) {
		this.numInterlocks = numInterlocks;
	}
}