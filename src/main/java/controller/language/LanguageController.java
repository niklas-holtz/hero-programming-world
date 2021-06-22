package controller.language;

import java.util.Locale;
import java.util.ResourceBundle;

import controller.GameController;
import controller.HeroSimulatorStage;

public class LanguageController extends GameController {

    private static final Locale de = Locale.GERMAN;
    private static final Locale en = Locale.ENGLISH;
    private ResourceBundle res;

    public LanguageController(HeroSimulatorStage stage) {
        super(stage);
        this.setLanguage(stage.getPropLoader().getLanguage(), false);
    }

    public ResourceBundle getRes() {
        return this.res;
    }

    public void setLanguage(String lang, boolean update) {
        switch (lang) {
            case ("de"):
                this.getGerman();
                super.sim.getPropLoader().setLanguage("de");
                break;
            case ("en"):
            default:
                this.getEnglish();
                super.sim.getPropLoader().setLanguage("en");
                break;
        }
        if (update) super.sim.updateLanguage();
    }

    private void getGerman() {
        this.res = ResourceBundle.getBundle("language", de);
    }

    private void getEnglish() {
        this.res = ResourceBundle.getBundle("language", en);
    }
}
