package org.rul.meapi.model;

/**
 * Created by rgonzalez on 23/05/2016.
 */
public class  CommandAbstract {

    private String name;
    private int type;
    private int index;

    public CommandAbstract(String name, int type, int index) {
        this.name = name;
        this.type = type;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
