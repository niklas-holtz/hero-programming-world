package controller.database;

import java.io.File;

import controller.GameController;
import controller.HeroSimulatorStage;
import models.Map;
import views.MapPanel;

public class DBController extends GameController {

    private DBManager dbm;

    public DBController(Map m, MapPanel mp, HeroSimulatorStage sim) {
        super(m, mp, sim);
        dbm = new DBManager();
    }

    public void closeConnection() {
        this.dbm.shutdown();
    }

    public synchronized void saveExample(String t, String name) {
        File file;
        String final_name = "examples/" + name;
        for (int counter = 0; counter < 100; counter++) {
            String adder = (counter > 0) ? "(" + Integer.toString(counter) + ")" : "";
            final_name = "examples/" + name + adder + ".xml";
            if (!new File(final_name).exists())
                break;
        }
        file = new File(final_name);
        super.sim.getMapController().saveXML(file);
        String code = super.sim.getEditor().getText();
        this.dbm.saveExample(t, file, code);

    }

    public synchronized void deleteExample(int id) {
        this.dbm.deleteExample(id);
    }

    public synchronized String[][] getAllExamplesFromDB() {
        return this.dbm.getAllExamplesAsArray();
    }

    public synchronized void loadExample(int id) {
        String code = this.dbm.getExampleCode(id);
        String mapPath = this.dbm.getExampleMap(id);
        if (code != null && mapPath != null) {
            super.sim.getEditor().setText(code);
            super.sim.getMapController().loadXML(new File(mapPath));
        }
    }
}
