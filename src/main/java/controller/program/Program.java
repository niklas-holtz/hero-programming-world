package controller.program;

public class Program {

    private String name;
    private String code;

    public Program(String name) {
        this.name = name;
        this.code = "void main() {\n\n}";
    }

    public Program(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String getPrefix() {
        return new String("import models.*; import annotations.*; public class " + this.name + " extends Hero { public " + this.name + "(Map map) { super(map);} public \n");
    }

    @Override
    public String toString() {
        return getPrefix() + this.code + "\n}";
    }
}
