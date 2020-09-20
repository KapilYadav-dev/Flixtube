package in.kay.flixtube.Model;

public class UsersModel {
    String Email,Membership,MobileUid,Name,Violation;

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getMembership() {
        return Membership;
    }

    public void setMembership(String membership) {
        Membership = membership;
    }

    public String getMobileUid() {
        return MobileUid;
    }

    public void setMobileUid(String mobileUid) {
        MobileUid = mobileUid;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getViolation() {
        return Violation;
    }

    public void setViolation(String violation) {
        Violation = violation;
    }

    public UsersModel() {
    }

    public UsersModel(String email, String membership, String mobileUid, String name, String violation) {
        Email = email;
        Membership = membership;
        MobileUid = mobileUid;
        Name = name;
        Violation = violation;
    }
}
