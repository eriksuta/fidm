package com.esuta.fidm.repository.schema;

import java.util.ArrayList;
import java.util.List;

/**
 *  @author shood
 * */
public class ProjectType extends ObjectType{

    private ObjectReferenceType leader;
    private List<ObjectReferenceType> governors;

    private List<String> projectType;
    private String locality;

    private List<ObjectReferenceType> members;

    private List<ObjectReferenceType> parentProjects;

    public ProjectType(){}

    public ObjectReferenceType getLeader() {
        return leader;
    }

    public void setLeader(ObjectReferenceType leader) {
        this.leader = leader;
    }

    public List<ObjectReferenceType> getGovernors() {
        if(governors == null){
            governors = new ArrayList<ObjectReferenceType>();
        }

        return governors;
    }

    public void setGovernors(List<ObjectReferenceType> governors) {
        this.governors = governors;
    }

    public List<String> getProjectType() {
        if(projectType == null){
            projectType = new ArrayList<String>();
        }

        return projectType;
    }

    public void setProjectType(List<String> projectType) {
        this.projectType = projectType;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public List<ObjectReferenceType> getMembers() {
        if(members == null){
            members = new ArrayList<ObjectReferenceType>();
        }

        return members;
    }

    public void setMembers(List<ObjectReferenceType> members) {
        this.members = members;
    }

    public List<ObjectReferenceType> getParentProjects() {
        if(parentProjects == null){
            parentProjects = new ArrayList<ObjectReferenceType>();
        }

        return parentProjects;
    }

    public void setParentProjects(List<ObjectReferenceType> parentProjects) {
        this.parentProjects = parentProjects;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProjectType)) return false;
        if (!super.equals(o)) return false;

        ProjectType that = (ProjectType) o;

        if (governors != null ? !governors.equals(that.governors) : that.governors != null) return false;
        if (leader != null ? !leader.equals(that.leader) : that.leader != null) return false;
        if (locality != null ? !locality.equals(that.locality) : that.locality != null) return false;
        if (members != null ? !members.equals(that.members) : that.members != null) return false;
        if (parentProjects != null ? !parentProjects.equals(that.parentProjects) : that.parentProjects != null)
            return false;
        if (projectType != null ? !projectType.equals(that.projectType) : that.projectType != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (leader != null ? leader.hashCode() : 0);
        result = 31 * result + (governors != null ? governors.hashCode() : 0);
        result = 31 * result + (projectType != null ? projectType.hashCode() : 0);
        result = 31 * result + (locality != null ? locality.hashCode() : 0);
        result = 31 * result + (members != null ? members.hashCode() : 0);
        result = 31 * result + (parentProjects != null ? parentProjects.hashCode() : 0);
        return result;
    }
}
