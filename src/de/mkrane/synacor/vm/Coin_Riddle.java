package de.mkrane.synacor.vm;

class Coin_Riddle {

  private static boolean solves(int a, int b, int c, int d, int e) {
    int result = a;
    result += b * c * c;
    result += d * d * d;
    result -= e;

    return result == 399;
  }

  public static void main(String[] args) {
    String[] names = {"red", "corroded", "shiny", "concave", "blue"};
    int[] values = {2, 3, 5, 7, 9};
    System.out.println("Solution for the coin riddle: _ + _ * _^2 + _^3 - _ = 399");
    for (int a = 0; a < values.length; a++)
      for (int b = 0; b < values.length; b++)
        for (int c = 0; c < values.length; c++)
          for (int d = 0; d < values.length; d++)
            for (int e = 0; e < values.length; e++)
              if (solves(values[a], values[b], values[c], values[d], values[e]))
                System.out.println(String.format(
                    "use %s coin\nuse %s coin\nuse %s coin\nuse %s coin\nuse %s coin%n", names[a],
                    names[b], names[c], names[d], names[e]));
  }
}
