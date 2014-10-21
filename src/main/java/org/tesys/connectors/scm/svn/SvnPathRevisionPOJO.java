package org.tesys.connectors.scm.svn;

public class SvnPathRevisionPOJO {
    
    private String path;
    private Integer revision;
    
    public SvnPathRevisionPOJO() {
	//jackson
    }

    public SvnPathRevisionPOJO(String path, Integer revision) {
	this.path = path;
	this.revision = revision;
    }
    
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public Integer getRevision() {
        return revision;
    }
    public void setRevision(Integer revision) {
        this.revision = revision;
    }

    @Override
    public String toString() {
	return "SvnPathRevisionPOJO [path=" + path + ", revision=" + revision
		+ "]";
    }
    
    

}
