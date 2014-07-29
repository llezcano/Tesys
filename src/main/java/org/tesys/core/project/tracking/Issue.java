package org.tesys.core.project.tracking;

public interface Issue {
	
	public String getKey() ;
	
	public String getResolutiondate() ;
	
	public String getAggregatetimeoriginalestimate();
	
	public String getUpdated() ;
	
	public String getCreated() ;
	
	public String getWorkratio() ;
	
	public String getAggregatetimeestimate() ;

	public String getReporter() ;

	public String[] getSubtasks() ;

}
