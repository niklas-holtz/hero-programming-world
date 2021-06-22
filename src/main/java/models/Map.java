package models;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import util.Random2DArray;
import util.observer.ReverseObservable;

@SuppressWarnings("serial")
public class Map extends ReverseObservable implements Serializable {
    private int rows = 10;
    private int colls = 14;
    // 0 = No Tile, -1 = Swim-Tile, 1 = Wall, 2 = Coin
    private volatile int[][] tiles;
    private transient Hero held;
    private volatile int held_x, held_y;
    private volatile int held_max_inv = 10;
    private final transient IntegerProperty held_current_inv = new SimpleIntegerProperty(0);
    // 0 = N, 1 = E, 2 = S, 3 = W
    private int held_dir;

    //Constants
    private static final int WALL_TILE = 1;
    private static final int SWIM_TILE = -1;
    private static final int COIN_TILE = 2;
    private static final int NULL_TILE = 0;
    private static final int HERO_TILE = 3;
    private static final int NORTH = 0, EAST = 1, SOUTH = 2, WEST = 3;

    //Constants for random generation
    private static final int WATER_COUNT = 5;
    private static final int WALL_COUNT = 10;
    private static final int COIN_COUNT = 7;

    public Map() {
        this.held = new Hero(this);
        this.held_dir = SOUTH;
        this.next(WATER_COUNT, WALL_COUNT, COIN_COUNT);
    }

    public void next(int water, int wall, int coin) {
        synchronized (this) {
            Random2DArray rnd = new Random2DArray(this.rows, this.colls);
            this.tiles = rnd.generate(SWIM_TILE, WALL_TILE, COIN_TILE, HERO_TILE, water, wall, coin);
            this.held_x = rnd.getPlayerX();
            this.held_y = rnd.getPlayerY();
        }
        this.notifyObservers();
    }

    public synchronized Hero getHero() {
        return this.held;
    }

    public synchronized void setHero(Hero hero) {
        this.held = hero;
    }

    public int getTile(int posX, int posY) {
        if (posX >= this.colls || posY >= this.rows || posX < 0 || posY < 0)
            return WALL_TILE;

        return this.tiles[posY][posX];
    }

    public boolean setTile(int posX, int posY, int value) {
        synchronized (this) {
            if (posX >= this.colls || posY >= this.rows || posX < 0 || posY < 0)
                return false;

            if (value == HERO_TILE) {
                if (this.getTile(posX, posY) == NULL_TILE) {
                    this.held_x = posX;
                    this.held_y = posY;
                }
            } else {
                tiles[posY][posX] = value;
            }

        }
        this.notifyObservers();
        return true;
    }

    public void swim() throws MauerDaException, NoWaterException {
        int nextTile_x = getNext("x"), nextTile_y = getNext("y");
        synchronized (this) {
            if (nextTile_x >= this.colls || nextTile_y >= this.rows || nextTile_x < 0 || nextTile_y < 0)
                throw new MauerDaException();

            int tile = this.tiles[nextTile_y][nextTile_x];

            if (tile == WALL_TILE)
                throw new MauerDaException();

            if (tile != SWIM_TILE)
                throw new NoWaterException();

            this.held_x = nextTile_x;
            this.held_y = nextTile_y;
        }
        this.notifyObservers();
    }

    public void walk() throws MauerDaException, WaterException {
        int nextTile_x = getNext("x"), nextTile_y = getNext("y");
        synchronized (this) {
            if (nextTile_x >= this.colls || nextTile_y >= this.rows || nextTile_x < 0 || nextTile_y < 0)
                throw new MauerDaException();

            int tile = this.tiles[nextTile_y][nextTile_x];

            if (tile == SWIM_TILE)
                throw new WaterException();

            if (tile == WALL_TILE)
                throw new MauerDaException();

            this.held_x = nextTile_x;
            this.held_y = nextTile_y;
        }
        this.notifyObservers();
    }

