package controller;

import controller.buttons.CoinButton;
import controller.database.DBController;
import controller.database.LoadExampleEventHandler;
import controller.database.SaveExampleEventHandler;
import controller.language.LanguageController;
import controller.map.ChangeCoinsEventHandler;
import controller.map.MapController;
import controller.map.ResizeMapEventHandler;
import controller.mouse.MouseController;
import controller.program.NewFileEventHandler;
import controller.program.OpenFileEventHandler;
import controller.program.Program;
import controller.program.ProgramController;
import controller.simulation.SimulationManager;
import controller.simulation.SimulationState;
import controller.tutor.TutorController;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.Map;
import util.properties.PropLoader;
import views.MapPanel;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

public class HeroSimulatorStage extends Stage {
    BorderPane pane1, pane2;
    Scene scene;
    ToolBar toolbar;
    MenuBar menu;
    Menu editor, karte, held, simulation, beispiele, speicherMenue, ladeMenue, expMenue, tutor, sprache;
    MenuItem neu, oeffnen, komp, drucken, m_drucken, beenden, saveXML, saveSerial, ladeXML, ladeSerial, expPNG, expGIF, groesse, bspSpeichern, bspLaden, tutbutton_1, tutbutton_2;
    ToggleGroup platzierungsGruppe;
    ToggleGroup t_platzierungsGruppe;
    RadioMenuItem platz_held, platz_muenze, platz_mauer, platz_wasser, del_kachel, sprache1, sprache2;
    MenuItem muenzen, linksUm, rechtsUm, laufe, schwimm, karten_groesse, start, pause, stop;
    SplitPane spane1;
    TextArea editor_area;
    Pane simulation_area;
    Label label;
    Button changeMapSize, t_neu, t_oeffnen, t_speichern, t_komp, t_linksUm, t_rechtsUm, t_laufe, t_schwimm, t_nimm;
    CoinButton t_muenze;
    Button t_play;
    Button t_pause;
    Button t_stop;
    ToggleButton t_platz_held, t_platz_muenze, t_platz_mauer, t_platz_wasser, t_del_kachel;
    Slider t_slider;
    MapPanel mapPanel;
    Map map;
    MapController mapController;
    MouseController mouseController;
    ProgramController programController;
    TutorController tutorController;
    File dir, m_dir;
    SimulationManager sim;
    DBController dbc;
    PropLoader propLoader;
    LanguageController lang;
    SoundController sound;
    CheckBox soundEnabled;


    public HeroSimulatorStage(Map m, Program program) {
        this.propLoader = new PropLoader();
        this.lang = new LanguageController(this);
        this.map = m;
        this.mapController = new MapController(this.map, this.mapPanel, this);
        this.programController = new ProgramController(this, this.map);
        this.programController.setCurrent(program);
        this.setTitle(getS("app_title"));

        createDirectory();
        this.pane1 = new BorderPane();
        this.pane2 = new BorderPane();
        this.pane1.setCenter(this.pane2);
        this.scene = new Scene(this.pane1, 1200, 600);
        this.sound = new SoundController(this);

        //GUI
        createMenuBar();
        createToolBar();
        initToggleBarEvent();

        ///TextArea und Simulationsbereich
        this.spane1 = new SplitPane();
        this.pane2.setCenter(this.spane1);
        createEditorArea();
        createSimulationArea();

        //Label & Controller
        createLabel();

        this.initProgramController();
        this.initMapController();
        this.mouseController = new MouseController(this.map, this.mapPanel, this);
        this.mouseController.initEvents();
        this.dbc = new DBController(this.map, this.mapPanel, this);
        this.initDatabaseController();
        this.initTutorController();

        this.sim = new SimulationManager(this);
        this.initSimulationManager();

        this.tutorController = new TutorController(this);


        this.updateLanguage();

        //stage stuff
        this.getIcons().add(new Image("/character/Hero_S_32.png"));
        this.setTitle(getS("app_title") + ": " + this.programController.getProgram().getName());
        this.setScene(this.scene);
        this.setOnCloseRequest(e -> {
            this.close();
            Platform.exit();
            System.exit(1);
        });
        this.show();
    }

