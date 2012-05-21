/* Generated By:JavaCC: Do not edit this line. XMLBIFv02TokenManager.java */
package javabayes.Parsers.XMLBIFv02;

public class XMLBIFv02TokenManager implements XMLBIFv02Constants {
	private final int jjStopAtPos(int pos, int kind) {
		jjmatchedKind = kind;
		jjmatchedPos = pos;
		return pos + 1;
	}

	private final int jjMoveStringLiteralDfa0_0() {
		switch (curChar) {
		case 60:
			jjmatchedKind = 3;
			return jjMoveStringLiteralDfa1_0(0x10L);
		case 62:
			jjmatchedKind = 5;
			return jjMoveNfa_0(1, 0);
		case 66:
			return jjMoveStringLiteralDfa1_0(0x80L);
		case 68:
			return jjMoveStringLiteralDfa1_0(0x100L);
		case 69:
			return jjMoveStringLiteralDfa1_0(0x200L);
		case 70:
			return jjMoveStringLiteralDfa1_0(0x400L);
		case 71:
			return jjMoveStringLiteralDfa1_0(0x800L);
		case 78:
			return jjMoveStringLiteralDfa1_0(0x3000L);
		case 80:
			return jjMoveStringLiteralDfa1_0(0xc000L);
		case 84:
			return jjMoveStringLiteralDfa1_0(0x30000L);
		case 86:
			return jjMoveStringLiteralDfa1_0(0xc0000L);
		case 98:
			return jjMoveStringLiteralDfa1_0(0x80L);
		case 100:
			return jjMoveStringLiteralDfa1_0(0x100100L);
		case 101:
			return jjMoveStringLiteralDfa1_0(0x200L);
		case 102:
			return jjMoveStringLiteralDfa1_0(0x400L);
		case 103:
			return jjMoveStringLiteralDfa1_0(0x800L);
		case 110:
			return jjMoveStringLiteralDfa1_0(0x3000L);
		case 112:
			return jjMoveStringLiteralDfa1_0(0xc000L);
		case 116:
			return jjMoveStringLiteralDfa1_0(0x30000L);
		case 118:
			return jjMoveStringLiteralDfa1_0(0xc0000L);
		default:
			return jjMoveNfa_0(1, 0);
		}
	}

	private final int jjMoveStringLiteralDfa1_0(long active0) {
		try {
			curChar = input_stream.readChar();
		} catch (java.io.IOException e) {
			return jjMoveNfa_0(1, 0);
		}
		switch (curChar) {
		case 47:
			if ((active0 & 0x10L) != 0L) {
				jjmatchedKind = 4;
				jjmatchedPos = 1;
			}
			break;
		case 65:
			return jjMoveStringLiteralDfa2_0(active0, 0xd1000L);
		case 69:
			return jjMoveStringLiteralDfa2_0(active0, 0x2100L);
		case 73:
			return jjMoveStringLiteralDfa2_0(active0, 0x880L);
		case 78:
			return jjMoveStringLiteralDfa2_0(active0, 0x200L);
		case 79:
			return jjMoveStringLiteralDfa2_0(active0, 0x400L);
		case 82:
			return jjMoveStringLiteralDfa2_0(active0, 0xc000L);
		case 89:
			return jjMoveStringLiteralDfa2_0(active0, 0x20000L);
		case 97:
			return jjMoveStringLiteralDfa2_0(active0, 0xd1000L);
		case 101:
			return jjMoveStringLiteralDfa2_0(active0, 0x2100L);
		case 105:
			return jjMoveStringLiteralDfa2_0(active0, 0x100880L);
		case 110:
			return jjMoveStringLiteralDfa2_0(active0, 0x200L);
		case 111:
			return jjMoveStringLiteralDfa2_0(active0, 0x400L);
		case 114:
			return jjMoveStringLiteralDfa2_0(active0, 0xc000L);
		case 121:
			return jjMoveStringLiteralDfa2_0(active0, 0x20000L);
		default:
			break;
		}
		return jjMoveNfa_0(1, 1);
	}

