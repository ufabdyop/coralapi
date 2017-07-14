package edu.utah.nanofab.coralapi;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import org.opencoral.constants.Constants;
import org.opencoral.idl.AccountNotFoundSignal;
import org.opencoral.idl.Activity;
import org.opencoral.idl.InvalidAccountSignal;
import org.opencoral.idl.InvalidAgentSignal;
import org.opencoral.idl.InvalidMemberSignal;
import org.opencoral.idl.InvalidNicknameSignal;
import org.opencoral.idl.InvalidProcessSignal;
import org.opencoral.idl.InvalidProjectSignal;
import org.opencoral.idl.InvalidResourceSignal;
import org.opencoral.idl.InvalidRoleSignal;
import org.opencoral.idl.InvalidTicketSignal;
import org.opencoral.idl.MemberDuplicateSignal;
import org.opencoral.idl.MemberNotFoundSignal;
import org.opencoral.idl.NotAuthorizedSignal;
import org.opencoral.idl.Persona;
import org.opencoral.idl.ProjectNotFoundSignal;
import org.opencoral.idl.Relation;
import org.opencoral.idl.ResourceUnavailableSignal;
import org.opencoral.idl.RoleNotFoundSignal;
import org.opencoral.idl.Timestamp;
import org.opencoral.idl.Auth.AuthManager;
import org.opencoral.idl.Auth.AuthManagerHelper;
import org.opencoral.idl.Equipment.EquipmentManager;
import org.opencoral.idl.Equipment.EquipmentManagerHelper;
import org.opencoral.idl.Equipment.EquipmentManagerPackage.MachineRetrievalFailedSignal;
import org.opencoral.idl.Reservation.ReservationManager;
import org.opencoral.idl.Reservation.ReservationManagerHelper;
import org.opencoral.idl.Reservation.ReservationManagerPackage.ReservationNotFoundSignal;
import org.opencoral.idl.Resource.ResourceManager;
import org.opencoral.idl.Resource.ResourceManagerHelper;
import org.opencoral.util.ResourceRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import edu.utah.nanofab.coralapi.resource.Account;
import edu.utah.nanofab.coralapi.exceptions.ConfigLoaderException;
import edu.utah.nanofab.coralapi.exceptions.InvalidAccountException;
import edu.utah.nanofab.coralapi.exceptions.InvalidAgentException;
import edu.utah.nanofab.coralapi.exceptions.InvalidCallOrderException;
import edu.utah.nanofab.coralapi.exceptions.InvalidDateException;
import edu.utah.nanofab.coralapi.exceptions.InvalidMemberException;
import edu.utah.nanofab.coralapi.exceptions.InvalidProcessException;
import edu.utah.nanofab.coralapi.exceptions.InvalidProjectException;
import edu.utah.nanofab.coralapi.exceptions.InvalidResourceException;
import edu.utah.nanofab.coralapi.exceptions.InvalidRoleException;
import edu.utah.nanofab.coralapi.exceptions.UnknownMemberException;
import edu.utah.nanofab.coralapi.helper.ActivityString;
import edu.utah.nanofab.coralapi.helper.TimestampConverter;
import edu.utah.nanofab.coralapi.helper.Utils;
import edu.utah.nanofab.coralapi.collections.Accounts;
import edu.utah.nanofab.coralapi.collections.EquipmentRoles;
import edu.utah.nanofab.coralapi.collections.LabRoles;
import edu.utah.nanofab.coralapi.collections.Machines;
import edu.utah.nanofab.coralapi.collections.Members;
import edu.utah.nanofab.coralapi.collections.Projects;
import edu.utah.nanofab.coralapi.collections.Reservations;
import edu.utah.nanofab.coralapi.exceptions.CoralConnectionException;
import edu.utah.nanofab.coralapi.exceptions.RequestFailedException;
import edu.utah.nanofab.coralapi.helper.CoralConnectorInterface;
import edu.utah.nanofab.coralapi.resource.Enable;
import edu.utah.nanofab.coralapi.resource.LabRole;
import edu.utah.nanofab.coralapi.resource.Machine;
import edu.utah.nanofab.coralapi.resource.Member;
import edu.utah.nanofab.coralapi.resource.Project;
import edu.utah.nanofab.coralapi.resource.ProjectRole;
import edu.utah.nanofab.coralapi.resource.Reservation;
import edu.utah.nanofab.coralapi.helper.CoralManagerConnector;
import edu.utah.nanofab.coralapi.helper.CoralManagers;
import static edu.utah.nanofab.coralapi.helper.TimestampConverter.stringToDate;
import edu.utah.nanofab.coralapi.resource.RunDataProcess;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import org.opencoral.corba.RundataAdapter;
import org.opencoral.idl.InvalidDateSignal;
import org.opencoral.idl.Reservation.ReservationManagerPackage.ReservationDuplicateSignal;
import org.opencoral.idl.Runtime.NullReturnException;
import org.opencoral.idl.Runtime.RuntimeManager;
import org.opencoral.idl.Runtime.ServerErrorException;
import org.opencoral.runtime.xml.RmRunData;
import org.opencoral.util.RmUtil;
import org.opencoral.util.XMLType;
import edu.utah.nanofab.coralapi.resource.Role;

/**
 * Decorator of the CoralAPI class.  Each method call retries upon failure
 * 
 * Automatically generated using:
 * mvn exec:java -Dexec.mainClass=edu.utah.nanofab.coralapi.codegeneration.CreateRetryDecorator \
 *     -Dexec.args="src/main/java/edu/utah/nanofab/coralapi/CoralAPI.java" \
 *    | grep -v '^\[INFO\]' > src/main/java/edu/utah/nanofab/coralapi/CoralAPIRetryDecorator.java
 * 
 * @author ryant
 */
public class CoralAPIRetryDecorator implements CoralAPIInterface {

