package in.kay.flixtube.Model;

public class MovieModel {
    String imdb;
    String category;
    String title;
    String trailer;
    String image;
    String type;
    String url;
    String featured;

    public String getFeatured() {
        return featured;
    }

    public MovieModel() {
    }

    public String getImage() {
        return image;
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

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public MovieModel(String imdb, String category, String title, String trailer, String type, String url, String image,String featured) {
        this.imdb = imdb;
        this.category = category;
        this.title = title;
        this.trailer = trailer;
        this.type = type;
        this.url = url;
        this.image = image;
        this.featured = featured;
    }

}
