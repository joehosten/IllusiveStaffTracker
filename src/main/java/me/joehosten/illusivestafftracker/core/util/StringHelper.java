package me.joehosten.illusivestafftracker.core.util;

import lombok.Getter;
import lombok.Setter;

public class StringHelper {

    @Getter
    private final String initial;
    @Getter
    @Setter
    private String message;

    /**
     * Message Constructor (as String)
     *
     * @param msg Message
     */
    public StringHelper(String... msg) {
        String actual = String.join("\n", msg);
        setMessage(actual);
        this.initial = actual;
    }

    /**
     * Replacer
     * <p>
     * Simply replaces object1 with object2.
     * Could be a string, int, double, whatever
     *
     * @param o1 - Object 1
     * @param o2 - Object 2
     * @return - Returns replaced value
     */
    public StringHelper replace(Object o1, Object o2) {
        if (o2 instanceof Integer || o2 instanceof Double || o2 instanceof Long) {
            o2 = me.joehosten.illusivestafftracker.core.util.BotUtils.decimalFormat(o2);
        }

        String newMSG = this.message;

        newMSG = newMSG.replaceAll(String.valueOf(o1), String.valueOf(o2));

        setMessage(newMSG);
        return this;
    }

}
