package de.mkrane.synacor.vm;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Dissassembler {

  static void dissambleToFile(int[] ram, String file) throws IOException {
    Dissassembler diss = new Dissassembler();

    diss.ram = Arrays.copyOf(ram, 0x8000 + 8);
    diss.pos = diss.ram.length;
    Files.write(Paths.get(file), diss.dissassemble());
    System.out.println("done");
  }

  private List<String> out = null;
  private int[] ram = new int[1 << 16];
  private int pos;

  private void loadRam(File file) throws IOException {
    DataInputStream input = new DataInputStream(new FileInputStream(file));
    pos = 0;
    while (input.available() > 0)
      ram[pos++] = input.readUnsignedByte() + (input.readUnsignedByte() << 8);
    input.close();
    out = null;
  }

  private List<String> dissassemble() {
    if (out != null)
      return out;
    out = new ArrayList<>();
    for (int pc = 0; pc < pos; ) {
      String line = "";
      if (pc >= 6068) {
        if (ram[pc] >= 32 && ram[pc] < 127)
          line = (String.format("[%d] '%c' %d", pc, ram[pc], ram[pc]));
        else
          line = (String.format("[%d]  %d", pc, ram[pc]));
        pc++;
      } else {
        INSTRUCTION opCode = INSTRUCTION.fromInt(ram[pc]);
        switch (opCode) {
          case HALT: // halt: 0
            line = (String.format("[%d] halt", pc));
            pc++;
            break;
          case SET: // set: 1 a b
            if (ram[pc + 2] >= 0x8000)
              line = (String.format("[%d] set r%d = r%d", pc, ram[pc + 1] - 0x8000, ram[pc + 2] - 0x8000));
            else
              line = (String.format("[%d] set r%d = %d", pc, ram[pc + 1] - 0x8000, ram[pc + 2]));
            pc += 3;
            break;
          case PUSH: // push: 2 a
          case POP: // pop: 3 a
          case CALL: // call: 17 a
            line += appendStack(opCode, pc);
            pc += 2;
            break;
          case EQ: // eq: 4 a b c
          case GT: // gt: 5 a b c
          case ADD: // add: 9 a b c
          case MULT: // mult: 10 a b c
          case MOD: // mod: 11 a b c
          case AND: // and: 12 a b c
          case OR: // or: 13 a b c
            line += appendInfo(opCode, pc, 3);
            pc += 4;
            break;
          case JMP: // jmp: 6 a
            line = String.format("[%d] jmp %d", pc, ram[pc + 1]);
            pc += 2;
            break;
          case JT: // jt: 7 a b
          case JF: // jf: 8 a b
          case NOT: // not: 14 a b
            line += appendInfo(opCode, pc, 2);
            pc += 3;
            break;
          case RMEM: // rmem: 15 a b
          case WMEM: // wmem: 16 a b
            line += appendMemInfo(opCode, pc);
            pc += 3;
            break;
          case RET: // ret: 18
            line = (String.format("[%d] ret", pc));
            pc += 1;
            break;
          case OUT: // out: 19 a
            if (ram[pc + 1] >= 0x8000)
              line = (String.format("[%d] out r%d", pc, ram[pc + 1] - 0x8000));
            else if (ram[pc + 1] != '\n')
              line = (String.format("[%d] out '%c' %d", pc, (char) ram[pc + 1], ram[pc + 1]));
            else
              line = (String.format("[%d] out '\\n' %d", pc, ram[pc + 1]));
            pc += 2;
            break;
          case IN: // in: 20 a
            if (ram[pc + 1] >= 0x8000)
              line = (String.format("[%d] in r%d", pc, ram[pc + 1] - 0x8000));
            else
              line = (String.format("[%d] in %d", pc, ram[pc + 1]));
            pc += 2;
            break;
          case NOP: // noop: 21
            line = (String.format("[%d] nop", pc));
            pc += 1;
            break;
          case ERR:
            line = (String.format("[%d] err %d", pc, ram[pc]));
            pc += 1;
          default:
            break;
        }
      }
      out.add(line);
    }
    return out;
  }

  private String appendInfo(INSTRUCTION op, int pc, int count) {
    StringBuilder sb = new StringBuilder();
    sb.append("[").append(pc).append("] ").append(op.toString().toLowerCase());
    for (int i = 1; i <= count; i++)
      if (ram[pc + i] >= 0x8000)
        sb.append(" r").append(ram[pc + i] - 0x8000);
      else
        sb.append(" ").append(ram[pc + i]);
    return sb.toString();
  }

  private String appendStack(INSTRUCTION op, int pc) {
    if (ram[pc + 1] >= 0x8000)
      return String.format("[%d] %s @r%d", pc, op.toString(), ram[pc + 1] - 0x8000);
    else
      return String.format("[%d] %s @%d", pc, op.toString(), ram[pc + 1]);
  }

  private String appendMemInfo(INSTRUCTION op, int pc) {
    StringBuilder sb = new StringBuilder();
    sb.append("[").append(pc).append("] ").append(op.toString().toLowerCase());

    if (ram[pc + 1] >= 0x8000)
      sb.append(" r").append(ram[pc + 1] - 0x8000);
    else
      sb.append(" @").append(ram[pc + 1]);
    if (ram[pc + 2] >= 0x8000)
      sb.append(" @r").append(ram[pc + 2] - 0x8000);
    else
      sb.append(" @").append(ram[pc + 2]);

    return sb.toString();
  }

  public static void main(String[] args) throws IOException {
    Dissassembler diss = new Dissassembler();
    diss.loadRam(new File("./res/challenge.bin"));
    Files.write(Paths.get("./res/diss_enc.txt"), diss.dissassemble());
    System.out.println("done");
  }
}
