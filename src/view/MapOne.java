package view;

/**
 * This program is mean to demo key listeners
 * 
 * A new rectangle will be filled for each arrow click (unless it
 * would cause an exit from the board. 
 * 
 * Pressing the lower case 'a' key begins an animation
 */
import game.Game;
import game.Globals;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

public class MapOne extends JPanel implements Observer {

  public final static int OFFSET = 15; // move animation to right from upper left

  private BufferedImage sprites;
  private Rectangle2D.Double[][] recs;
  private Game game;

  public MapOne(Game game) {
    this.game = game;
    ListenToKeys listener = new ListenToKeys();
    this.addKeyListener(listener);
    try {
      sprites = ImageIO.read(new File("images/megaman7.gif"));
    } catch (IOException e) {
      e.printStackTrace();
    }
    setBackground(Color.GREEN);
    animatingFight = false;
    this.setSize(Globals.WIDTH, Globals.HEIGHT);
    this.setFocusable(true);

    recs = new Rectangle2D.Double[Globals.ROWS][Globals.COLUMNS];
    for (int r = 0; r < Globals.ROWS; r++) {
      for (int c = 0; c < Globals.COLUMNS; c++) {
        recs[r][c] = new Rectangle2D.Double(c * Globals.TILE_SIZE, r
            * Globals.TILE_SIZE, Globals.TILE_SIZE, Globals.TILE_SIZE);
      }
    }

    // Arbitrary picked as starting location
    selectedRow = 2;
    selectedCol = 5;

    setUpFightAnimationData();
  }

  private void setUpFightAnimationData() {
    fightSubimages = new ArrayList<BufferedImage>();
    fightSubimages.add(sprites.getSubimage(505, 0, 50, 50));
    fightSubimages.add(sprites.getSubimage(505, 50, 50, 50));
    fightSubimages.add(sprites.getSubimage(505, 104, 50, 50));
    fightSubimages.add(sprites.getSubimage(505, 155, 50, 50));
    // get two from the last column of sprite sheet
    fightSubimages.add(sprites.getSubimage(505, 0, 50, 50));
    fightSubimages.add(sprites.getSubimage(505, 50, 50, 50));
    standingImage = sprites.getSubimage(110, 47, 50, 50);
  }

  private int selectedRow, selectedCol;
  private List<BufferedImage> fightSubimages;
  private BufferedImage standingImage;

  @Override
  public void update(Observable o, Object arg) {
    game = (Game) o;
    repaint();
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;

    for (int r = 0; r < Globals.ROWS; r++) {
      for (int c = 0; c < Globals.COLUMNS; c++) {
        g2.draw(recs[r][c]);
      }
    }

    drawAttackPossibilities(g2);

    if (animatingFight) {
      if (tic == fightSubimages.size()) {
        // We're done with the battle scene
        g2.drawImage(standingImage, upperLeftX, upperLeftY + 5, null);
        animatingFight = false;
      } else
        g2.drawImage(fightSubimages.get(tic), upperLeftX + 6, upperLeftY, null);
    } else
      g2.drawImage(standingImage, Globals.TILE_SIZE * selectedCol,
          Globals.TILE_SIZE * selectedRow, null);
  }

  private void drawAttackPossibilities(Graphics2D g2) {
    int r = selectedRow;
    int c = selectedCol;
    g2.setColor(Color.CYAN);
    if (r > 0)
      g2.fill(recs[r - 1][c]);
    if (r < Globals.ROWS - 1)
      g2.fill(recs[r + 1][c]);

    if (c < Globals.COLUMNS - 1)
      g2.fill(recs[r][c + 1]);

    if (c > 0)
      g2.fill(recs[r][c - 1]);

    g2.setColor(Color.BLACK);
  }

  int y;

  private class AnimationListener implements ActionListener {

    public void actionPerformed(ActionEvent arg0) {
      repaint();
      y += fightSubimages.get(tic).getHeight();
      tic++;
      if (tic >= fightSubimages.size()) {
        timer.stop();
      }
    }
  }

  int tic;
  Timer timer;
  private boolean animatingFight;
  public int upperLeftX, upperLeftY;
  public static final int ms_delay_in_timer = 75;

  private void animateAnAttack() {
    timer = new Timer(ms_delay_in_timer, new AnimationListener());
    tic = 0;
    upperLeftX = selectedCol * Globals.TILE_SIZE;
    upperLeftY = selectedRow * Globals.TILE_SIZE;
    repaint();
    timer.start();
    animatingFight = true;
  }

  private class ListenToKeys implements KeyListener {

    public void keyPressed(KeyEvent key) {
      if (animatingFight) // Don't move current location until the fight is over
        return;
      int keyCode = key.getKeyCode();

      if (keyCode == KeyEvent.VK_UP) {
        System.out.println("Up arrow");
        if (selectedRow > 0)
          selectedRow -= 1;
      }

      if (keyCode == KeyEvent.VK_DOWN) {
        System.out.println("Down arrow");
        if (selectedRow < Globals.ROWS - 1)
          selectedRow += 1;
      }

      if (keyCode == KeyEvent.VK_LEFT) {
        System.out.println("Left arrow");
        if (selectedCol > 0)
          selectedCol -= 1;
      }

      if (keyCode == KeyEvent.VK_RIGHT) {
        System.out.println("Right arrow");
        if (selectedCol < Globals.COLUMNS - 1)
          selectedCol += 1;
      }

      // redraw the whole panel with a new selected position
      repaint();
    }

    @Override
    public void keyReleased(KeyEvent arg0) {
      // TODO Auto-generated method stub
    }

    @Override
    public void keyTyped(KeyEvent key) {
      char ch = key.getKeyChar();
      // Only start a fight if not in a fight, ignore the key press
      if (!animatingFight && ch == 'a')
        animateAnAttack();
    }

  }

}