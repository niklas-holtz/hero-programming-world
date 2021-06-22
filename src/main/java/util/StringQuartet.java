package util;

import javafx.beans.property.SimpleStringProperty;

public class StringQuartet {

    private final SimpleStringProperty  first;
    private final SimpleStringProperty  second;
    private final SimpleStringProperty  third;
    private final SimpleStringProperty  fourth;

    public StringQuartet(String first, String second, String third, String fourth) {
        this.first = new SimpleStringProperty(first);
        this.second = new SimpleStringProperty(second);
        this.third = new SimpleStringProperty(third);
        this.fourth = new SimpleStringProperty(fourth);
    }

    public String getFirst() { return first.get(); }
    public String getSecond() { return second.get(); }
    public String getThird() { return third.get(); }
    public String getFourth() { return fourth.get(); }
}