    private void initTutorController() {
        if (this.propLoader.getRole().equals("student")) {
            tutbutton_1.setOnAction(e -> this.tutorController.studentSendRequest());
            tutbutton_2.setOnAction(e -> this.tutorController.studentGetAnswer());
        } else if (this.propLoader.getRole().equals("tutor")) {
            tutbutton_1.setOnAction(e -> this.tutorController.tutorGetRequest());
            tutbutton_2.setOnAction(e -> this.tutorController.tutorSendAnswer());
        }
    }

    private void initDatabaseController() {
        this.setOnCloseRequest(e -> this.dbc.closeConnection());
        this.bspSpeichern.setOnAction(new SaveExampleEventHandler(this.dbc));
        this.bspLaden.setOnAction(new LoadExampleEventHandler(this.dbc));
    }

    private void initSimulationManager() {
        this.t_play.setOnAction(e -> this.sim.run());
        this.t_pause.setOnAction(e -> this.sim.pause());
        this.t_stop.setOnAction(e -> this.sim.stop());
        this.start.setOnAction(e -> this.sim.run());
        this.pause.setOnAction(e -> this.sim.pause());
        this.stop.setOnAction(e -> this.sim.stop());
        this.t_slider.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                sim.setCurrentSpeed(t_slider.getValue() * 50);
            }
        });
        this.editor_area.textProperty().addListener(c -> {
            if (this.sim.getState() == SimulationState.running) this.sim.stop();
        });
    }

    private void initProgramController() {
        this.neu.setOnAction(new NewFileEventHandler(this.programController));
        this.t_neu.setOnAction(new NewFileEventHandler(this.programController));
        this.t_oeffnen.setOnAction(new OpenFileEventHandler(this.programController));
        this.oeffnen.setOnAction(new OpenFileEventHandler(this.programController));
        this.speicherMenue.setOnAction(e -> this.programController.autoSaveProgram());
        this.t_speichern.setOnAction(e -> this.programController.autoSaveProgram());
        this.setOnCloseRequest(e -> this.programController.deleteCurrent());
        this.editor_area.setText(this.programController.getProgram().getCode());
        this.komp.setOnAction(e -> this.programController.compileCurrent(true));
        this.t_komp.setOnAction(e -> this.programController.compileCurrent(true));
        this.programController.compileCurrent(false);
    }

    private void initMapController() {
        this.laufe.setOnAction(e -> this.mapController.walk());
        //MenuEvents
        this.schwimm.setOnAction(e -> this.mapController.swim());
        this.linksUm.setOnAction(e -> this.mapController.turnLeft());
        this.rechtsUm.setOnAction(e -> this.mapController.turnRight());
        //ToolbarEvents
        this.t_laufe.setOnAction(e -> this.mapController.walk());
        this.t_schwimm.setOnAction(e -> this.mapController.swim());
        this.t_nimm.setOnAction(e -> this.mapController.takeCoin());
        this.t_linksUm.setOnAction(e -> this.mapController.turnLeft());
        this.t_rechtsUm.setOnAction(e -> this.mapController.turnRight());

        this.changeMapSize.setOnAction(new ResizeMapEventHandler(this));
        this.groesse.setOnAction(new ResizeMapEventHandler(this));
        this.t_muenze.setOnAction(new ChangeCoinsEventHandler(this));
        this.muenzen.setOnAction(new ChangeCoinsEventHandler(this));
        //Serialization
        this.saveSerial.setOnAction(e -> this.mapController.serialize());
        this.ladeSerial.setOnAction(e -> this.mapController.deserialize());
        //XML
        this.saveXML.setOnAction(e -> this.mapController.saveXML());
        this.ladeXML.setOnAction(e -> this.mapController.loadXML());
    }

    private void createDirectory() {
        this.dir = new File("programs");
        if (!this.dir.exists()) {
            this.dir.mkdir();
        }
        this.m_dir = new File("maps");
        if (!this.m_dir.exists()) {
            this.m_dir.mkdir();
        }
    }

    private void createLabel() {
        this.label = new Label("Welcome!");
        this.pane2.setBottom(label);
    }

    private void createSimulationArea() {
        ScrollPane sc = new ScrollPane();
        sc.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
        sc.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        mapPanel = new MapPanel(this.mapController, sc);
        sc.setContent(mapPanel);
        spane1.getItems().add(sc);
    }

    private void createEditorArea() {
        editor_area = new TextArea();
        editor_area.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
        spane1.getItems().add(editor_area);
    }

    private void createToolBar() {
        toolbar = new ToolBar();
        t_neu = new Button(null, loadImage("/icons/New24.gif"));
        t_oeffnen = new Button(null, loadImage("/icons/Open24.gif"));
        t_speichern = new Button(null, loadImage("/icons/Save24.gif"));
        t_komp = new Button(null, loadImage("/icons/Compile24.gif"));
        changeMapSize = new Button(null, loadImage("/icons/Terrain24.gif"));
        t_platzierungsGruppe = new ToggleGroup();
        t_platz_held = new ToggleButton(null, loadImage("/icons/Hero_Icon_24.png"));
        t_platz_held.setToggleGroup(t_platzierungsGruppe);
        t_platz_held.setId("platz_held");
        t_platz_muenze = new ToggleButton(null, loadImage("/icons/Coin24.png"));
        t_platz_muenze.setToggleGroup(t_platzierungsGruppe);
        t_platz_muenze.setId("platz_muenze");
        t_platz_mauer = new ToggleButton(null, loadImage("/icons/wall_24.png"));
        t_platz_mauer.setToggleGroup(t_platzierungsGruppe);
        t_platz_mauer.setId("platz_mauer");
        t_platz_wasser = new ToggleButton(null, loadImage("/icons/water_24.png"));
        t_platz_wasser.setToggleGroup(t_platzierungsGruppe);
        t_platz_wasser.setId("platz_wasser");
        t_del_kachel = new ToggleButton(null, loadImage("/icons/del_24.png"));
        t_del_kachel.setToggleGroup(t_platzierungsGruppe);
        t_del_kachel.setId("del_kachel");

        t_muenze = new CoinButton(loadImage("/icons/coins_24.png"), this);
        t_linksUm = new Button(null, loadImage("/icons/leftturn_24.png"));
        t_rechtsUm = new Button(null, loadImage("/icons/rightturn_24.png"));
        t_laufe = new Button(null, loadImage("/icons/walk_24.png"));
        t_schwimm = new Button(null, loadImage("/icons/swim_24.png"));
        t_nimm = new Button(null, loadImage("/icons/takecoin_24.png"));

        t_play = new Button(null, loadImage("/icons/Play24.gif"));
        t_pause = new Button(null, loadImage("/icons/Pause24.gif"));
        t_stop = new Button(null, loadImage("/icons/Stop24.gif"));

        t_slider = new Slider(0, 10, 5);

        soundEnabled = new CheckBox("Audio");
        soundEnabled.setSelected(this.propLoader.getSound());
        soundEnabled.setOnAction(e -> {
            if (soundEnabled.isSelected() && !this.propLoader.getSound()) {
                this.sound.setEnabled(true);
                this.propLoader.setSound("true");
            } else if (!soundEnabled.isSelected() && this.propLoader.getSound()) {
                this.propLoader.setSound("false");
                this.sound.setEnabled(false);

            }
            soundEnabled.setSelected(this.propLoader.getSound());
        });

        toolbar.getItems().addAll(t_neu, t_oeffnen, new Separator(), t_speichern, t_komp, new Separator(), changeMapSize, t_platz_held, t_platz_muenze, t_platz_mauer, t_platz_wasser, t_del_kachel, new Separator(), t_muenze, t_linksUm, t_rechtsUm, t_laufe, t_schwimm, t_nimm, new Separator(), t_play, t_pause, t_stop, new Separator(), t_slider, new Separator(), soundEnabled);

        pane2.setTop(toolbar);
    }

    public void updateLanguage() {
        // app title
        this.setTitle(getS("app_title") + ": " + this.programController.getProgram().getName());
        // editor menu
        editor.setText(getS("editor_menu"));
        neu.setText(getS("e_neu"));
        oeffnen.setText(getS("e_oeffnen"));
        komp.setText(getS("e_komp"));
        drucken.setText(getS("e_drucken"));
        m_drucken.setText(getS("e_drucken"));
        beenden.setText(getS("e_beenden"));
        // map menu
        karte.setText(getS("karte_menu"));
        speicherMenue.setText(getS("k_speichern"));
        saveXML.setText(getS("k_saveXML"));
        saveSerial.setText(getS("k_saveSerial"));
        ladeMenue.setText(getS("k_ladeMenu"));
        ladeXML.setText(getS("k_ladeXML"));
        ladeSerial.setText(getS("k_ladeSerial"));
        expMenue.setText(getS("k_expMenu"));
        expPNG.setText(getS("k_png"));
        expGIF.setText(getS("k_gif"));
        groesse.setText(getS("k_groesse"));
        drucken.setText(getS("k_drucken"));
        platz_held.setText(getS("k_held"));
        platz_muenze.setText(getS("k_muenze"));
        platz_mauer.setText(getS("k_mauer"));
        platz_wasser.setText(getS("k_wasser"));
        del_kachel.setText(getS("k_del"));
        // hero menu
        held.setText(getS("held_menu"));
        muenzen.setText(getS("h_muenzen"));
        linksUm.setText(getS("h_linksUm"));
        rechtsUm.setText(getS("h_rechtsUm"));
        laufe.setText(getS("h_laufe"));
        schwimm.setText(getS("h_schwimm"));
        // simulation menu
        simulation.setText(getS("simulation_menu"));
        start.setText(getS("s_start"));
        pause.setText(getS("s_pause"));
        stop.setText(getS("s_stop"));
        // example menu
        beispiele.setText(getS("beispiele_menu"));
        bspSpeichern.setText(getS("b_speichern"));
        bspLaden.setText(getS("b_laden"));
        // tutor menu
        tutor.setText(getS("tutor_menu"));
        // lang menu
        sprache.setText(getS("sprache_menu"));
        sprache1.setText(getS("sp_eng"));
        sprache2.setText(getS("sp_deu"));

        // Tooltips
        t_neu.setTooltip(new Tooltip(getS("t_neu")));
        t_oeffnen.setTooltip(new Tooltip(getS("t_oeffnen")));
        t_speichern.setTooltip(new Tooltip(getS("t_speichern")));
        t_komp.setTooltip(new Tooltip(getS("t_komp")));
        changeMapSize.setTooltip(new Tooltip(getS("t_mapSize")));
        t_platz_held.setTooltip(new Tooltip(getS("t_held")));
        t_platz_muenze.setTooltip(new Tooltip(getS("t_muenze")));
        t_platz_mauer.setTooltip(new Tooltip(getS("t_mauer")));
        t_platz_wasser.setTooltip(new Tooltip(getS("t_wasser")));
        t_del_kachel.setTooltip(new Tooltip(getS("t_del")));
        t_muenze.setTooltip(new Tooltip(getS("t_muenzen")));
        t_linksUm.setTooltip(new Tooltip(getS("t_linksUm")));
        t_rechtsUm.setTooltip(new Tooltip(getS("t_rechtsUm")));
        t_laufe.setTooltip(new Tooltip(getS("t_laufe")));
        t_schwimm.setTooltip(new Tooltip(getS("t_schwimm")));
        t_nimm.setTooltip(new Tooltip(getS("t_nimm")));
        t_play.setTooltip(new Tooltip(getS("t_play")));
        t_pause.setTooltip(new Tooltip(getS("t_pause")));
        t_stop.setTooltip(new Tooltip(getS("t_stop")));
        this.t_muenze.update();

        if (this.propLoader.getRole().equals("student")) {
            tutbutton_1.setText(getS("tut1"));
            tutbutton_2.setText(getS("tut2"));
        } else if (this.propLoader.getRole().equals("tutor")) {
            tutbutton_1.setText(getS("tut3"));
            tutbutton_2.setText(getS("tut4"));
        }
    }

    private String getS(String s) {
        return this.lang.getRes().getString(s);
    }

    private void createMenuBar() {
        // menu bar
        menu = new MenuBar();
        menu.setBackground(new Background(new BackgroundFill(Color.BISQUE, CornerRadii.EMPTY, Insets.EMPTY)));

        // menu
        editor = new Menu();
        neu = new MenuItem();
        neu.setAccelerator(KeyCombination.keyCombination("Ctrl+N"));
        neu.setGraphic(loadImage("/icons/New16.gif"));
        oeffnen = new MenuItem();
        oeffnen.setAccelerator(KeyCombination.keyCombination("Ctrl+O"));
        oeffnen.setGraphic(loadImage("/icons/Open16.gif"));
        komp = new MenuItem();
        komp.setAccelerator(KeyCombination.keyCombination("Ctrl+K"));
        drucken = new MenuItem();
        drucken.setAccelerator(KeyCombination.keyCombination("Ctrl+P"));
        drucken.setGraphic(loadImage("/icons/Print16.gif"));
        drucken.setOnAction(event -> {
            TextArea printArea = editor_area;
            printArea.setWrapText(true);
            printArea.getChildrenUnmodifiable().forEach(node -> node.setStyle("-fx-background-color: transparent"));
            printArea.setStyle("-fx-background-color: transparent");
            PrinterJob printerJob = PrinterJob.createPrinterJob();
            if (printerJob != null && printerJob.showPrintDialog(null)) {
                if (printerJob.printPage(printArea)) {
                    printerJob.endJob();
                    // done printing
                }  // failed to print
            }  // failed to get printer job or failed to show print dialog
        });

        beenden = new MenuItem();
        beenden.setAccelerator(KeyCombination.keyCombination("Ctrl+Q"));
        beenden.setOnAction(e -> {
            Platform.exit();
            System.exit(0);
        });

        editor.getItems().addAll(neu, oeffnen, new SeparatorMenuItem(), komp, drucken, new SeparatorMenuItem(), beenden);

        //saveXML, saveJAXB, saveSerial, ladeXML, ladeJAXB, ladeSerial, expPNG, expGIF,
        karte = new Menu();

        speicherMenue = new Menu();
        saveXML = new MenuItem();
        saveSerial = new MenuItem();

        speicherMenue.getItems().addAll(saveXML, saveSerial);

        ladeMenue = new Menu();
        ladeXML = new MenuItem();
        ladeSerial = new MenuItem();
        ladeMenue.getItems().addAll(ladeXML, ladeSerial);

        expMenue = new Menu();

        class saveMapHandler implements EventHandler<ActionEvent> {
            final String format;
            final Stage s;

            public saveMapHandler(String format, Stage s) {
                this.format = format;
                this.s = s;
            }

            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();

                //Set extension filter
                FileChooser.ExtensionFilter extFilter =
                        new FileChooser.ExtensionFilter((format.equals("png")) ? "png files (*.png)" : "gif files (*.gif)", (format.equals("png")) ? "*.png" : "*.gif");
                fileChooser.getExtensionFilters().add(extFilter);

                //Show save file dialog
                File file = fileChooser.showSaveDialog(s);

                if (file != null) {
                    try {
                        WritableImage writableImage = new WritableImage((int) mapPanel.getCanvas().getWidth(), (int) mapPanel.getCanvas().getHeight());
                        mapPanel.getCanvas().snapshot(null, writableImage);
                        RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
                        ImageIO.write(renderedImage, (format.equals("png")) ? "png" : "gif", file);
                    } catch (IOException ex) {
                        // Logger.getLogger(JavaFX_DrawOnCanvas.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }

        expPNG = new MenuItem();
        expPNG.setOnAction(new saveMapHandler("png", this));

        expGIF = new MenuItem();
        expGIF.setOnAction(new saveMapHandler("gif", this));
        expMenue.getItems().addAll(expPNG, expGIF);

        groesse = new MenuItem();
        m_drucken = new MenuItem();
        m_drucken.setOnAction(event -> {
            Canvas printArea = mapPanel.getCanvas();
            printArea.setStyle("-fx-background-color: transparent");
            PrinterJob printerJob = PrinterJob.createPrinterJob();
            if (printerJob != null && printerJob.showPrintDialog(null)) {
                if (printerJob.printPage(printArea)) {
                    printerJob.endJob();
                    // done printing
                }  // failed to print
            }  // failed to get printer job or failed to show print dialog
        });

        platzierungsGruppe = new ToggleGroup();
        platz_held = new RadioMenuItem();
        platz_held.setToggleGroup(platzierungsGruppe);
        platz_muenze = new RadioMenuItem();
        platz_muenze.setToggleGroup(platzierungsGruppe);
        platz_mauer = new RadioMenuItem();
        platz_mauer.setToggleGroup(platzierungsGruppe);
        platz_wasser = new RadioMenuItem();
        platz_wasser.setToggleGroup(platzierungsGruppe);
        del_kachel = new RadioMenuItem();
        del_kachel.setToggleGroup(platzierungsGruppe);

        karte.getItems().addAll(speicherMenue, ladeMenue, expMenue, m_drucken, groesse, new SeparatorMenuItem(), platz_held, platz_muenze, platz_mauer, platz_wasser, del_kachel);

        held = new Menu();
        muenzen = new MenuItem();
        linksUm = new MenuItem();
        linksUm.setAccelerator(KeyCombination.keyCombination("Ctrl+Shift+L"));
        rechtsUm = new MenuItem();
        rechtsUm.setAccelerator(KeyCombination.keyCombination("Ctrl+Shift+R"));
        laufe = new MenuItem();
        laufe.setAccelerator(KeyCombination.keyCombination("Ctrl+Shift+W"));
        schwimm = new MenuItem();
        schwimm.setAccelerator(KeyCombination.keyCombination("Ctrl+Shift+S"));

        held.getItems().addAll(muenzen, new SeparatorMenuItem(), linksUm, rechtsUm, laufe, schwimm);

        simulation = new Menu();
        start = new MenuItem();
        start.setAccelerator(KeyCombination.keyCombination("Ctrl+F11"));
        start.setGraphic(loadImage("/icons/Play16.gif"));
        pause = new MenuItem();
        pause.setGraphic(loadImage("/icons/Pause16.gif"));
        stop = new MenuItem();
        stop.setAccelerator(KeyCombination.keyCombination("Ctrl+F12"));
        stop.setGraphic(loadImage("/icons/Stop16.gif"));

        simulation.getItems().addAll(start, pause, stop);

        beispiele = new Menu();
        bspSpeichern = new MenuItem();
        bspLaden = new MenuItem();
        beispiele.getItems().addAll(bspSpeichern, bspLaden);

        tutor = new Menu();
        tutbutton_1 = new MenuItem();
        tutbutton_2 = new MenuItem();

        tutor.getItems().addAll(tutbutton_1, tutbutton_2);

        sprache = new Menu();
        ToggleGroup langGroup = new ToggleGroup();
        sprache1 = new RadioMenuItem();
        sprache1.setToggleGroup(langGroup);
        sprache2 = new RadioMenuItem();
        sprache2.setToggleGroup(langGroup);
        sprache1.setOnAction(e -> this.lang.setLanguage("en", true));
        sprache2.setOnAction(e -> this.lang.setLanguage("de", true));

        sprache.getItems().addAll(sprache1, sprache2);

        switch (this.propLoader.getLanguage()) {
            case ("de"):
                sprache1.setSelected(false);
                sprache2.setSelected(true);
                break;
            case ("en"):
            default:
                sprache1.setSelected(true);
                sprache2.setSelected(false);
                break;
        }

        menu.getMenus().addAll(editor, karte, held, simulation, beispiele, tutor, sprache);

        pane1.setTop(menu);
    }

    private void initToggleBarEvent() {
        platzierungsGruppe.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (newValue == null) {
                    if (t_platzierungsGruppe.getSelectedToggle() != null)
                        t_platzierungsGruppe.selectToggle(null);
                    return;
                }

                if (newValue == platz_held) {
                    if (t_platzierungsGruppe.getSelectedToggle() != t_platz_held)
                        t_platzierungsGruppe.selectToggle(t_platz_held);
                } else if (newValue == platz_muenze) {
                    if (t_platzierungsGruppe.getSelectedToggle() != t_platz_muenze)
                        t_platzierungsGruppe.selectToggle(t_platz_muenze);
                } else if (newValue == platz_mauer) {
                    if (t_platzierungsGruppe.getSelectedToggle() != t_platz_mauer)
                        t_platzierungsGruppe.selectToggle(t_platz_mauer);
                } else if (newValue == platz_wasser) {
                    if (t_platzierungsGruppe.getSelectedToggle() != t_platz_wasser)
                        t_platzierungsGruppe.selectToggle(t_platz_wasser);
                } else if (newValue == del_kachel) {
                    if (t_platzierungsGruppe.getSelectedToggle() != t_del_kachel)
                        t_platzierungsGruppe.selectToggle(t_del_kachel);
                }
            }
        });

        t_platzierungsGruppe.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (newValue == null) {
                    if (platzierungsGruppe.getSelectedToggle() != null)
                        platzierungsGruppe.selectToggle(null);
                    return;
                }

                if (newValue == t_platz_held) {
                    if (platzierungsGruppe.getSelectedToggle() != platz_held)
                        platzierungsGruppe.selectToggle(platz_held);
                } else if (newValue == t_platz_muenze) {
                    if (platzierungsGruppe.getSelectedToggle() != platz_muenze)
                        platzierungsGruppe.selectToggle(platz_muenze);
                } else if (newValue == platz_mauer) {
                    if (platzierungsGruppe.getSelectedToggle() != platz_mauer)
                        platzierungsGruppe.selectToggle(platz_mauer);
                } else if (newValue == t_platz_wasser) {
                    if (platzierungsGruppe.getSelectedToggle() != platz_wasser)
                        platzierungsGruppe.selectToggle(platz_wasser);
                } else if (newValue == del_kachel) {
                    if (platzierungsGruppe.getSelectedToggle() != del_kachel)
                        platzierungsGruppe.selectToggle(del_kachel);
                }
            }
        });
    }

    public TextArea getEditor() {
        return this.editor_area;
    }

    public Button getT_play() {
        return t_play;
    }

    public Button getT_pause() {
        return t_pause;
    }

    public Button getT_stop() {
        return t_stop;
    }

    public MenuItem getStart() {
        return start;
    }

    public MenuItem getPause() {
        return pause;
    }

    public MenuItem getStop() {
        return stop;
    }

    public MenuItem getTutItem1() {
        return this.tutbutton_1;
    }

    public MenuItem getTutItem2() {
        return this.tutbutton_2;
    }

    public MapController getMapController() {
        return this.mapController;
    }

    public File getDir() {
        return dir;
    }

    public File getMapDir() {
        return this.m_dir;
    }

    public String getAPPNAME() {
        return getS("app_title");
    }

    public Label getLabel() {
        return this.label;
    }

    public ToggleGroup getPlatzierungsGruppe() {
        return platzierungsGruppe;
    }

    public ToggleGroup getT_platzierungsGruppe() {
        return t_platzierungsGruppe;
    }

    public Button getCoinButton() {
        return this.t_muenze;
    }

    public SimulationManager getSimManager() {
        return this.sim;
    }

    public PropLoader getPropLoader() {
        return this.propLoader;
    }

    public LanguageController getLanguageController() {
        return this.lang;
    }

    public SoundController getSoundController() {
        return this.sound;
    }

    public void setInteractiveMenuItemsDisabled(boolean value) {
        this.t_laufe.setDisable(value);
        this.t_schwimm.setDisable(value);
        this.t_linksUm.setDisable(value);
        this.t_rechtsUm.setDisable(value);
        this.t_nimm.setDisable(value);
        this.laufe.setDisable(value);
        this.schwimm.setDisable(value);
        this.linksUm.setDisable(value);
        this.rechtsUm.setDisable(value);
    }

    public void setDisabledToggleGroups(boolean value) {
        for (Toggle t : this.t_platzierungsGruppe.getToggles()) {
            ToggleButton k = (ToggleButton) t;
            if (k.isDisable() != value) k.setDisable(value);
        }
        for (Toggle t : this.platzierungsGruppe.getToggles()) {
            RadioMenuItem k = (RadioMenuItem) t;
            if (k.isDisable() != value) k.setDisable(value);
        }
    }

    private ImageView loadImage(String path) {
        try {
            Image newImage = new Image(getClass().getResourceAsStream(path));
            return new ImageView(newImage);
        } catch (NullPointerException e) {
            System.err.println("Bild nicht gefunden! Pfad: " + path);
            return null;
        }
    }
}
