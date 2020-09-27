package in.kay.flixtube.Model;

public class WatchlistModel {
    String image,title;

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public WatchlistModel() {
    }

    public WatchlistModel(String image, String title) {
        this.image = image;
        this.title = title;
    }
}
