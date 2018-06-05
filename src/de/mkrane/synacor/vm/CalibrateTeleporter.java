package de.mkrane.synacor.vm;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

class CalibrateTeleporter {

  // [6027] jt r0 6035
  // [6030] add r0 r1 1
  // [6034] ret
  //
  // [6035] jt r1 6048
  // [6038] add r0 r0 32767
  // [6042] set r1 = r7
  // [6045] call @6027
  // [6047] ret
  //
  // [6048] push r0
  // [6050] add r1 r1 32767
  // [6054] call @6027
  // [6056] set r1 = r0
  // [6059] pop @r0
  // [6061] add r0 r0 32767
  // [6065] call @6027
  // [6067] ret

//	int[] stack = new int[1 << 16];
//	int sp = -1;
//
//	int r0, r1, r7;
//
//	void func_6027() {
//		if (r0 == 0) {
//			r0 = (r1 + 1) % 32768;
//			return;
//		}
//		if (r1 == 0) {
//			r0 = (r0 + 32767) % 32768; // r0--
//			r1 = r7;
//			func_6027();
//			return;
//		}
//		stack[++sp] = r0;
//		r1 = (r1 + 32767) % 32768; // r1--
//		func_6027();
//		r1 = r0;
//		r0 = stack[sp--];
//		r0 = (r0 + 32767) % 32768; // r1--
//		func_6027();
//	}
//
//	int validate(int r0, int r1) {
//		if (r0 == 0)
//			return (r1 + 1) % 32768;
//		if (r1 == 0)
//			return validate(r0 - 1, r7);
//		return validate(r0 - 1, validate(r0, r1 - 1));
//	}

  private static int validate(int r0, int r1, int r7) {
    int[][] table = new int[r0 + 1][0x8000];

    // P(0,r1)
    for (int i = 0; i < table[0].length; i++)
      table[0][i] = (i + 1) % 0x8000;

    for (int i = 1; i < table.length; i++) {
      // P(r0, 0)
      table[i][0] = table[(i - 1)][r7];
      // P(r0, r1)
      for (int j = 1; j < table[0].length; j++)
        table[i][j] = table[i - 1][table[i][j - 1]];
    }
    return table[r0][r1];
  }

  public static void main(String[] args) {
    System.out.println("Synacor VM Teleporter Calibration");
    int threadCount = 10;
    long start_calc = System.currentTimeMillis();
    ExecutorService exService = Executors.newFixedThreadPool(threadCount);

    for (AtomicInteger num = new AtomicInteger(); num.get() < threadCount; num.incrementAndGet()) {
      final int step = 0xF777 / threadCount + 1;
      exService.submit(new Callable<Void>() {
        final int start = (num.get() * step) + 1;
        final int stop = Math.min(((num.get() + 1) * step) + 1, 0xF777);

        @Override
        public Void call() {
          for (int r7 = start; r7 <= stop && !Thread.currentThread().isInterrupted(); r7++) {
            if (validate(4, 1, r7) == 6) {
              System.out.println(String.format("P(4,1,r7) = 6 for r7 = %d (took %.2f s)", r7,
                  (System.currentTimeMillis() - start_calc) / 1000.0));
              exService.shutdownNow();
            }
          }
          return null;
        }
      });
    }
    exService.shutdown();
  }
}
