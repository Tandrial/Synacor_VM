package de.mkrane.synacor.vm;

import java.util.ArrayList;
import java.util.List;

public class VaultSolver {

  enum Direction {
    north, south, west, east
  }

  private static final int[][] vault_layout = {{'*', 8, '-', 1},
      {4, '*', 11, '*'},
      {'+', 4, '-', 18},
      {22, '-', 9, '*'}};

  private static final int GOAL_VALUE = 30;
  private static final int MAX_MOVES = 12;

  private class Path implements Cloneable {

    private List<Direction> moves = new ArrayList<>();
    private java.awt.Point pos = new java.awt.Point(0, 3);
    private int op = 0;

    private int orbValue = vault_layout[pos.y][pos.x];

    int getSize() {
      return moves.size();
    }

    boolean atVault() {
      return pos.x == 3 && pos.y == 0;
    }

    int getValue() {
      return orbValue;
    }

    void printPath() {
      moves.forEach(System.out::println);
    }

    boolean canMove(Direction direction) {
      switch (direction) {
        case north:
          return pos.y > 0;
        case south:
          return pos.y < 3 && pos.x != 0 && pos.y != 2;
        case west:
          return pos.x > 0 && pos.x != 1 && pos.y != 3;
        case east:
          return pos.x < 3;
      }
      return false;
    }

    void makeMove(Direction direction) {
      switch (direction) {
        case north:
          pos.y--;
          break;
        case south:
          pos.y++;
          break;
        case west:
          pos.x--;
          break;
        case east:
          pos.x++;
          break;
      }
      moves.add(direction);
      applyRoomValue(vault_layout[pos.y][pos.x]);
    }

    private void applyRoomValue(int roomValue) {
      switch (op) {
        case '+':
          orbValue += roomValue;
          break;
        case '-':
          orbValue -= roomValue;
          break;
        case '*':
          orbValue *= roomValue;
          break;
        default:
          op = roomValue;
          return;
      }
      op = 0;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
      Path clone = (Path) super.clone();
      clone.pos = this.pos.getLocation();
      clone.op = this.op;
      clone.orbValue = this.orbValue;
      clone.moves = new ArrayList<>(this.moves);
      return clone;
    }
  }

  private static Path best = null;

  private Path findPath() throws CloneNotSupportedException {
    return findPath(new Path());
  }

  private static Path findPath(Path p) throws CloneNotSupportedException {
    if (p.getSize() > MAX_MOVES) {
      return null;
    } else if (p.atVault()) {
      if (p.getValue() == GOAL_VALUE)
        best = p;
      else
        return null;
    }

    for (Direction direction : Direction.values()) {
      Path neu = (Path) p.clone();
      if (neu.canMove(direction)) {
        neu.makeMove(direction);
        findPath(neu);
      }
    }
    return best;
  }

  public static void main(String[] args) {
    try {
      long start_calc = System.currentTimeMillis();
      Path p = new VaultSolver().findPath();
      System.out.println(String.format(
          "Solution to get to the Vault in less than 12 moves with an orb-Value of 30 (took %.2f s)\n",
          (System.currentTimeMillis() - start_calc) / 1000.0));
      p.printPath();
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }
  }
}