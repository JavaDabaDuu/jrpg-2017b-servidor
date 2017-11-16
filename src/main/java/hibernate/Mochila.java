
package hibernate;

public class Mochila {
	private int idMochila;
	private int item1;
	private int item2;
	private int item3;
	private int item4;
	private int item5;
	private int item6;
	private int item7;
	private int item8;
	private int item9;
	private int item10;
	private int item11;
	private int item12;
	private int item13;
	private int item14;
	private int item15;
	private int item16;
	private int item17;
	private int item18;
	private int item19;
	private int item20;
	
	
	
	public Mochila(int idMochila) {
		this.idMochila = idMochila;
		this.item1 = SLOT1;
		this.item2 = SLOT2;
		this.item3 = SLOT3;
		this.item4 = SLOT4;
		this.item5 = SLOT5;
		this.item6 = SLOT6;
		this.item7 = SLOT7;
		this.item8 = SLOT8;
		this.item9 = SLOT9;
		this.item10 = SLOT10;
		this.item11 = SLOT11;
		this.item12 = SLOT12;
		this.item13 = SLOT13;
		this.item14 = SLOT14;
		this.item15 = SLOT15;
		this.item16 = SLOT16;
		this.item17 = SLOT17;
		this.item18 = SLOT18;
		this.item19 = SLOT19;
		this.item20 = SLOT20;
	}

	public int getIdMochila() {
		return idMochila;
	}

	public void setIdMochila(int idMochila) {
		this.idMochila = idMochila;
	}

	public int getItem1() {
		return item1;
	}

	public void setItem1(int item1) {
		this.item1 = item1;
	}

	public int getItem2() {
		return item2;
	}

	public void setItem2(int item2) {
		this.item2 = item2;
	}

	public int getItem3() {
		return item3;
	}

	public void setItem3(int item3) {
		this.item3 = item3;
	}

	public int getItem4() {
		return item4;
	}

	public void setItem4(int item4) {
		this.item4 = item4;
	}

	public int getItem5() {
		return item5;
	}

	public void setItem5(int item5) {
		this.item5 = item5;
	}

	public int getItem6() {
		return item6;
	}

	public void setItem6(int item6) {
		this.item6 = item6;
	}

	public int getItem7() {
		return item7;
	}

	public void setItem7(int item7) {
		this.item7 = item7;
	}

	public int getItem8() {
		return item8;
	}

	public void setItem8(int item8) {
		this.item8 = item8;
	}

	public int getItem9() {
		return item9;
	}

	public void setItem9(int item9) {
		this.item9 = item9;
	}

	public int getItem10() {
		return item10;
	}

	public void setItem10(int item10) {
		this.item10 = item10;
	}

	public int getItem11() {
		return item11;
	}

	public void setItem11(int item11) {
		this.item11 = item11;
	}

	public int getItem12() {
		return item12;
	}

	public void setItem12(int item12) {
		this.item12 = item12;
	}

	public int getItem13() {
		return item13;
	}

	public void setItem13(int item13) {
		this.item13 = item13;
	}

	public int getItem14() {
		return item14;
	}

	public void setItem14(int item14) {
		this.item14 = item14;
	}

	public int getItem15() {
		return item15;
	}

	public void setItem15(int item15) {
		this.item15 = item15;
	}

	public int getItem16() {
		return item16;
	}

	public void setItem16(int item16) {
		this.item16 = item16;
	}

	public int getItem17() {
		return item17;
	}

	public void setItem17(int item17) {
		this.item17 = item17;
	}

	public int getItem18() {
		return item18;
	}

	public void setItem18(int item18) {
		this.item18 = item18;
	}

	public int getItem19() {
		return item19;
	}

	public void setItem19(int item19) {
		this.item19 = item19;
	}

	public int getItem20() {
		return item20;
	}

	public void setItem20(int item20) {
		this.item20 = item20;
	}

	private static final int SLOT1 = 1;
	private static final int SLOT2 = 2;
	private static final int SLOT3 = 3;
	private static final int SLOT4 = 4;
	private static final int SLOT5 = 5;
	private static final int SLOT6 = 6;
	private static final int SLOT7 = 7;
	private static final int SLOT8 = 8;
	private static final int SLOT9 = 9;
	private static final int SLOT10 = 10;
	private static final int SLOT11 = 11;
	private static final int SLOT12 = 12;
	private static final int SLOT13 = 13;
	private static final int SLOT14 = 14;
	private static final int SLOT15 = 15;
	private static final int SLOT16 = 16;
	private static final int SLOT17 = 17;
	private static final int SLOT18 = 18;
	private static final int SLOT19 = 19;
	private static final int SLOT20 = 20;

	public int getByItemId(final int i) {
		switch (i) {
		case SLOT1:
			return getItem1();
		case SLOT2:
			return getItem2();
		case SLOT3:
			return getItem3();
		case SLOT4:
			return getItem4();
		case SLOT5:
			return getItem5();
		case SLOT6:
			return getItem6();
		case SLOT7:
			return getItem7();
		case SLOT8:
			return getItem8();
		case SLOT9:
			return getItem9();
		case SLOT10:
			return getItem10();
		case SLOT11:
			return getItem11();
		case SLOT12:
			return getItem12();
		case SLOT13:
			return getItem13();
		case SLOT14:
			return getItem14();
		case SLOT15:
			return getItem15();
		case SLOT16:
			return getItem16();
		case SLOT17:
			return getItem17();
		case SLOT18:
			return getItem18();
		case SLOT19:
			return getItem19();
		case SLOT20:
			return getItem20();
		default:
			break;
		}

		return -1;
	}

	public Mochila(int idMochila, int item1, int item2, int item3, int item4, int item5, int item6, int item7,
			int item8, int item9, int item10, int item11, int item12, int item13, int item14, int item15, int item16,
			int item17, int item18, int item19, int item20) {
		super();
		this.idMochila = idMochila;
		this.item1 = item1;
		this.item2 = item2;
		this.item3 = item3;
		this.item4 = item4;
		this.item5 = item5;
		this.item6 = item6;
		this.item7 = item7;
		this.item8 = item8;
		this.item9 = item9;
		this.item10 = item10;
		this.item11 = item11;
		this.item12 = item12;
		this.item13 = item13;
		this.item14 = item14;
		this.item15 = item15;
		this.item16 = item16;
		this.item17 = item17;
		this.item18 = item18;
		this.item19 = item19;
		this.item20 = item20;
	}

}