	private final int jjMoveStringLiteralDfa2_0(long old0, long active0) {
		if (((active0 &= old0)) == 0L)
			return jjMoveNfa_0(1, 1);
		try {
			curChar = input_stream.readChar();
		} catch (java.io.IOException e) {
			return jjMoveNfa_0(1, 1);
		}
		switch (curChar) {
		case 66:
			return jjMoveStringLiteralDfa3_0(active0, 0x10000L);
		case 70:
			if ((active0 & 0x80L) != 0L) {
				jjmatchedKind = 7;
				jjmatchedPos = 2;
			}
			return jjMoveStringLiteralDfa3_0(active0, 0x100L);
		case 76:
			return jjMoveStringLiteralDfa3_0(active0, 0x40000L);
		case 77:
			return jjMoveStringLiteralDfa3_0(active0, 0x1000L);
		case 79:
			return jjMoveStringLiteralDfa3_0(active0, 0xc000L);
		case 80:
			return jjMoveStringLiteralDfa3_0(active0, 0x20000L);
		case 82:
			if ((active0 & 0x400L) != 0L) {
				jjmatchedKind = 10;
				jjmatchedPos = 2;
			}
			return jjMoveStringLiteralDfa3_0(active0, 0x80000L);
		case 84:
			return jjMoveStringLiteralDfa3_0(active0, 0x2200L);
		case 86:
			return jjMoveStringLiteralDfa3_0(active0, 0x800L);
		case 98:
			return jjMoveStringLiteralDfa3_0(active0, 0x10000L);
		case 102:
			if ((active0 & 0x80L) != 0L) {
				jjmatchedKind = 7;
				jjmatchedPos = 2;
			}
			return jjMoveStringLiteralDfa3_0(active0, 0x100L);
		case 108:
			return jjMoveStringLiteralDfa3_0(active0, 0x40000L);
		case 109:
			return jjMoveStringLiteralDfa3_0(active0, 0x1000L);
		case 111:
			return jjMoveStringLiteralDfa3_0(active0, 0xc000L);
		case 112:
			return jjMoveStringLiteralDfa3_0(active0, 0x20000L);
		case 114:
			if ((active0 & 0x400L) != 0L) {
				jjmatchedKind = 10;
				jjmatchedPos = 2;
			}
			return jjMoveStringLiteralDfa3_0(active0, 0x80000L);
		case 115:
			return jjMoveStringLiteralDfa3_0(active0, 0x100000L);
		case 116:
			return jjMoveStringLiteralDfa3_0(active0, 0x2200L);
		case 118:
			return jjMoveStringLiteralDfa3_0(active0, 0x800L);
		default:
			break;
		}
		return jjMoveNfa_0(1, 2);
	}

