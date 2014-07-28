package org.tesys.core.project.tracking;

//import java.util.Date;

public class IssuePOJO implements Issue  {
	
	private Integer key ;

	private Integer resolutiondate ;
	
//	private Progress progressPOJO ;

	private String aggregatetimeoriginalestimate ;

	private String updated ;

	private String created ;

	private String workratio ;
	
	private String aggregatetimeestimate ;
	
// private IssueTypePOJO issuetype ;	

	private UserPOJO reporter ;
	
// private PriorityPOJO priority ;
	
// private StatusPOJO status ;
	
// private ProjectPOJO project ;

//	private UserPOJO assignee ;
	
	private IssuePOJO[] subtasks ;


	@Override
	public Integer getKey() {
		return key;
	}

	public void setKey(Integer key) {
		this.key = key;
	}

	@Override
	public Integer getResolutiondate() {
		return resolutiondate;
	}

	public void setResolutiondate(Integer resolutiondate) {
		this.resolutiondate = resolutiondate;
	}

	@Override
	public String getAggregatetimeoriginalestimate() {
		return aggregatetimeoriginalestimate;
	}

	public void setAggregatetimeoriginalestimate(
			String aggregatetimeoriginalestimate) {
		this.aggregatetimeoriginalestimate = aggregatetimeoriginalestimate;
	}

	@Override
	public String getUpdated() {
		return updated;
	}

	public void setUpdated(String updated) {
		this.updated = updated;
	}

	@Override
	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	@Override
	public String getWorkratio() {
		return workratio;
	}

	public void setWorkratio(String workratio) {
		this.workratio = workratio;
	}

	@Override
	public String getAggregatetimeestimate() {
		return aggregatetimeestimate;
	}

	public void setAggregatetimeestimate(String aggregatetimeestimate) {
		this.aggregatetimeestimate = aggregatetimeestimate;
	}

	@Override
	public UserPOJO getReporter() {
		return reporter;
	}

	public void setReporter(UserPOJO reporter) {
		this.reporter = reporter;
	}

	@Override
	public IssuePOJO[] getSubtasks() {
		return subtasks;
	}

	public void setSubtasks(IssuePOJO[] subtasks) {
		this.subtasks = subtasks;
	}



}
