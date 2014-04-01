package edu.utah.nanofab.coralapi.collections;

import edu.utah.nanofab.coralapi.resource.Project;


public class Projects extends ProxySet<Project> {
	@Override
	public boolean contains(Object projectObject) 
	{
		Project project = (Project)projectObject;
		for (Project m : this.collection ) {
			if (projectsEqual(project, m)) {
				return true;
			}
		}
		return false;
	}

	private boolean projectsEqual(Project project, Project m) {
		System.out.println("checking if " + project.getName() + " is equal to " + m.getName());
		return m.equals(project);
	}
	
	public static Projects fromIdlProjectArray(
			org.opencoral.idl.Project[] allProjects) {
		Projects projectCollection = new Projects();
		for(org.opencoral.idl.Project idlProject : allProjects) {
			Project p = new Project();
			p.populateFromIdlProject(idlProject);
			projectCollection.add(p);
		}
    	return projectCollection;
	}
}
