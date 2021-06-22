package controller.mouse;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Alert.AlertType;
import models.Hero;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

import annotations.Invisible;
import controller.HeroSimulatorStage;
import controller.SoundController;

public class HeroContextMenu extends ContextMenu {

    public HeroContextMenu(Hero hero, HeroSimulatorStage sim) {
        List<Method> methods = new ArrayList<Method>();
        List<Method> newMethods = getMethods(hero.getClass());        // To get the size for the seperator
        methods.addAll(newMethods);
        methods.addAll(getMethods(Hero.class));
        int counter = 0;    // For separators
        for (Method i : methods) {
            if (counter == newMethods.size()) this.getItems().add(new SeparatorMenuItem());
            MenuItem m = new MenuItem(i.getReturnType() + " " + i.getName() + "(" + parametersToString(i) + ")");
            if (i.getParameterCount() > 0) m.setDisable(true);
            m.setOnAction(event -> {
                try {
                    if (!i.getReturnType().equals(Void.TYPE)) {
                        Alert alert = new Alert(AlertType.INFORMATION, "Du hast die Methode '" + i.getReturnType() + " " + i.getName() + "()' aufgerufen. \nDie Methode übergibt: " + i.invoke(hero) + ".", ButtonType.OK);
                        alert.showAndWait();
                        return;
                    }
                    i.invoke(hero);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException ex) {
                    //Invoke has thrown an exception => Sound
                    sim.getSoundController().playSound(SoundController.Sounds.ERROR);
                }
            });
            this.getItems().add(m);
            counter++;
        }
    }

    private String parametersToString(Method m) {
        Parameter[] p = m.getParameters();
        String output = new String();
        for (int i = 0; i < p.length; i++) {
            if (i == p.length - 1) {
                //last
                output += p[i].getName();
                continue;
            }
            output += p[i].getName() + " ,";
        }
        return output;
    }

    public List<Method> getMethods(Class<? extends Hero> hero) {
        Method[] heroMethods = hero.getDeclaredMethods();
        List<Method> container = new ArrayList<Method>();
        for (Method i : heroMethods) {
            //check if static or private
            Invisible visible = i.getAnnotation(Invisible.class);
            if (Modifier.isStatic(i.getModifiers()) || Modifier.isPrivate(i.getModifiers()) || visible != null)
                continue;
            i.setAccessible(true);
            container.add(i);
        }
        return container;
    }
}
