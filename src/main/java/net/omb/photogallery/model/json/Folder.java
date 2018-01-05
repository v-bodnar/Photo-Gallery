package net.omb.photogallery.model.json;

import java.util.LinkedList;
import java.util.List;

public class Folder {
    private String name;
    private String path;
    private List<Folder> children;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<Folder> getChildren() {
        return children;
    }

    public void setChildren(List<Folder> children) {
        this.children = children;
    }

    public void addChild(Folder child){
        if(children == null){
            children = new LinkedList<>();
        }
        children.add(child);
    }
}
