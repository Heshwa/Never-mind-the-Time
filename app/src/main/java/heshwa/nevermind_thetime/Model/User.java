package heshwa.nevermind_thetime.Model;

public class User
{
    private String id;
    private String username;
    private String imageURL;
    private String status;
    private String search;

    public User()
    {

    }

    public User(String id, String username, String imageURL, String status,String search)
    {
        this.id = id;
        this.username = username;
        this.imageURL = imageURL;
        this.status = status;
        this.search=search;
    }

    public String getid()
    {
        return id;
    }
    public void setid(String id)
    {
        this.id=id;
    }

    public String getusername()
    {
        return username;
    }
    public void setusername(String usename)
    {
        this.username=usename;
    }
    public String getImageURL()
    {
        return imageURL;
    }
    public void setImageURL(String imageURL)
    {
        this.imageURL=imageURL;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