    public void resize(int rows, int colls) {
        synchronized (this) {
            int[][] buffer = new int[rows][colls];
            for (int i = 0; i < rows; i++) {
                for (int k = 0; k < colls; k++) {
                    if (k >= this.colls || i >= this.rows)
                        buffer[i][k] = 0;
                    else
                        buffer[i][k] = this.tiles[i][k];
                }
            }
            this.rows = rows;
            this.colls = colls;
            if (this.held_x >= colls || this.held_y >= rows) {
                if (this.tiles[0][0] != 0)
                    setTile(0, 0, 0);

                this.held_x = 0;
                this.held_y = 0;
            }
            this.tiles = buffer;
        }
        this.notifyObservers();
    }

    public void moveHero(int x, int y) {
        synchronized (this) {
            this.held_x = x;
            this.held_y = y;
        }
        this.notifyObservers();
    }

    public void turnLeft() {
        synchronized (this) {
            if (this.held_dir > 0) {
                this.held_dir--;
            } else {
                this.held_dir = 3;
            }
        }
        this.notifyObservers();
    }

    public void turnRight() {
        synchronized (this) {
            if (this.held_dir < 3) {
                this.held_dir++;
            } else {
                this.held_dir = 0;
            }
        }
        this.notifyObservers();
    }

    public synchronized void giveCoins(int coins) {
        this.held_current_inv.setValue(this.held_current_inv.getValue() + coins);
    }

    public synchronized void setCoins(int coins) {
        this.held_current_inv.setValue(coins);
    }

    public void takeCoin() throws FullInventoryException, NoCoinException {
        synchronized (this) {
            if (getCoin()) {
                if (isInventoryFull())
                    throw new FullInventoryException();
                this.tiles[this.held_y][this.held_x] = 0;
                this.held_current_inv.setValue(this.held_current_inv.getValue() + 1);
            } else
                throw new NoCoinException();
        }
        this.notifyObservers();
    }

    public synchronized boolean heroIsOnPos(int posX, int posY) {
        return (this.held_x == posX && this.held_y == posY);
    }

    public synchronized boolean frontIsClear() {
        int nextTile_x = getNext("x"), nextTile_y = getNext("y");
        if (nextTile_x >= this.colls || nextTile_y >= this.rows || nextTile_x < 0 || nextTile_y < 0)
            return false;

        if (this.tiles[nextTile_y][nextTile_x] == WALL_TILE)
            return false;

        return true;
    }

    public synchronized boolean frontIsWater() {
        int nextTile_x = getNext("x"), nextTile_y = getNext("y");
        if (nextTile_x >= this.colls || nextTile_y >= this.rows || nextTile_x < 0 || nextTile_y < 0)
            return false;

        if (this.tiles[nextTile_y][nextTile_x] != SWIM_TILE)
            return false;

        return true;
    }

    public synchronized boolean isInventoryFull() {
        return (this.held_current_inv.get() >= this.held_max_inv);
    }

    public synchronized boolean isSwimming() {
        return (this.tiles[this.held_y][this.held_x] == SWIM_TILE);
    }

    public synchronized int getRows() {
        return this.rows;
    }

    public synchronized int getColls() {
        return this.colls;
    }

    private synchronized int getNext(String w) {
        int nextTile;
        if (w == "x" || w == "X") {
            nextTile = this.held_x;
            switch (this.held_dir) {
                case EAST:
                    nextTile++;
                    break;
                case WEST:
                    nextTile--;
                    break;
            }
        } else if (w == "y" || w == "Y") {
            nextTile = this.held_y;
            switch (this.held_dir) {
                case NORTH:
                    nextTile--;
                    break;
                case SOUTH:
                    nextTile++;
                    break;
            }
        } else
            nextTile = -1;

        return nextTile;
    }

    public synchronized int getInventorySize() {
        return this.held_max_inv;
    }

    public synchronized int[][] getMapArray() {
        return this.tiles;
    }

    public synchronized int getCurrentCoins() {
        return this.held_current_inv.get();
    }

    public synchronized IntegerProperty getCoinProperty() {
        return this.held_current_inv;
    }

    public synchronized boolean getCoin() {
        return (this.tiles[this.held_y][this.held_x] == COIN_TILE);
    }

