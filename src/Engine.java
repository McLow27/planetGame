package src;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.Color;
import src.gui.Handler;
import src.gui.Title;

/**
 * A game engine extending the Canvas class; runs a tick/render engine in a
 * separate thread with a constant TPS of 20 and varying peak FPS rendering the
 * game onto the canvas that is itself
 */
public class Engine extends Canvas implements Runnable {

    public static final int WIDTH = 1280, HEIGHT = WIDTH / 16 * 9;
    private Thread thread;
    private boolean running = false;

    /**
     * This class is merely used for the InputAdapters to send input to the
     * currently displayed GUI since Java does not have pointers (kinda).
     */
    class UI {
        /**
         * The current game state i.e. what is being displayed to the user
         */
        enum State {
            TITLE,
            LOBBY,
            NONE,
            GAME;
        }

        private GUI ui;
        private State state;

        /**
         * An empty constructor creating a new UI object with the game state being NONE
         * and no GUI with which to interact
         */
        public UI() {
            this.ui = null;
            this.state = State.NONE;
        }

        /**
         * A constructor for a new UI object with the GUI object and game state enum
         */
        public UI(GUI gui, State state) {
            this.ui = gui;
            this.state = state;
        }

        /**
         * Changes the game state and therefore what will be processed and displayed by
         * the engine
         * 
         * @param gui   the GUI to tick and render
         * @param state an enum constant identifying the current game state
         */
        public void setState(GUI gui, State state) {
            this.ui = gui;
            this.state = state;
        }

        /**
         * Returns the enum constant identifying the current game state
         * 
         * @return an enum constant representing the game state
         */
        public State getState() {
            return state;
        }

        /**
         * Returns the GUI object of the current game state
         * 
         * @return the GUI object to be ticked and rendered
         */
        public GUI getUI() {
            return ui;
        }
    }

    private UI state;

    /**
     * A new game engine object; a new instance of the game
     */
    public Engine() {
        state = new UI();
        setState(UI.State.TITLE);
        new Window(WIDTH, HEIGHT, "Working Title", this);
        this.addKeyListener(new KeyInput(state));
        this.addMouseListener(new MouseInput(state));
    }

    /**
     * Creates and starts a new thread for the game engine's tick/render loop
     */
    public synchronized void start() {
        thread = new Thread(this);
        thread.start();
        running = true;
    }

    /**
     * Stops the thread that runs the game engine's tick/render loop
     */
    public synchronized void stop() {
        try {
            thread.join();
            running = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * The core of the engine; the tick/render loop
     */
    public void run() {
        long lastTime = System.nanoTime();
        double tps = 20.0;
        double ns = 100000000 / tps;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while (delta >= 1) {
                tick();
                delta--;
            }
            if (running)
                render();
            frames++;

            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                System.out.println("FPS: " + frames);
                frames = 0;
            }
        }
    }

    /**
     * Sets the game state that defines what is ticked and rendered by the engine
     * 
     * @param state a constant from the local UI.State enum that represents the game
     *              state
     */
    public void setState(UI.State state) {
        switch (state) {
            case TITLE:
                this.state.setState(new Title(), state);
                break;
            case LOBBY:
                // Not yet implemented -> NullPointerException
                this.state.setState(null, state);
                break;
            case GAME:
                // Basically initialises a new game
                this.state.setState(new Handler(), state);
                break;
            default:
                this.state.setState(null, UI.State.NONE);
                break;
        }
    }

    /**
     * Returns the enum constant that represents the current game state
     * 
     * @return a constant from the local UI.State enum
     */
    public UI.State getState() {
        return state.getState();
    }

    /**
     * Executes the tick method of the currently displayed GUI object
     */
    private void tick() {
        state.getUI().tick();
    }

    /**
     * Creates new graphics for this canvas, renders the currently displayed GUI,
     * then displays the buffer strategy
     */
    public void render() {
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null) {
            this.createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        state.getUI().render(g);

        g.dispose();
        bs.show();
    }

    public static void main(String[] args) {
        new Engine();
    }

}
