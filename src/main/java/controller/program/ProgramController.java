package controller.program;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.lang.model.SourceVersion;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import controller.GameController;
import controller.HeroSimulatorStage;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import models.Hero;
import models.Map;

public class ProgramController extends GameController {

    private static ArrayList<Program> programs = new ArrayList<Program>();
    private Program current;

    public ProgramController(HeroSimulatorStage sim, Map m) {
        super(sim, m);
    }


    public synchronized boolean compileCurrent(boolean alert) {
        final String file = getProgramPath();
        autoSaveProgram();
        JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        boolean success = javac.run(null, null, err, file) == 0;
        if (!success) {
            if (alert) {
                Alert info = new Alert(AlertType.WARNING, "Kompilierfehler!\n" + err.toString(), ButtonType.OK);
                info.showAndWait();
            }
            return false;
        } else {
            try {
                URL[] urls = new URL[]{new File(super.sim.getDir().getAbsolutePath()).toURI().toURL()};
                @SuppressWarnings("resource")
                URLClassLoader classLoader = new URLClassLoader(urls);
                Class<?> newHero = classLoader.loadClass(this.getProgram().getName());
                Object k = newHero.getDeclaredConstructor(Map.class).newInstance(this.getMap());
                super.map.setHero((Hero) k);
            } catch (ClassNotFoundException | MalformedURLException | IllegalAccessException | InstantiationException | NoSuchMethodException | IllegalArgumentException | InvocationTargetException | SecurityException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public synchronized boolean autoSaveProgram() {
        // Check if exits
        File[] list = super.sim.getDir().listFiles();
        for (File file : list) {
            if (file.getName().equals(current.getName() + ".java")) {
                return saveProgram(file);
            }
        }
        // Else ...
        return saveProgram(new File(super.sim.getDir() + "\\" + current.getName() + ".java"));
    }

    public synchronized boolean saveProgram(File file) {
        refreshCode();
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(current.toString());
            fileWriter.close();
        } catch (IOException e) {
            Logger.getLogger(HeroSimulatorStage.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
        // Refresh title & name
        String fname = file.getName();
        int pos = fname.lastIndexOf(".");
        if (pos > 0) {
            fname = fname.substring(0, pos);
        }
        if (!super.sim.getTitle().equals(super.sim.getAPPNAME() + ": " + fname)) {
            this.current.setName(fname);
            super.sim.setTitle(super.sim.getAPPNAME() + ": " + fname);
        }
        return true;
    }

    public void refreshCode() {
        this.current.setCode(super.sim.getEditor().getText());
    }

    public void addProgram(Program p) {
        programs.add(p);
    }

    public Program getProgram() {
        return this.current;
    }

    public String getProgramPath() {
        return this.getStage().getDir().getAbsolutePath() + "\\" + current.getName() + ".java";
    }

    public void setCurrent(Program p) {
        this.current = p;
        programs.add(p);
    }

    public void deleteCurrent() {
        programs.remove(current);
        current = null;
    }

    public synchronized void openProgramOnCurrentStage(String name, String... code) {
        Program prog;
        if (code.length > 0) {
            prog = new Program(name, code[0]);
        } else {
            prog = new Program(name);
        }
        // This.getStage().getSimManager().stop();
        this.deleteCurrent();
        this.setCurrent(prog);
        super.sim.getEditor().setText(prog.getCode());
        this.compileCurrent(false);
    }


    public synchronized void startNewStage(String name, String... code) {
        Program prog;
        if (code.length > 0) {
            prog = new Program(name, code[0]);
        } else {
            prog = new Program(name);
        }

        Map map = new Map();
        new HeroSimulatorStage(map, prog);
    }

    public synchronized boolean isProgramOpen(String title) {
        for (Program p : programs) {
            if (p.getName().equals(title)) {
                return true;
            }
        }
        return false;
    }

    public boolean isValidFileName(String className) {
        // https://stackoverflow.com/questions/13979172/how-to-check-if-the-class-name-is-valid
        return SourceVersion.isIdentifier(className) && !SourceVersion.isKeyword(className);
    }
}
