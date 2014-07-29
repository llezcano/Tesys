package org.tesys.core.project.tracking;

import java.util.Arrays;


//import java.util.Date;

public class IssuePOJO implements Issue  {
	
	private String key;
	
	private Progress progress;

	private String[] subtasks;

	private String issuetype;

	private String status;

	private String workratio;

	private String assignee;

	private String resolutiondate;

	private String aggregatetimeestimate;

	private String reporter;

	private String aggregatetimeoriginalestimate;

	private String project;

	private String updated;

	private String created;

	private String priority;


	public void setKey (String key)
	{
	this.key = key;
	}

	
	public Progress getProgress ()
	{
	return progress;
	}

	public void setProgress (Progress progress)
	{
	this.progress = progress;
	}

	public String[] getSubtasks ()
	{
	return subtasks;
	}

	public void setSubtasks (String[] subtasks)
	{
	this.subtasks = subtasks;
	}

	public String getIssuetype ()
	{
	return issuetype;
	}

	public void setIssuetype (String issuetype)
	{
	this.issuetype = issuetype;
	}

	public String getStatus ()
	{
	return status;
	}

	public void setStatus (String status)
	{
	this.status = status;
	}

	public String getWorkratio ()
	{
	return workratio;
	}

	public void setWorkratio (String workratio)
	{
	this.workratio = workratio;
	}

	public String getAssignee ()
	{
	return assignee;
	}

	public void setAssignee (String assignee)
	{
	this.assignee = assignee;
	}

	public String getResolutiondate ()
	{
	return resolutiondate;
	}

	public void setResolutiondate (String resolutiondate)
	{
	this.resolutiondate = resolutiondate;
	}

	public String getAggregatetimeestimate ()
	{
	return aggregatetimeestimate;
	}

	public void setAggregatetimeestimate (String aggregatetimeestimate)
	{
	this.aggregatetimeestimate = aggregatetimeestimate;
	}

	public String getReporter ()
	{
	return reporter;
	}

	public void setReporter (String reporter)
	{
	this.reporter = reporter;
	}

	public String getAggregatetimeoriginalestimate ()
	{
	return aggregatetimeoriginalestimate;
	}

	public void setAggregatetimeoriginalestimate (String aggregatetimeoriginalestimate)
	{
	this.aggregatetimeoriginalestimate = aggregatetimeoriginalestimate;
	}

	public String getProject ()
	{
	return project;
	}

	public void setProject (String project)
	{
	this.project = project;
	}

	public String getUpdated ()
	{
	return updated;
	}

	public void setUpdated (String updated)
	{
	this.updated = updated;
	}

	public String getCreated ()
	{
	return created;
	}

	public void setCreated (String created)
	{
	this.created = created;
	}

	public String getPriority ()
	{
	return priority;
	}

	public void setPriority (String priority)
	{
	this.priority = priority;
	}

	@Override
	public String getKey() {
		// TODO Auto-generated method stub
		return key;
	}


	@Override
	public String toString() {
		return "IssuePOJO [key=" + key + ", progress=" + progress
				+ ", subtasks=" + Arrays.toString(subtasks) + ", issuetype="
				+ issuetype + ", status=" + status + ", workratio=" + workratio
				+ ", assignee=" + assignee + ", resolutiondate="
				+ resolutiondate + ", aggregatetimeestimate="
				+ aggregatetimeestimate + ", reporter=" + reporter
				+ ", aggregatetimeoriginalestimate="
				+ aggregatetimeoriginalestimate + ", project=" + project
				+ ", updated=" + updated + ", created=" + created
				+ ", priority=" + priority + "]";
	}


}