	private final int jjMoveStringLiteralDfa3_0(long old0, long active0) {
		if (((active0 &= old0)) == 0L)
			return jjMoveNfa_0(1, 2);
		try {
			curChar = input_stream.readChar();
		} catch (java.io.IOException e) {
			return jjMoveNfa_0(1, 2);
		}
		switch (curChar) {
		case 65:
			return jjMoveStringLiteralDfa4_0(active0, 0x100L);
		case 66:
			return jjMoveStringLiteralDfa4_0(active0, 0x4000L);
		case 69:
			if ((active0 & 0x1000L) != 0L) {
				jjmatchedKind = 12;
				jjmatchedPos = 3;
			} else if ((active0 & 0x20000L) != 0L) {
				jjmatchedKind = 17;
				jjmatchedPos = 3;
			}
			return jjMoveStringLiteralDfa4_0(active0, 0x800L);
		case 73:
			return jjMoveStringLiteralDfa4_0(active0, 0x80000L);
		case 76:
			return jjMoveStringLiteralDfa4_0(active0, 0x10000L);
		case 80:
			return jjMoveStringLiteralDfa4_0(active0, 0x8000L);
		case 82:
			return jjMoveStringLiteralDfa4_0(active0, 0x200L);
		case 85:
			return jjMoveStringLiteralDfa4_0(active0, 0x40000L);
		case 87:
			return jjMoveStringLiteralDfa4_0(active0, 0x2000L);
		case 97:
			return jjMoveStringLiteralDfa4_0(active0, 0x100L);
		case 98:
			return jjMoveStringLiteralDfa4_0(active0, 0x4000L);
		case 99:
			return jjMoveStringLiteralDfa4_0(active0, 0x100000L);
		case 101:
			if ((active0 & 0x1000L) != 0L) {
				jjmatchedKind = 12;
				jjmatchedPos = 3;
			} else if ((active0 & 0x20000L) != 0L) {
				jjmatchedKind = 17;
				jjmatchedPos = 3;
			}
			return jjMoveStringLiteralDfa4_0(active0, 0x800L);
		case 105:
			return jjMoveStringLiteralDfa4_0(active0, 0x80000L);
		case 108:
			return jjMoveStringLiteralDfa4_0(active0, 0x10000L);
		case 112:
			return jjMoveStringLiteralDfa4_0(active0, 0x8000L);
		case 114:
			return jjMoveStringLiteralDfa4_0(active0, 0x200L);
		case 117:
			return jjMoveStringLiteralDfa4_0(active0, 0x40000L);
		case 119:
			return jjMoveStringLiteralDfa4_0(active0, 0x2000L);
		default:
			break;
		}
		return jjMoveNfa_0(1, 3);
	}

	private final int jjMoveStringLiteralDfa4_0(long old0, long active0) {
		if (((active0 &= old0)) == 0L)
			return jjMoveNfa_0(1, 3);
		try {
			curChar = input_stream.readChar();
		} catch (java.io.IOException e) {
			return jjMoveNfa_0(1, 3);
		}
		switch (curChar) {
		case 65:
			return jjMoveStringLiteralDfa5_0(active0, 0x84000L);
		case 69:
			if ((active0 & 0x10000L) != 0L) {
				jjmatchedKind = 16;
				jjmatchedPos = 4;
			} else if ((active0 & 0x40000L) != 0L) {
				jjmatchedKind = 18;
				jjmatchedPos = 4;
			}
			return jjMoveStringLiteralDfa5_0(active0, 0x8000L);
		case 78:
			if ((active0 & 0x800L) != 0L) {
				jjmatchedKind = 11;
				jjmatchedPos = 4;
			}
			break;
		case 79:
			return jjMoveStringLiteralDfa5_0(active0, 0x2000L);
		case 85:
			return jjMoveStringLiteralDfa5_0(active0, 0x100L);
		case 89:
			if ((active0 & 0x200L) != 0L) {
				jjmatchedKind = 9;
				jjmatchedPos = 4;
			}
			break;
		case 97:
			return jjMoveStringLiteralDfa5_0(active0, 0x84000L);
		case 101:
			if ((active0 & 0x10000L) != 0L) {
				jjmatchedKind = 16;
				jjmatchedPos = 4;
			} else if ((active0 & 0x40000L) != 0L) {
				jjmatchedKind = 18;
				jjmatchedPos = 4;
			}
			return jjMoveStringLiteralDfa5_0(active0, 0x8000L);
		case 110:
			if ((active0 & 0x800L) != 0L) {
				jjmatchedKind = 11;
				jjmatchedPos = 4;
			}
			break;
		case 111:
			return jjMoveStringLiteralDfa5_0(active0, 0x2000L);
		case 114:
			return jjMoveStringLiteralDfa5_0(active0, 0x100000L);
		case 117:
			return jjMoveStringLiteralDfa5_0(active0, 0x100L);
		case 121:
			if ((active0 & 0x200L) != 0L) {
				jjmatchedKind = 9;
				jjmatchedPos = 4;
			}
			break;
		default:
			break;
		}
		return jjMoveNfa_0(1, 4);
	}

