package in.kay.flixtube.Model;

public class CastModel {
    String profile_path,name;

    public String getProfile_path() {
        return profile_path;
    }

    public CastModel(String profile_path, String name) {
        this.profile_path = profile_path;
        this.name = name;
    }

    public void setProfile_path(String profile_path) {
        this.profile_path = profile_path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