	private final CoralAPIInterface delegate;
	private final Integer numberOfRetries;

	public CoralAPIRetryDecorator(CoralAPIInterface delegate) {
		this.delegate = delegate;
		this.numberOfRetries = 2;
	}

	public void setup(java.lang.String coralUser, java.lang.String configUrl)
			throws CoralConnectionException {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				delegate.setup(coralUser, configUrl);
				success = true;
			} catch (CoralConnectionException ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
	}

	public Projects getProjects() throws ProjectNotFoundSignal {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		edu.utah.nanofab.coralapi.collections.Projects returnValue = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				returnValue = delegate.getProjects();
				success = true;
			} catch (ProjectNotFoundSignal ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
		return returnValue;
	}

	public Projects getAllProjects() throws ProjectNotFoundSignal {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		edu.utah.nanofab.coralapi.collections.Projects returnValue = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				returnValue = delegate.getAllProjects();
				success = true;
			} catch (ProjectNotFoundSignal ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
		return returnValue;
	}

	public Project getProject(java.lang.String name)
			throws ProjectNotFoundSignal {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		edu.utah.nanofab.coralapi.resource.Project returnValue = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				returnValue = delegate.getProject(name);
				success = true;
			} catch (ProjectNotFoundSignal ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
		return returnValue;
	}

