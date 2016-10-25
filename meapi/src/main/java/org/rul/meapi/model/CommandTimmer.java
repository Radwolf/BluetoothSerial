package org.rul.meapi.model;

/**
 * Created by rgonzalez on 23/05/2016.
 */
public class CommandTimmer extends CommandAbstract{

    private long timeMilis;

    public CommandTimmer(String name, int type, int index, long timeMilis) {
        super(name, type, index);
        this.timeMilis = timeMilis;
    }

    public long getTimeMilis() {
        return timeMilis;
    }

    public void setTimeMilis(long timeMilis) {
        this.timeMilis = timeMilis;
    }

    @Override
    public String toString() {
        return "CommandTimmer{" +
                "timeMilis=" + timeMilis +
                '}';
    }
}
