package shanshan.tsinghua.edu.cn.model;

import java.io.Serializable;

/**
 * Created by hujiawei on 14/11/20.
 * <p/>
 * model Project
 */

public class Project implements Serializable{

    private String id;
    private String name;
    private String content;
    private String organiser;
    private String email;
    private String imgfile;
    private int pcount;
    private float target;
    private float mcount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getOrganiser() {
        return organiser;
    }

    public void setOrganiser(String organiser) {
        this.organiser = organiser;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImgfile() {
        return imgfile;
    }

    public void setImgfile(String imgfile) {
        this.imgfile = imgfile;
    }

    public int getPcount() {
        return pcount;
    }

    public void setPcount(int pcount) {
        this.pcount = pcount;
    }

    public float getTarget() {
        return target;
    }

    public void setTarget(float target) {
        this.target = target;
    }

    public float getMcount() {
        return mcount;
    }

    public void setMcount(float mcount) {
        this.mcount = mcount;
    }
}
