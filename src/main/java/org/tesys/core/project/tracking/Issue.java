package org.tesys.core.project.tracking;

public interface Issue {
	
	public Integer getKey() ;
	
	public Integer getResolutiondate() ;
	
	public String getAggregatetimeoriginalestimate();
	
	public String getUpdated() ;
	
	public String getCreated() ;
	
	public String getWorkratio() ;
	
	public String getAggregatetimeestimate() ;

	public User getReporter() ;

	public Issue[] getSubtasks() ;

}