    public synchronized String getDir() {
        switch (this.held_dir) {
            case NORTH:
                return "North";
            case EAST:
                return "East";
            case SOUTH:
                return "South";
            case WEST:
                return "West";
        }
        return "Err";
    }

    public void deserializeMap(Map m) {
        synchronized (this) {
            this.rows = m.rows;
            this.colls = m.colls;
            this.tiles = m.tiles;
            this.held_x = m.held_x;
            this.held_y = m.held_y;
            this.held_max_inv = m.held_max_inv;
            this.held_dir = m.held_dir;
        }
        this.notifyObservers();
    }

    public boolean loadXML(File file) {
        synchronized (this) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(true);
            try {
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(file);

                NodeList l = document.getElementsByTagName("map");
                this.rows = Integer.parseInt(((Element) l.item(0)).getAttribute("rows"));
                this.colls = Integer.parseInt(((Element) l.item(0)).getAttribute("colls"));
                this.held_x = Integer.parseInt(((Element) l.item(0)).getAttribute("held_x"));
                this.held_y = Integer.parseInt(((Element) l.item(0)).getAttribute("held_y"));
                this.held_dir = Integer.parseInt(((Element) l.item(0)).getAttribute("held_dir"));
                this.held_max_inv = Integer.parseInt(((Element) l.item(0)).getAttribute("max_inv"));
                l = document.getElementsByTagName("tile");
                int[][] buffer = new int[this.rows][this.colls];
                for (int i = 0; i < l.getLength(); i++) {
                    Element e = (Element) l.item(i);
                    buffer[Integer.parseInt(e.getAttribute("x"))][Integer.parseInt(e.getAttribute("y"))] = Integer.parseInt(e.getAttribute("value"));
                }
                this.tiles = buffer;
            } catch (ParserConfigurationException | SAXException | IOException e) {
            }
        }
        this.notifyObservers();

        return false;
    }

    public synchronized boolean saveXML(File file) {
        XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
        XMLStreamWriter writer;
        try {
            writer = xmlOutputFactory.createXMLStreamWriter(new FileOutputStream(file.getAbsolutePath()), "UTF-8");
            writer.writeStartDocument("UTF-8", "1.0");
            writer.writeCharacters("\n");
            writer.writeDTD("<!DOCTYPE map [\n"
                    + "<!ELEMENT map (tile+)>\n"
                    + "<!ATTLIST map "
                    + "rows CDATA #REQUIRED "
                    + "colls CDATA #REQUIRED "
                    + "held_x CDATA #REQUIRED "
                    + "held_y CDATA #REQUIRED "
                    + "held_dir CDATA #REQUIRED "
                    + "max_inv CDATA #REQUIRED>\n"
                    + "<!ELEMENT tile EMPTY>\n"
                    + "<!ATTLIST tile "
                    + "x CDATA #REQUIRED "
                    + "y CDATA #REQUIRED "
                    + "value CDATA #REQUIRED>\n"
                    + "]>\n"
            );
            writer.writeStartElement("map");
            writer.writeAttribute("rows", String.valueOf(this.rows));
            writer.writeAttribute("colls", String.valueOf(this.colls));
            writer.writeAttribute("held_x", String.valueOf(this.held_x));
            writer.writeAttribute("held_y", String.valueOf(this.held_y));
            writer.writeAttribute("held_dir", String.valueOf(this.held_dir));
            writer.writeAttribute("max_inv", String.valueOf(this.held_max_inv));
            writer.writeCharacters("\n");
            for (int i = 0; i < rows; i++) {
                for (int k = 0; k < colls; k++) {
                    writer.writeStartElement("tile");
                    writer.writeAttribute("x", String.valueOf(i));
                    writer.writeAttribute("y", String.valueOf(k));
                    writer.writeAttribute("value", String.valueOf(this.tiles[i][k]));
                    writer.writeEndElement(); // tile
                    writer.writeCharacters("\n");
                }
            }
            writer.writeEndDocument();
            writer.writeCharacters("\n");
            writer.close();
            return true;
        } catch (FileNotFoundException | XMLStreamException | FactoryConfigurationError e1) {
        }
        return false;
    }

}
