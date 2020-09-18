package in.kay.flixtube.Model;

public class SeriesModel {
    String imdb;
    String category;
    String title;
    String trailer;
    String image;
    String type;
    String episode, url, season;

    public String getEpisode() {
        return episode;
    }

    public String getUrl() {
        return url;
    }

    public String getSeason() {
        return season;
    }

    public SeriesModel(String episode, String url, String season) {
        this.episode = episode;
        this.url = url;
        this.season = season;
    }

    public String getImdb() {
        return imdb;
    }

    public String getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public String getTrailer() {
        return trailer;
    }

    public String getImage() {
        return image;
    }

    public String getType() {
        return type;
    }

    public SeriesModel() {
    }

    public SeriesModel(String imdb, String category, String title, String trailer, String image, String type) {
        this.imdb = imdb;
        this.category = category;
        this.title = title;
        this.trailer = trailer;
        this.image = image;
        this.type = type;
    }
}
