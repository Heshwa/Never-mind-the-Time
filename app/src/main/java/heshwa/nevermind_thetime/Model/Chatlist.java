package heshwa.nevermind_thetime.Model;

public class Chatlist
{
    public String id;
    public  Object priority;

    public Chatlist() {
    }

    public Chatlist(String id,Object priority) {
        this.id = id;
        this.priority=priority;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object getPriority() {
        return priority;
    }

    public void setPriority(Object priority) {
        this.priority = priority;
    }
    public  void getpriority(String id)
    {
         getPriority();
    }
}
