package de.mkrane.synacor.vm;

public enum INSTRUCTION {
	HALT, SET, PUSH, POP, EQ, GT, JMP, JT, JF, ADD, MULT, MOD, AND, OR, NOT, RMEM, WMEM, CALL, RET, OUT, IN, NOP, ERR;
	private static final INSTRUCTION[] allValues = values();

	public static INSTRUCTION fromInt(int n) {
		if (n >= allValues.length)
			return ERR;
		return allValues[n];
	}
}