	private final int jjMoveStringLiteralDfa5_0(long old0, long active0) {
		if (((active0 &= old0)) == 0L)
			return jjMoveNfa_0(1, 4);
		try {
			curChar = input_stream.readChar();
		} catch (java.io.IOException e) {
			return jjMoveNfa_0(1, 4);
		}
		switch (curChar) {
		case 66:
			return jjMoveStringLiteralDfa6_0(active0, 0x84000L);
		case 76:
			return jjMoveStringLiteralDfa6_0(active0, 0x100L);
		case 82:
			return jjMoveStringLiteralDfa6_0(active0, 0xa000L);
		case 98:
			return jjMoveStringLiteralDfa6_0(active0, 0x84000L);
		case 101:
			return jjMoveStringLiteralDfa6_0(active0, 0x100000L);
		case 108:
			return jjMoveStringLiteralDfa6_0(active0, 0x100L);
		case 114:
			return jjMoveStringLiteralDfa6_0(active0, 0xa000L);
		default:
			break;
		}
		return jjMoveNfa_0(1, 5);
	}

	private final int jjMoveStringLiteralDfa6_0(long old0, long active0) {
		if (((active0 &= old0)) == 0L)
			return jjMoveNfa_0(1, 5);
		try {
			curChar = input_stream.readChar();
		} catch (java.io.IOException e) {
			return jjMoveNfa_0(1, 5);
		}
		switch (curChar) {
		case 73:
			return jjMoveStringLiteralDfa7_0(active0, 0x4000L);
		case 75:
			if ((active0 & 0x2000L) != 0L) {
				jjmatchedKind = 13;
				jjmatchedPos = 6;
			}
			break;
		case 76:
			return jjMoveStringLiteralDfa7_0(active0, 0x80000L);
		case 84:
			if ((active0 & 0x100L) != 0L) {
				jjmatchedKind = 8;
				jjmatchedPos = 6;
			}
			return jjMoveStringLiteralDfa7_0(active0, 0x8000L);
		case 105:
			return jjMoveStringLiteralDfa7_0(active0, 0x4000L);
		case 107:
			if ((active0 & 0x2000L) != 0L) {
				jjmatchedKind = 13;
				jjmatchedPos = 6;
			}
			break;
		case 108:
			return jjMoveStringLiteralDfa7_0(active0, 0x80000L);
		case 116:
			if ((active0 & 0x100L) != 0L) {
				jjmatchedKind = 8;
				jjmatchedPos = 6;
			}
			return jjMoveStringLiteralDfa7_0(active0, 0x108000L);
		default:
			break;
		}
		return jjMoveNfa_0(1, 6);
	}

	private final int jjMoveStringLiteralDfa7_0(long old0, long active0) {
		if (((active0 &= old0)) == 0L)
			return jjMoveNfa_0(1, 6);
		try {
			curChar = input_stream.readChar();
		} catch (java.io.IOException e) {
			return jjMoveNfa_0(1, 6);
		}
		switch (curChar) {
		case 69:
			if ((active0 & 0x80000L) != 0L) {
				jjmatchedKind = 19;
				jjmatchedPos = 7;
			}
			break;
		case 76:
			return jjMoveStringLiteralDfa8_0(active0, 0x4000L);
		case 89:
			if ((active0 & 0x8000L) != 0L) {
				jjmatchedKind = 15;
				jjmatchedPos = 7;
			}
			break;
		case 101:
			if ((active0 & 0x80000L) != 0L) {
				jjmatchedKind = 19;
				jjmatchedPos = 7;
			} else if ((active0 & 0x100000L) != 0L) {
				jjmatchedKind = 20;
				jjmatchedPos = 7;
			}
			break;
		case 108:
			return jjMoveStringLiteralDfa8_0(active0, 0x4000L);
		case 121:
			if ((active0 & 0x8000L) != 0L) {
				jjmatchedKind = 15;
				jjmatchedPos = 7;
			}
			break;
		default:
			break;
		}
		return jjMoveNfa_0(1, 7);
	}