	public void createNewMember(edu.utah.nanofab.coralapi.resource.Member member)
			throws MemberDuplicateSignal, InvalidProjectSignal, Exception {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				delegate.createNewMember(member);
				success = true;
			} catch (MemberDuplicateSignal ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidProjectSignal ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
					throw ex2;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex3) {
				caughtException = ex3;
				if (count >= numberOfRetries) {
					throw ex3;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
	}

	public void createNewProject(
			edu.utah.nanofab.coralapi.resource.Project project)
			throws Exception {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				delegate.createNewProject(project);
				success = true;
			} catch (Exception ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
	}

	public void createNewProjectUnlessExists(
			edu.utah.nanofab.coralapi.resource.Project project)
			throws Exception {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				delegate.createNewProjectUnlessExists(project);
				success = true;
			} catch (Exception ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
	}

	public void deleteMemberFromProject(java.lang.String memberName,
			java.lang.String projectName) throws InvalidTicketSignal,
			MemberDuplicateSignal, InvalidProjectSignal, NotAuthorizedSignal,
			InvalidMemberSignal {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				delegate.deleteMemberFromProject(memberName, projectName);
				success = true;
			} catch (InvalidTicketSignal ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (MemberDuplicateSignal ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
					throw ex2;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidProjectSignal ex3) {
				caughtException = ex3;
				if (count >= numberOfRetries) {
					throw ex3;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (NotAuthorizedSignal ex4) {
				caughtException = ex4;
				if (count >= numberOfRetries) {
					throw ex4;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidMemberSignal ex5) {
				caughtException = ex5;
				if (count >= numberOfRetries) {
					throw ex5;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex6) {
				caughtException = ex6;
				if (count >= numberOfRetries) {
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
	}

	public void addProjectMembers(java.lang.String project,
			java.lang.String[] members) throws InvalidTicketSignal,
			InvalidMemberSignal, InvalidProjectSignal, NotAuthorizedSignal {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				delegate.addProjectMembers(project, members);
				success = true;
			} catch (InvalidTicketSignal ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidMemberSignal ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
					throw ex2;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidProjectSignal ex3) {
				caughtException = ex3;
				if (count >= numberOfRetries) {
					throw ex3;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (NotAuthorizedSignal ex4) {
				caughtException = ex4;
				if (count >= numberOfRetries) {
					throw ex4;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex5) {
				caughtException = ex5;
				if (count >= numberOfRetries) {
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
	}

	public void removeProjectMembers(java.lang.String project,
			java.lang.String[] members) throws InvalidTicketSignal,
			InvalidMemberSignal, InvalidProjectSignal, NotAuthorizedSignal {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				delegate.removeProjectMembers(project, members);
				success = true;
			} catch (InvalidTicketSignal ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidMemberSignal ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
					throw ex2;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidProjectSignal ex3) {
				caughtException = ex3;
				if (count >= numberOfRetries) {
					throw ex3;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (NotAuthorizedSignal ex4) {
				caughtException = ex4;
				if (count >= numberOfRetries) {
					throw ex4;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex5) {
				caughtException = ex5;
				if (count >= numberOfRetries) {
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
	}

	public Member getMember(java.lang.String member)
			throws UnknownMemberException, Exception {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		edu.utah.nanofab.coralapi.resource.Member returnValue = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				returnValue = delegate.getMember(member);
				success = true;
			} catch (UnknownMemberException ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
					throw ex2;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
		return returnValue;
	}

	public Members getAllMembers() throws UnknownMemberException, Exception {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		edu.utah.nanofab.coralapi.collections.Members returnValue = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				returnValue = delegate.getAllMembers();
				success = true;
			} catch (UnknownMemberException ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
					throw ex2;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
		return returnValue;
	}

	public Members getAllActiveMembers() throws UnknownMemberException,
			Exception {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		edu.utah.nanofab.coralapi.collections.Members returnValue = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				returnValue = delegate.getAllActiveMembers();
				success = true;
			} catch (UnknownMemberException ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
					throw ex2;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
		return returnValue;
	}

	public Member getQualifications(java.lang.String member)
			throws UnknownMemberException, Exception {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		edu.utah.nanofab.coralapi.resource.Member returnValue = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				returnValue = delegate.getQualifications(member);
				success = true;
			} catch (UnknownMemberException ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
					throw ex2;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
		return returnValue;
	}

	public Projects getMemberProjects(java.lang.String member) {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		edu.utah.nanofab.coralapi.collections.Projects returnValue = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				returnValue = delegate.getMemberProjects(member);
				success = true;
			} catch (Exception ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
		return returnValue;
	}

	public HashMap<String, ArrayList<String>> getAllMemberProjects(
			boolean activeOnly) {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		java.util.HashMap<String, ArrayList<String>> returnValue = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				returnValue = delegate.getAllMemberProjects(activeOnly);
				success = true;
			} catch (Exception ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
		return returnValue;
	}

	public void addMemberProjects(java.lang.String member,
			java.lang.String[] projects) throws InvalidTicketSignal,
			InvalidMemberSignal, InvalidProjectSignal, NotAuthorizedSignal {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				delegate.addMemberProjects(member, projects);
				success = true;
			} catch (InvalidTicketSignal ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidMemberSignal ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
					throw ex2;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidProjectSignal ex3) {
				caughtException = ex3;
				if (count >= numberOfRetries) {
					throw ex3;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (NotAuthorizedSignal ex4) {
				caughtException = ex4;
				if (count >= numberOfRetries) {
					throw ex4;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex5) {
				caughtException = ex5;
				if (count >= numberOfRetries) {
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
	}

	public void addMemberProjectCollection(java.lang.String member,
			edu.utah.nanofab.coralapi.collections.Projects projects) {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				delegate.addMemberProjectCollection(member, projects);
				success = true;
			} catch (Exception ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
	}

	public void removeMemberProjects(java.lang.String member,
			java.lang.String[] projects) throws InvalidTicketSignal,
			InvalidMemberSignal, InvalidProjectSignal, NotAuthorizedSignal {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				delegate.removeMemberProjects(member, projects);
				success = true;
			} catch (InvalidTicketSignal ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidMemberSignal ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
					throw ex2;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidProjectSignal ex3) {
				caughtException = ex3;
				if (count >= numberOfRetries) {
					throw ex3;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (NotAuthorizedSignal ex4) {
				caughtException = ex4;
				if (count >= numberOfRetries) {
					throw ex4;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex5) {
				caughtException = ex5;
				if (count >= numberOfRetries) {
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
	}

	public void addEquipmentRoleToMember(java.lang.String member,
			java.lang.String roleName, java.lang.String resource)
			throws IOException, InvalidTicketSignal, InvalidRoleSignal,
			InvalidMemberSignal, NotAuthorizedSignal, InvalidResourceSignal {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				delegate.addEquipmentRoleToMember(member, roleName, resource);
				success = true;
			} catch (IOException ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidTicketSignal ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
					throw ex2;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidRoleSignal ex3) {
				caughtException = ex3;
				if (count >= numberOfRetries) {
					throw ex3;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidMemberSignal ex4) {
				caughtException = ex4;
				if (count >= numberOfRetries) {
					throw ex4;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (NotAuthorizedSignal ex5) {
				caughtException = ex5;
				if (count >= numberOfRetries) {
					throw ex5;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidResourceSignal ex6) {
				caughtException = ex6;
				if (count >= numberOfRetries) {
					throw ex6;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex7) {
				caughtException = ex7;
				if (count >= numberOfRetries) {
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
	}

	public void removeEquipmentRoleFromMember(java.lang.String member,
			java.lang.String roleName, java.lang.String resource)
			throws IOException, InvalidTicketSignal, InvalidRoleSignal,
			InvalidMemberSignal, NotAuthorizedSignal, InvalidResourceSignal {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				delegate.removeEquipmentRoleFromMember(member, roleName,
						resource);
				success = true;
			} catch (IOException ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidTicketSignal ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
					throw ex2;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidRoleSignal ex3) {
				caughtException = ex3;
				if (count >= numberOfRetries) {
					throw ex3;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidMemberSignal ex4) {
				caughtException = ex4;
				if (count >= numberOfRetries) {
					throw ex4;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (NotAuthorizedSignal ex5) {
				caughtException = ex5;
				if (count >= numberOfRetries) {
					throw ex5;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidResourceSignal ex6) {
				caughtException = ex6;
				if (count >= numberOfRetries) {
					throw ex6;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex7) {
				caughtException = ex7;
				if (count >= numberOfRetries) {
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
	}

	public void removeLabRoleFromMember(java.lang.String member,
			java.lang.String roleName, java.lang.String lab)
			throws IOException, InvalidTicketSignal, InvalidRoleSignal,
			InvalidMemberSignal, NotAuthorizedSignal, InvalidResourceSignal {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				delegate.removeLabRoleFromMember(member, roleName, lab);
				success = true;
			} catch (IOException ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidTicketSignal ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
					throw ex2;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidRoleSignal ex3) {
				caughtException = ex3;
				if (count >= numberOfRetries) {
					throw ex3;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidMemberSignal ex4) {
				caughtException = ex4;
				if (count >= numberOfRetries) {
					throw ex4;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (NotAuthorizedSignal ex5) {
				caughtException = ex5;
				if (count >= numberOfRetries) {
					throw ex5;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidResourceSignal ex6) {
				caughtException = ex6;
				if (count >= numberOfRetries) {
					throw ex6;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex7) {
				caughtException = ex7;
				if (count >= numberOfRetries) {
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
	}

	public void removeGenericRoleFromMember(java.lang.String member,
			java.lang.String roleName, java.lang.String resource,
			java.lang.String roleType) throws IOException, InvalidTicketSignal,
			InvalidRoleSignal, InvalidMemberSignal, NotAuthorizedSignal,
			InvalidResourceSignal {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				delegate.removeGenericRoleFromMember(member, roleName,
						resource, roleType);
				success = true;
			} catch (IOException ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidTicketSignal ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
					throw ex2;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidRoleSignal ex3) {
				caughtException = ex3;
				if (count >= numberOfRetries) {
					throw ex3;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidMemberSignal ex4) {
				caughtException = ex4;
				if (count >= numberOfRetries) {
					throw ex4;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (NotAuthorizedSignal ex5) {
				caughtException = ex5;
				if (count >= numberOfRetries) {
					throw ex5;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidResourceSignal ex6) {
				caughtException = ex6;
				if (count >= numberOfRetries) {
					throw ex6;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex7) {
				caughtException = ex7;
				if (count >= numberOfRetries) {
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
	}

	public void addProjectRoleToMember(java.lang.String member,
			java.lang.String roleName, java.lang.String resource)
			throws IOException, InvalidTicketSignal, InvalidRoleSignal,
			InvalidMemberSignal, NotAuthorizedSignal, InvalidResourceSignal {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				delegate.addProjectRoleToMember(member, roleName, resource);
				success = true;
			} catch (IOException ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidTicketSignal ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
					throw ex2;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidRoleSignal ex3) {
				caughtException = ex3;
				if (count >= numberOfRetries) {
					throw ex3;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidMemberSignal ex4) {
				caughtException = ex4;
				if (count >= numberOfRetries) {
					throw ex4;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (NotAuthorizedSignal ex5) {
				caughtException = ex5;
				if (count >= numberOfRetries) {
					throw ex5;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidResourceSignal ex6) {
				caughtException = ex6;
				if (count >= numberOfRetries) {
					throw ex6;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex7) {
				caughtException = ex7;
				if (count >= numberOfRetries) {
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
	}

	public void removeProjectRoleFromMember(java.lang.String member,
			java.lang.String roleName, java.lang.String resource)
			throws IOException, InvalidTicketSignal, InvalidRoleSignal,
			InvalidMemberSignal, NotAuthorizedSignal, InvalidResourceSignal {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				delegate.removeProjectRoleFromMember(member, roleName, resource);
				success = true;
			} catch (IOException ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidTicketSignal ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
					throw ex2;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidRoleSignal ex3) {
				caughtException = ex3;
				if (count >= numberOfRetries) {
					throw ex3;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidMemberSignal ex4) {
				caughtException = ex4;
				if (count >= numberOfRetries) {
					throw ex4;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (NotAuthorizedSignal ex5) {
				caughtException = ex5;
				if (count >= numberOfRetries) {
					throw ex5;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidResourceSignal ex6) {
				caughtException = ex6;
				if (count >= numberOfRetries) {
					throw ex6;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex7) {
				caughtException = ex7;
				if (count >= numberOfRetries) {
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
	}

	public void addSafetyFlagToMember(java.lang.String member)
			throws IOException, InvalidTicketSignal, InvalidRoleSignal,
			InvalidMemberSignal, NotAuthorizedSignal, InvalidResourceSignal {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				delegate.addSafetyFlagToMember(member);
				success = true;
			} catch (IOException ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidTicketSignal ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
					throw ex2;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidRoleSignal ex3) {
				caughtException = ex3;
				if (count >= numberOfRetries) {
					throw ex3;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidMemberSignal ex4) {
				caughtException = ex4;
				if (count >= numberOfRetries) {
					throw ex4;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (NotAuthorizedSignal ex5) {
				caughtException = ex5;
				if (count >= numberOfRetries) {
					throw ex5;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidResourceSignal ex6) {
				caughtException = ex6;
				if (count >= numberOfRetries) {
					throw ex6;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex7) {
				caughtException = ex7;
				if (count >= numberOfRetries) {
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
	}

	public void removeSafetyFlagFromMember(java.lang.String member)
			throws IOException, InvalidTicketSignal, InvalidRoleSignal,
			InvalidMemberSignal, NotAuthorizedSignal, InvalidResourceSignal {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				delegate.removeSafetyFlagFromMember(member);
				success = true;
			} catch (IOException ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidTicketSignal ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
					throw ex2;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidRoleSignal ex3) {
				caughtException = ex3;
				if (count >= numberOfRetries) {
					throw ex3;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidMemberSignal ex4) {
				caughtException = ex4;
				if (count >= numberOfRetries) {
					throw ex4;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (NotAuthorizedSignal ex5) {
				caughtException = ex5;
				if (count >= numberOfRetries) {
					throw ex5;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidResourceSignal ex6) {
				caughtException = ex6;
				if (count >= numberOfRetries) {
					throw ex6;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex7) {
				caughtException = ex7;
				if (count >= numberOfRetries) {
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
	}

	public Members getProjectMembers(java.lang.String projectName) {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		edu.utah.nanofab.coralapi.collections.Members returnValue = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				returnValue = delegate.getProjectMembers(projectName);
				success = true;
			} catch (Exception ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
		return returnValue;
	}

	public void createNewAccount(edu.utah.nanofab.coralapi.resource.Account acct)
			throws Exception {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				delegate.createNewAccount(acct);
				success = true;
			} catch (Exception ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
	}

	public void createNewAccountUnlessExists(
			edu.utah.nanofab.coralapi.resource.Account acct) throws Exception {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				delegate.createNewAccountUnlessExists(acct);
				success = true;
			} catch (Exception ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
	}

	public Machine getMachine(java.lang.String name)
			throws MachineRetrievalFailedSignal {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		edu.utah.nanofab.coralapi.resource.Machine returnValue = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				returnValue = delegate.getMachine(name);
				success = true;
			} catch (MachineRetrievalFailedSignal ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
		return returnValue;
	}

	public Machines getAllMachines() {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		edu.utah.nanofab.coralapi.collections.Machines returnValue = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				returnValue = delegate.getAllMachines();
				success = true;
			} catch (Exception ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
		return returnValue;
	}

	public Account getAccount(java.lang.String name)
			throws InvalidAccountSignal {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		edu.utah.nanofab.coralapi.resource.Account returnValue = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				returnValue = delegate.getAccount(name);
				success = true;
			} catch (InvalidAccountSignal ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
		return returnValue;
	}

	public Accounts getAccounts() throws AccountNotFoundSignal {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		edu.utah.nanofab.coralapi.collections.Accounts returnValue = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				returnValue = delegate.getAccounts();
				success = true;
			} catch (AccountNotFoundSignal ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
		return returnValue;
	}

	public Accounts getAllAccounts() throws AccountNotFoundSignal {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		edu.utah.nanofab.coralapi.collections.Accounts returnValue = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				returnValue = delegate.getAllAccounts();
				success = true;
			} catch (AccountNotFoundSignal ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
		return returnValue;
	}

	public void deleteProject(java.lang.String projectName)
			throws InvalidTicketSignal, NotAuthorizedSignal, Exception {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				delegate.deleteProject(projectName);
				success = true;
			} catch (InvalidTicketSignal ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (NotAuthorizedSignal ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
					throw ex2;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex3) {
				caughtException = ex3;
				if (count >= numberOfRetries) {
					throw ex3;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
	}

	public boolean authenticate(java.lang.String username,
			java.lang.String password) {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		boolean returnValue = false;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				returnValue = delegate.authenticate(username, password);
				success = true;
			} catch (Exception ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
		return returnValue;
	}

	public void activateMember(java.lang.String memberName)
			throws UnknownMemberException, Exception {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				delegate.activateMember(memberName);
				success = true;
			} catch (UnknownMemberException ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
					throw ex2;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
	}

	public void activateMemberWithProject(java.lang.String memberName,
			java.lang.String projectName) throws InvalidTicketSignal,
			ProjectNotFoundSignal, MemberNotFoundSignal, InvalidProjectSignal,
			NotAuthorizedSignal {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				delegate.activateMemberWithProject(memberName, projectName);
				success = true;
			} catch (InvalidTicketSignal ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (ProjectNotFoundSignal ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
					throw ex2;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (MemberNotFoundSignal ex3) {
				caughtException = ex3;
				if (count >= numberOfRetries) {
					throw ex3;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidProjectSignal ex4) {
				caughtException = ex4;
				if (count >= numberOfRetries) {
					throw ex4;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (NotAuthorizedSignal ex5) {
				caughtException = ex5;
				if (count >= numberOfRetries) {
					throw ex5;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex6) {
				caughtException = ex6;
				if (count >= numberOfRetries) {
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
	}

	public void deactivateMember(java.lang.String memberName)
			throws InvalidTicketSignal, MemberNotFoundSignal,
			NotAuthorizedSignal {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				delegate.deactivateMember(memberName);
				success = true;
			} catch (InvalidTicketSignal ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (MemberNotFoundSignal ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
					throw ex2;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (NotAuthorizedSignal ex3) {
				caughtException = ex3;
				if (count >= numberOfRetries) {
					throw ex3;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex4) {
				caughtException = ex4;
				if (count >= numberOfRetries) {
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
	}

	public void activateProject(java.lang.String projectName)
			throws InvalidTicketSignal, ProjectNotFoundSignal,
			InvalidNicknameSignal, InvalidAccountSignal, NotAuthorizedSignal,
			Exception {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				delegate.activateProject(projectName);
				success = true;
			} catch (InvalidTicketSignal ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (ProjectNotFoundSignal ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
					throw ex2;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidNicknameSignal ex3) {
				caughtException = ex3;
				if (count >= numberOfRetries) {
					throw ex3;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidAccountSignal ex4) {
				caughtException = ex4;
				if (count >= numberOfRetries) {
					throw ex4;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (NotAuthorizedSignal ex5) {
				caughtException = ex5;
				if (count >= numberOfRetries) {
					throw ex5;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex6) {
				caughtException = ex6;
				if (count >= numberOfRetries) {
					throw ex6;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
	}

	public void deactivateProject(java.lang.String projectName)
			throws InvalidTicketSignal, ProjectNotFoundSignal,
			InvalidNicknameSignal, InvalidAccountSignal, NotAuthorizedSignal,
			Exception {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				delegate.deactivateProject(projectName);
				success = true;
			} catch (InvalidTicketSignal ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (ProjectNotFoundSignal ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
					throw ex2;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidNicknameSignal ex3) {
				caughtException = ex3;
				if (count >= numberOfRetries) {
					throw ex3;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidAccountSignal ex4) {
				caughtException = ex4;
				if (count >= numberOfRetries) {
					throw ex4;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (NotAuthorizedSignal ex5) {
				caughtException = ex5;
				if (count >= numberOfRetries) {
					throw ex5;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex6) {
				caughtException = ex6;
				if (count >= numberOfRetries) {
					throw ex6;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
	}

	public void updateProject(edu.utah.nanofab.coralapi.resource.Project project)
			throws InvalidTicketSignal, ProjectNotFoundSignal,
			InvalidNicknameSignal, InvalidAccountSignal, NotAuthorizedSignal,
			Exception {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				delegate.updateProject(project);
				success = true;
			} catch (InvalidTicketSignal ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (ProjectNotFoundSignal ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
					throw ex2;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidNicknameSignal ex3) {
				caughtException = ex3;
				if (count >= numberOfRetries) {
					throw ex3;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidAccountSignal ex4) {
				caughtException = ex4;
				if (count >= numberOfRetries) {
					throw ex4;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (NotAuthorizedSignal ex5) {
				caughtException = ex5;
				if (count >= numberOfRetries) {
					throw ex5;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex6) {
				caughtException = ex6;
				if (count >= numberOfRetries) {
					throw ex6;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
	}

	public void updateAccount(edu.utah.nanofab.coralapi.resource.Account account)
			throws InvalidTicketSignal, AccountNotFoundSignal,
			NotAuthorizedSignal, Exception {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				delegate.updateAccount(account);
				success = true;
			} catch (InvalidTicketSignal ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (AccountNotFoundSignal ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
					throw ex2;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (NotAuthorizedSignal ex3) {
				caughtException = ex3;
				if (count >= numberOfRetries) {
					throw ex3;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex4) {
				caughtException = ex4;
				if (count >= numberOfRetries) {
					throw ex4;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
	}

	public void updateMember(edu.utah.nanofab.coralapi.resource.Member member)
			throws InvalidTicketSignal, MemberNotFoundSignal,
			InvalidProjectSignal, NotAuthorizedSignal, Exception {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				delegate.updateMember(member);
				success = true;
			} catch (InvalidTicketSignal ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (MemberNotFoundSignal ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
					throw ex2;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidProjectSignal ex3) {
				caughtException = ex3;
				if (count >= numberOfRetries) {
					throw ex3;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (NotAuthorizedSignal ex4) {
				caughtException = ex4;
				if (count >= numberOfRetries) {
					throw ex4;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex5) {
				caughtException = ex5;
				if (count >= numberOfRetries) {
					throw ex5;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
	}

	public void close() {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				delegate.close();
				success = true;
			} catch (Exception ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
	}

	public void reInitialize() throws CoralConnectionException {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				delegate.reInitialize();
				success = true;
			} catch (CoralConnectionException ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
	}

	public void addLabRoleToMember(
			edu.utah.nanofab.coralapi.resource.LabRole newRole)
			throws Exception {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				delegate.addLabRoleToMember(newRole);
				success = true;
			} catch (Exception ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
	}

	public void addProjectRoleToMember(
			edu.utah.nanofab.coralapi.resource.ProjectRole newRole)
			throws Exception {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				delegate.addProjectRoleToMember(newRole);
				success = true;
			} catch (Exception ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
	}

	public LabRoles getLabRoles(java.lang.String username)
			throws RoleNotFoundSignal {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		edu.utah.nanofab.coralapi.collections.LabRoles returnValue = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				returnValue = delegate.getLabRoles(username);
				success = true;
			} catch (RoleNotFoundSignal ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
		return returnValue;
	}

	public EquipmentRoles getEquipmentRoles(java.lang.String username)
			throws RoleNotFoundSignal {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		edu.utah.nanofab.coralapi.collections.EquipmentRoles returnValue = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				returnValue = delegate.getEquipmentRoles(username);
				success = true;
			} catch (RoleNotFoundSignal ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
		return returnValue;
	}

	public void createNewRole(java.lang.String name,
			java.lang.String description, java.lang.String type)
			throws Exception {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				delegate.createNewRole(name, description, type);
				success = true;
			} catch (Exception ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
	}

	public Role getRole(java.lang.String name, java.lang.String type)
			throws InvalidRoleException {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		edu.utah.nanofab.coralapi.resource.Role returnValue = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				returnValue = delegate.getRole(name, type);
				success = true;
			} catch (InvalidRoleException ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
		return returnValue;
	}

	public void createNewReservation(
			edu.utah.nanofab.coralapi.resource.Reservation r)
			throws RequestFailedException {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				delegate.createNewReservation(r);
				success = true;
			} catch (RequestFailedException ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
	}

	public void createNewReservation(java.lang.String agent,
			java.lang.String member, java.lang.String project,
			java.lang.String item, java.lang.String bdate, int lengthInMinutes)
			throws RequestFailedException, Exception {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				delegate.createNewReservation(agent, member, project, item,
						bdate, lengthInMinutes);
				success = true;
			} catch (RequestFailedException ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
					throw ex2;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
	}

	public void deleteReservation(
			edu.utah.nanofab.coralapi.resource.Reservation r)
			throws InvalidTicketSignal, NotAuthorizedSignal,
			ReservationNotFoundSignal {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				delegate.deleteReservation(r);
				success = true;
			} catch (InvalidTicketSignal ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (NotAuthorizedSignal ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
					throw ex2;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (ReservationNotFoundSignal ex3) {
				caughtException = ex3;
				if (count >= numberOfRetries) {
					throw ex3;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex4) {
				caughtException = ex4;
				if (count >= numberOfRetries) {
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
	}

	public void deleteReservation(java.lang.String agent,
			java.lang.String member, java.lang.String project,
			java.lang.String item, java.lang.String bdate, int lengthInMinutes)
			throws ProjectNotFoundSignal, InvalidAccountSignal,
			MachineRetrievalFailedSignal, UnknownMemberException,
			ParseException, InvalidCallOrderException, Exception {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				delegate.deleteReservation(agent, member, project, item, bdate,
						lengthInMinutes);
				success = true;
			} catch (ProjectNotFoundSignal ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidAccountSignal ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
					throw ex2;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (MachineRetrievalFailedSignal ex3) {
				caughtException = ex3;
				if (count >= numberOfRetries) {
					throw ex3;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (UnknownMemberException ex4) {
				caughtException = ex4;
				if (count >= numberOfRetries) {
					throw ex4;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (ParseException ex5) {
				caughtException = ex5;
				if (count >= numberOfRetries) {
					throw ex5;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidCallOrderException ex6) {
				caughtException = ex6;
				if (count >= numberOfRetries) {
					throw ex6;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex7) {
				caughtException = ex7;
				if (count >= numberOfRetries) {
					throw ex7;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
	}

	public void deleteReservation(java.lang.String item,
			java.lang.String bdate, int lengthInMinutes)
			throws ProjectNotFoundSignal, InvalidAccountSignal,
			MachineRetrievalFailedSignal, UnknownMemberException,
			ParseException, InvalidCallOrderException, Exception {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				delegate.deleteReservation(item, bdate, lengthInMinutes);
				success = true;
			} catch (ProjectNotFoundSignal ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidAccountSignal ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
					throw ex2;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (MachineRetrievalFailedSignal ex3) {
				caughtException = ex3;
				if (count >= numberOfRetries) {
					throw ex3;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (UnknownMemberException ex4) {
				caughtException = ex4;
				if (count >= numberOfRetries) {
					throw ex4;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (ParseException ex5) {
				caughtException = ex5;
				if (count >= numberOfRetries) {
					throw ex5;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidCallOrderException ex6) {
				caughtException = ex6;
				if (count >= numberOfRetries) {
					throw ex6;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex7) {
				caughtException = ex7;
				if (count >= numberOfRetries) {
					throw ex7;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
	}

	public void deleteReservation(java.lang.String item, java.util.Date bdate,
			int lengthInMinutes) throws ProjectNotFoundSignal,
			InvalidAccountSignal, MachineRetrievalFailedSignal,
			UnknownMemberException, ParseException, InvalidCallOrderException,
			Exception {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				delegate.deleteReservation(item, bdate, lengthInMinutes);
				success = true;
			} catch (ProjectNotFoundSignal ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidAccountSignal ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
					throw ex2;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (MachineRetrievalFailedSignal ex3) {
				caughtException = ex3;
				if (count >= numberOfRetries) {
					throw ex3;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (UnknownMemberException ex4) {
				caughtException = ex4;
				if (count >= numberOfRetries) {
					throw ex4;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (ParseException ex5) {
				caughtException = ex5;
				if (count >= numberOfRetries) {
					throw ex5;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidCallOrderException ex6) {
				caughtException = ex6;
				if (count >= numberOfRetries) {
					throw ex6;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex7) {
				caughtException = ex7;
				if (count >= numberOfRetries) {
					throw ex7;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
	}

	public Reservations getReservations(java.lang.String member,
			java.lang.String tool, java.util.Date bdate, java.util.Date edate)
			throws Exception {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		edu.utah.nanofab.coralapi.collections.Reservations returnValue = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				returnValue = delegate.getReservations(member, tool, bdate,
						edate);
				success = true;
			} catch (Exception ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
		return returnValue;
	}

	public Reservations getReservations(java.lang.String member,
			java.lang.String tool, java.lang.String bdateAsString,
			int numberOfMinutes) throws Exception {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		edu.utah.nanofab.coralapi.collections.Reservations returnValue = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				returnValue = delegate.getReservations(member, tool,
						bdateAsString, numberOfMinutes);
				success = true;
			} catch (Exception ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
		return returnValue;
	}

	public Reservations getReservations(java.lang.String tool,
			java.util.Date bdate, java.util.Date edate) throws Exception {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		edu.utah.nanofab.coralapi.collections.Reservations returnValue = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				returnValue = delegate.getReservations(tool, bdate, edate);
				success = true;
			} catch (Exception ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
		return returnValue;
	}

	public String enable(
			edu.utah.nanofab.coralapi.resource.Enable enableActivity)
			throws InvalidTicketSignal, InvalidAgentSignal,
			InvalidProjectSignal, InvalidAccountSignal, InvalidMemberSignal,
			InvalidResourceSignal, InvalidProcessSignal,
			ResourceUnavailableSignal, NotAuthorizedSignal {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		java.lang.String returnValue = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				returnValue = delegate.enable(enableActivity);
				success = true;
			} catch (InvalidTicketSignal ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidAgentSignal ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
					throw ex2;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidProjectSignal ex3) {
				caughtException = ex3;
				if (count >= numberOfRetries) {
					throw ex3;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidAccountSignal ex4) {
				caughtException = ex4;
				if (count >= numberOfRetries) {
					throw ex4;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidMemberSignal ex5) {
				caughtException = ex5;
				if (count >= numberOfRetries) {
					throw ex5;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidResourceSignal ex6) {
				caughtException = ex6;
				if (count >= numberOfRetries) {
					throw ex6;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidProcessSignal ex7) {
				caughtException = ex7;
				if (count >= numberOfRetries) {
					throw ex7;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (ResourceUnavailableSignal ex8) {
				caughtException = ex8;
				if (count >= numberOfRetries) {
					throw ex8;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (NotAuthorizedSignal ex9) {
				caughtException = ex9;
				if (count >= numberOfRetries) {
					throw ex9;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex10) {
				caughtException = ex10;
				if (count >= numberOfRetries) {
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
		return returnValue;
	}

	public String enable(java.lang.String agent, java.lang.String member,
			java.lang.String project, java.lang.String account,
			java.lang.String machineName) throws UnknownMemberException,
			Exception {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		java.lang.String returnValue = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				returnValue = delegate.enable(agent, member, project, account,
						machineName);
				success = true;
			} catch (UnknownMemberException ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
					throw ex2;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
		return returnValue;
	}

	public String enable(java.lang.String agent, java.lang.String member,
			java.lang.String project, java.lang.String machineName)
			throws UnknownMemberException, Exception {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		java.lang.String returnValue = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				returnValue = delegate.enable(agent, member, project,
						machineName);
				success = true;
			} catch (UnknownMemberException ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
					throw ex2;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
		return returnValue;
	}

	public String disable(java.lang.String agent, java.lang.String machine)
			throws InvalidTicketSignal, InvalidAgentSignal,
			InvalidResourceSignal, ResourceUnavailableSignal,
			NotAuthorizedSignal {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		java.lang.String returnValue = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				returnValue = delegate.disable(agent, machine);
				success = true;
			} catch (InvalidTicketSignal ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidAgentSignal ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
					throw ex2;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidResourceSignal ex3) {
				caughtException = ex3;
				if (count >= numberOfRetries) {
					throw ex3;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (ResourceUnavailableSignal ex4) {
				caughtException = ex4;
				if (count >= numberOfRetries) {
					throw ex4;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (NotAuthorizedSignal ex5) {
				caughtException = ex5;
				if (count >= numberOfRetries) {
					throw ex5;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex6) {
				caughtException = ex6;
				if (count >= numberOfRetries) {
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
		return returnValue;
	}

	public String disableWithRundata(java.lang.String agent,
			java.lang.String machine, java.lang.String rundataId)
			throws InvalidTicketSignal, InvalidAgentSignal,
			InvalidResourceSignal, ResourceUnavailableSignal,
			NotAuthorizedSignal {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		java.lang.String returnValue = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				returnValue = delegate.disableWithRundata(agent, machine,
						rundataId);
				success = true;
			} catch (InvalidTicketSignal ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidAgentSignal ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
					throw ex2;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidResourceSignal ex3) {
				caughtException = ex3;
				if (count >= numberOfRetries) {
					throw ex3;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (ResourceUnavailableSignal ex4) {
				caughtException = ex4;
				if (count >= numberOfRetries) {
					throw ex4;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (NotAuthorizedSignal ex5) {
				caughtException = ex5;
				if (count >= numberOfRetries) {
					throw ex5;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex6) {
				caughtException = ex6;
				if (count >= numberOfRetries) {
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
		return returnValue;
	}

	public void updatePassword(java.lang.String member,
			java.lang.String newPassword) throws InvalidMemberSignal,
			NotAuthorizedSignal, InvalidTicketSignal {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				delegate.updatePassword(member, newPassword);
				success = true;
			} catch (InvalidMemberSignal ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (NotAuthorizedSignal ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
					throw ex2;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (InvalidTicketSignal ex3) {
				caughtException = ex3;
				if (count >= numberOfRetries) {
					throw ex3;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex4) {
				caughtException = ex4;
				if (count >= numberOfRetries) {
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
	}

	public boolean checkKeyIsValid() {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		boolean returnValue = false;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				returnValue = delegate.checkKeyIsValid();
				success = true;
			} catch (Exception ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
		return returnValue;
	}

	public String getRundataDefinitionForProcess(java.lang.String process)
			throws NullReturnException, ServerErrorException {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		java.lang.String returnValue = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				returnValue = delegate.getRundataDefinitionForProcess(process);
				success = true;
			} catch (NullReturnException ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (ServerErrorException ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
					throw ex2;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex3) {
				caughtException = ex3;
				if (count >= numberOfRetries) {
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
		return returnValue;
	}

	public RunDataProcess[] getRundataProcesses(java.lang.String tool)
			throws NullReturnException, ServerErrorException {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		edu.utah.nanofab.coralapi.resource.RunDataProcess[] returnValue = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				returnValue = delegate.getRundataProcesses(tool);
				success = true;
			} catch (NullReturnException ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (ServerErrorException ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
					throw ex2;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex3) {
				caughtException = ex3;
				if (count >= numberOfRetries) {
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
		return returnValue;
	}

	public RunDataProcess[] getRundataProcessesWithDefinitions(
			java.lang.String tool) throws NullReturnException,
			ServerErrorException {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		edu.utah.nanofab.coralapi.resource.RunDataProcess[] returnValue = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				returnValue = delegate.getRundataProcessesWithDefinitions(tool);
				success = true;
			} catch (NullReturnException ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (ServerErrorException ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
					throw ex2;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex3) {
				caughtException = ex3;
				if (count >= numberOfRetries) {
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
		return returnValue;
	}

	public String createRunData(java.lang.String rundata)
			throws NullReturnException, ServerErrorException {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		java.lang.String returnValue = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				returnValue = delegate.createRunData(rundata);
				success = true;
			} catch (NullReturnException ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (ServerErrorException ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
					throw ex2;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex3) {
				caughtException = ex3;
				if (count >= numberOfRetries) {
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
		return returnValue;
	}

	public void updateRunData(java.lang.String rundata)
			throws NullReturnException, ServerErrorException {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				delegate.updateRunData(rundata);
				success = true;
			} catch (NullReturnException ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (ServerErrorException ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
					throw ex2;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex3) {
				caughtException = ex3;
				if (count >= numberOfRetries) {
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
	}

	public void commitRunData(java.lang.String id) throws NullReturnException,
			ServerErrorException {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				delegate.commitRunData(id);
				success = true;
			} catch (NullReturnException ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (ServerErrorException ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
					throw ex2;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex3) {
				caughtException = ex3;
				if (count >= numberOfRetries) {
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
	}

	public String createAndCommitRunData(java.lang.String xmlDefinition)
			throws NullReturnException, ServerErrorException, Exception {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		java.lang.String returnValue = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				returnValue = delegate.createAndCommitRunData(xmlDefinition);
				success = true;
			} catch (NullReturnException ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
					throw ex1;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (ServerErrorException ex2) {
				caughtException = ex2;
				if (count >= numberOfRetries) {
					throw ex2;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			} catch (Exception ex3) {
				caughtException = ex3;
				if (count >= numberOfRetries) {
					throw ex3;
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
		return returnValue;
	}

	public RundataAdapter rundataFromXml(java.lang.String xml) {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		org.opencoral.corba.RundataAdapter returnValue = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				returnValue = delegate.rundataFromXml(xml);
				success = true;
			} catch (Exception ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
		return returnValue;
	}

	public void createAdjustmentRunData() {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				delegate.createAdjustmentRunData();
				success = true;
			} catch (Exception ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
	}

	public void addActivityToRundata(java.lang.String arg1,
			java.lang.String arg2) {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				delegate.addActivityToRundata(arg1, arg2);
				success = true;
			} catch (Exception ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
	}

	public String getLogLevel() {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		java.lang.String returnValue = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				returnValue = delegate.getLogLevel();
				success = true;
			} catch (Exception ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
		return returnValue;
	}

	public void setLogLevel(java.lang.String logLevel) {
		int count = 0;
		boolean success = false;
		Exception caughtException = null;
		while (count < numberOfRetries && !success) {
			count++;
			try {
				delegate.setLogLevel(logLevel);
				success = true;
			} catch (Exception ex1) {
				caughtException = ex1;
				if (count >= numberOfRetries) {
				}
				try {
					this.delegate.reInitialize();
				} catch (Exception innerEx) {
				}
			}
		}
	}
}
