package de.mkrane.synacor.vm;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;

class Coin_Riddle {

  private static boolean solves(int a, int b, int c, int d, int e) {
    int result = a;
    result += b * c * c;
    result += d * d * d;
    result -= e;

    return result == 399;
  }

  public static void main(String[] args) {
    List<SimpleEntry<String, Integer>> coins = List.of(
        new SimpleEntry<>("red", 2),
        new SimpleEntry<>("corroded", 3),
        new SimpleEntry<>("shiny", 5),
        new SimpleEntry<>("concave", 7),
        new SimpleEntry<>("blue", 9));

    System.out.println("Solution for the coin riddle: _ + _ * _^2 + _^3 - _ = 399");
    for (SimpleEntry<String, Integer> a : coins)
      for (SimpleEntry<String, Integer> b : coins)
        for (SimpleEntry<String, Integer> c : coins)
          for (SimpleEntry<String, Integer> d : coins)
            for (SimpleEntry<String, Integer> e : coins)
              if (solves(a.getValue(), b.getValue(), c.getValue(), d.getValue(), e.getValue()))
                System.out.printf("use %s coin\nuse %s coin\nuse %s coin\nuse %s coin\nuse %s coin\n\n",
                    a.getKey(), b.getKey(), c.getKey(), d.getKey(), e.getKey());
  }
}