	private final int jjMoveStringLiteralDfa8_0(long old0, long active0) {
		if (((active0 &= old0)) == 0L)
			return jjMoveNfa_0(1, 7);
		try {
			curChar = input_stream.readChar();
		} catch (java.io.IOException e) {
			return jjMoveNfa_0(1, 7);
		}
		switch (curChar) {
		case 73:
			return jjMoveStringLiteralDfa9_0(active0, 0x4000L);
		case 105:
			return jjMoveStringLiteralDfa9_0(active0, 0x4000L);
		default:
			break;
		}
		return jjMoveNfa_0(1, 8);
	}

	private final int jjMoveStringLiteralDfa9_0(long old0, long active0) {
		if (((active0 &= old0)) == 0L)
			return jjMoveNfa_0(1, 8);
		try {
			curChar = input_stream.readChar();
		} catch (java.io.IOException e) {
			return jjMoveNfa_0(1, 8);
		}
		switch (curChar) {
		case 84:
			return jjMoveStringLiteralDfa10_0(active0, 0x4000L);
		case 116:
			return jjMoveStringLiteralDfa10_0(active0, 0x4000L);
		default:
			break;
		}
		return jjMoveNfa_0(1, 9);
	}

	private final int jjMoveStringLiteralDfa10_0(long old0, long active0) {
		if (((active0 &= old0)) == 0L)
			return jjMoveNfa_0(1, 9);
		try {
			curChar = input_stream.readChar();
		} catch (java.io.IOException e) {
			return jjMoveNfa_0(1, 9);
		}
		switch (curChar) {
		case 89:
			if ((active0 & 0x4000L) != 0L) {
				jjmatchedKind = 14;
				jjmatchedPos = 10;
			}
			break;
		case 121:
			if ((active0 & 0x4000L) != 0L) {
				jjmatchedKind = 14;
				jjmatchedPos = 10;
			}
			break;
		default:
			break;
		}
		return jjMoveNfa_0(1, 10);
	}

	private final void jjCheckNAdd(int state) {
		if (jjrounds[state] != jjround) {
			jjstateSet[jjnewStateCnt++] = state;
			jjrounds[state] = jjround;
		}
	}

	private final void jjAddStates(int start, int end) {
		do {
			jjstateSet[jjnewStateCnt++] = jjnextStates[start];
		} while (start++ != end);
	}

	private final void jjCheckNAddTwoStates(int state1, int state2) {
		jjCheckNAdd(state1);
		jjCheckNAdd(state2);
	}

	private final void jjCheckNAddStates(int start, int end) {
		do {
			jjCheckNAdd(jjnextStates[start]);
		} while (start++ != end);
	}

	private final void jjCheckNAddStates(int start) {
		jjCheckNAdd(jjnextStates[start]);
		jjCheckNAdd(jjnextStates[start + 1]);
	}

	static final long[] jjbitVec0 = { 0x0L, 0x0L, 0xffffffffffffffffL,
			0xffffffffffffffffL };

	private final int jjMoveNfa_0(int startState, int curPos) {
		int strKind = jjmatchedKind;
		int strPos = jjmatchedPos;
		int seenUpto;
		input_stream.backup(seenUpto = curPos + 1);
		try {
			curChar = input_stream.readChar();
		} catch (java.io.IOException e) {
			throw new Error("Internal Error");
		}
		curPos = 0;
		int[] nextStates;
		int startsAt = 0;
		jjnewStateCnt = 30;
		int i = 1;
		jjstateSet[0] = startState;
		int j, kind = 0x7fffffff;
		for (;;) {
			if (++jjround == 0x7fffffff)
				ReInitRounds();
			if (curChar < 64) {
				long l = 1L << curChar;
				MatchLoop: do {
					switch (jjstateSet[--i]) {
					case 1:
						if ((0xefffffffffffffffL & l) != 0L) {
							if (kind > 23)
								kind = 23;
						} else if (curChar == 60)
							jjAddStates(0, 1);
						if ((0x3ff000000000000L & l) != 0L) {
							if (kind > 21)
								kind = 21;
							jjCheckNAddStates(2, 5);
						} else if ((0x100003600L & l) != 0L) {
							if (kind > 1)
								kind = 1;
							jjCheckNAdd(0);
						} else if (curChar == 46)
							jjCheckNAdd(4);
						if ((0x3fe000000000000L & l) != 0L) {
							if (kind > 21)
								kind = 21;
							jjCheckNAdd(2);
						}
						break;
					case 0:
						if ((0x100003600L & l) == 0L)
							break;
						if (kind > 1)
							kind = 1;
						jjCheckNAdd(0);
						break;
					case 2:
						if ((0x3ff000000000000L & l) == 0L)
							break;
						if (kind > 21)
							kind = 21;
						jjCheckNAdd(2);
						break;
					case 3:
						if (curChar == 46)
							jjCheckNAdd(4);
						break;
					case 4:
						if ((0x3ff000000000000L & l) == 0L)
							break;
						if (kind > 21)
							kind = 21;
						jjCheckNAddTwoStates(4, 5);
						break;
					case 6:
						if ((0x280000000000L & l) != 0L)
							jjCheckNAdd(7);
						break;
					case 7:
						if ((0x3ff000000000000L & l) == 0L)
							break;
						if (kind > 21)
							kind = 21;
						jjCheckNAdd(7);
						break;
					case 8:
						if ((0xefffffffffffffffL & l) != 0L && kind > 23)
							kind = 23;
						break;
					case 9:
						if ((0x3ff000000000000L & l) == 0L)
							break;
						if (kind > 21)
							kind = 21;
						jjCheckNAddStates(2, 5);
						break;
					case 10:
						if ((0x3ff000000000000L & l) != 0L)
							jjCheckNAddTwoStates(10, 11);
						break;
					case 11:
						if (curChar != 46)
							break;
						if (kind > 21)
							kind = 21;
						jjCheckNAddTwoStates(12, 13);
						break;
					case 12:
						if ((0x3ff000000000000L & l) == 0L)
							break;
						if (kind > 21)
							kind = 21;
						jjCheckNAddTwoStates(12, 13);
						break;
					case 14:
						if ((0x280000000000L & l) != 0L)
							jjCheckNAdd(15);
						break;
					case 15:
						if ((0x3ff000000000000L & l) == 0L)
							break;
						if (kind > 21)
							kind = 21;
						jjCheckNAdd(15);
						break;
					case 16:
						if ((0x3ff000000000000L & l) == 0L)
							break;
						if (kind > 21)
							kind = 21;
						jjCheckNAddTwoStates(16, 17);
						break;
					case 18:
						if ((0x280000000000L & l) != 0L)
							jjCheckNAdd(19);
						break;
					case 19:
						if ((0x3ff000000000000L & l) == 0L)
							break;
						if (kind > 21)
							kind = 21;
						jjCheckNAdd(19);
						break;
					case 20:
						if (curChar == 60)
							jjAddStates(0, 1);
						break;
					case 21:
						if (curChar == 33)
							jjCheckNAddTwoStates(22, 23);
						break;
					case 22:
						if ((0xbfffffffffffffffL & l) != 0L)
							jjCheckNAddTwoStates(22, 23);
						break;
					case 23:
						if (curChar == 62 && kind > 2)
							kind = 2;
						break;
					case 25:
						if ((0xbfffffffffffffffL & l) != 0L)
							jjAddStates(6, 7);
						break;
					case 26:
						if (curChar == 62 && kind > 6)
							kind = 6;
						break;
					case 29:
						if (curChar == 63)
							jjstateSet[jjnewStateCnt++] = 28;
						break;
					default:
						break;
					}
				} while (i != startsAt);
			} else if (curChar < 128) {
				long l = 1L << (curChar & 077);
				MatchLoop: do {
					switch (jjstateSet[--i]) {
					case 1:
						if (kind > 23)
							kind = 23;
						break;
					case 5:
						if ((0x2000000020L & l) != 0L)
							jjAddStates(8, 9);
						break;
					case 13:
						if ((0x2000000020L & l) != 0L)
							jjAddStates(10, 11);
						break;
					case 17:
						if ((0x2000000020L & l) != 0L)
							jjAddStates(12, 13);
						break;
					case 22:
						jjAddStates(14, 15);
						break;
					case 24:
						if ((0x100000001000L & l) != 0L)
							jjCheckNAddTwoStates(25, 26);
						break;
					case 25:
						jjCheckNAddTwoStates(25, 26);
						break;
					case 27:
						if ((0x200000002000L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 24;
						break;
					case 28:
						if ((0x100000001000000L & l) != 0L)
							jjstateSet[jjnewStateCnt++] = 27;
						break;
					default:
						break;
					}
				} while (i != startsAt);
			} else {
				int i2 = (curChar & 0xff) >> 6;
				long l2 = 1L << (curChar & 077);
				MatchLoop: do {
					switch (jjstateSet[--i]) {
					case 1:
						if ((jjbitVec0[i2] & l2) != 0L && kind > 23)
							kind = 23;
						break;
					case 22:
						if ((jjbitVec0[i2] & l2) != 0L)
							jjAddStates(14, 15);
						break;
					case 25:
						if ((jjbitVec0[i2] & l2) != 0L)
							jjAddStates(6, 7);
						break;
					default:
						break;
					}
				} while (i != startsAt);
			}
			if (kind != 0x7fffffff) {
				jjmatchedKind = kind;
				jjmatchedPos = curPos;
				kind = 0x7fffffff;
			}
			++curPos;
			if ((i = jjnewStateCnt) == (startsAt = 30 - (jjnewStateCnt = startsAt)))
				break;
			try {
				curChar = input_stream.readChar();
			} catch (java.io.IOException e) {
				break;
			}
		}
		if (jjmatchedPos > strPos)
			return curPos;

		int toRet = Math.max(curPos, seenUpto);

		if (curPos < toRet)
			for (i = toRet - Math.min(curPos, seenUpto); i-- > 0;)
				try {
					curChar = input_stream.readChar();
				} catch (java.io.IOException e) {
					throw new Error(
							"Internal Error : Please send a bug report.");
				}

		if (jjmatchedPos < strPos) {
			jjmatchedKind = strKind;
			jjmatchedPos = strPos;
		} else if (jjmatchedPos == strPos && jjmatchedKind > strKind)
			jjmatchedKind = strKind;

		return toRet;
	}

	static final int[] jjnextStates = { 21, 29, 10, 11, 16, 17, 25, 26, 6, 7,
			14, 15, 18, 19, 22, 23, };
	public static final String[] jjstrLiteralImages = { "", null, null, "\74",
			"\74\57", "\76", null, null, null, null, null, null, null, null,
			null, null, null, null, null, null,
			"\144\151\163\143\162\145\164\145", null, null, null, };
	public static final String[] lexStateNames = { "DEFAULT", };
	static final long[] jjtoToken = { 0xbffff9L, };
	static final long[] jjtoSkip = { 0x6L, };
	static final long[] jjtoSpecial = { 0x4L, };
	private ASCII_CharStream input_stream;
	private final int[] jjrounds = new int[30];
	private final int[] jjstateSet = new int[60];
	protected char curChar;

	public XMLBIFv02TokenManager(ASCII_CharStream stream) {
		if (ASCII_CharStream.staticFlag)
			throw new Error(
					"ERROR: Cannot use a static CharStream class with a non-static lexical analyzer.");
		input_stream = stream;
	}

	public XMLBIFv02TokenManager(ASCII_CharStream stream, int lexState) {
		this(stream);
		SwitchTo(lexState);
	}

	public void ReInit(ASCII_CharStream stream) {
		jjmatchedPos = jjnewStateCnt = 0;
		curLexState = defaultLexState;
		input_stream = stream;
		ReInitRounds();
	}

	private final void ReInitRounds() {
		int i;
		jjround = 0x80000001;
		for (i = 30; i-- > 0;)
			jjrounds[i] = 0x80000000;
	}

	public void ReInit(ASCII_CharStream stream, int lexState) {
		ReInit(stream);
		SwitchTo(lexState);
	}

	public void SwitchTo(int lexState) {
		if (lexState >= 1 || lexState < 0)
			throw new TokenMgrError("Error: Ignoring invalid lexical state : "
					+ lexState + ". State unchanged.",
					TokenMgrError.INVALID_LEXICAL_STATE);
		else
			curLexState = lexState;
	}

	private final Token jjFillToken() {
		Token t = Token.newToken(jjmatchedKind);
		t.kind = jjmatchedKind;
		String im = jjstrLiteralImages[jjmatchedKind];
		t.image = (im == null) ? input_stream.GetImage() : im;
		t.beginLine = input_stream.getBeginLine();
		t.beginColumn = input_stream.getBeginColumn();
		t.endLine = input_stream.getEndLine();
		t.endColumn = input_stream.getEndColumn();
		return t;
	}

	int curLexState = 0;
	int defaultLexState = 0;
	int jjnewStateCnt;
	int jjround;
	int jjmatchedPos;
	int jjmatchedKind;

	public final Token getNextToken() {
		int kind;
		Token specialToken = null;
		Token matchedToken;
		int curPos = 0;

		EOFLoop: for (;;) {
			try {
				curChar = input_stream.BeginToken();
			} catch (java.io.IOException e) {
				jjmatchedKind = 0;
				matchedToken = jjFillToken();
				matchedToken.specialToken = specialToken;
				return matchedToken;
			}

			jjmatchedKind = 0x7fffffff;
			jjmatchedPos = 0;
			curPos = jjMoveStringLiteralDfa0_0();
			if (jjmatchedKind != 0x7fffffff) {
				if (jjmatchedPos + 1 < curPos)
					input_stream.backup(curPos - jjmatchedPos - 1);
				if ((jjtoToken[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L) {
					matchedToken = jjFillToken();
					matchedToken.specialToken = specialToken;
					return matchedToken;
				} else {
					if ((jjtoSpecial[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L) {
						matchedToken = jjFillToken();
						if (specialToken == null)
							specialToken = matchedToken;
						else {
							matchedToken.specialToken = specialToken;
							specialToken = (specialToken.next = matchedToken);
						}
					}
					continue EOFLoop;
				}
			}
			int error_line = input_stream.getEndLine();
			int error_column = input_stream.getEndColumn();
			String error_after = null;
			boolean EOFSeen = false;
			try {
				input_stream.readChar();
				input_stream.backup(1);
			} catch (java.io.IOException e1) {
				EOFSeen = true;
				error_after = curPos <= 1 ? "" : input_stream.GetImage();
				if (curChar == '\n' || curChar == '\r') {
					error_line++;
					error_column = 0;
				} else
					error_column++;
			}
			if (!EOFSeen) {
				input_stream.backup(1);
				error_after = curPos <= 1 ? "" : input_stream.GetImage();
			}
			throw new TokenMgrError(EOFSeen, curLexState, error_line,
					error_column, error_after, curChar,
					TokenMgrError.LEXICAL_ERROR);
		}
	}

}
