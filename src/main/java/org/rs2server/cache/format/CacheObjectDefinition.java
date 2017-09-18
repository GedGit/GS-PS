package org.rs2server.cache.format;

import java.util.HashMap;
import java.util.Map;

import org.rs2server.cache.CacheManager;
import org.rs2server.io.InputStream;
import org.rs2server.rs2.model.GameObjectDefinition;

/* Class23_Sub13_Sub7 - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */

public class CacheObjectDefinition {

	public int anInt3740;
	public boolean aBoolean3741;
	public short[] aShortArray3742;
	public int anInt3743;
	public int anInt3744;
	public static int anInt3745;
	public int[] anIntArray3746;
	public int playerVariable = -1;
	public int anInt3748;
	public static int anInt3749;
	public static byte[][][] aByteArrayArrayArray3750;
	public int anInt3751;
	public int sizeX;
	public static int anInt3753;
	public int sizeY;
	public int anInt3755;
	public int anInt3756;
	public static int anInt3757;
	public boolean notCliped;
	public boolean aBoolean3759;
	public short[] destModelColors;
	public int anInt3761;
	public static String aClass16_3762;
	public boolean aBoolean3763;
	public byte[] aByteArray3764;
	public static int anInt3765;
	public byte aByte3766;
	public int[] anIntArray3767;
	public boolean aBoolean3768;
	public static int anInt3769;
	public static int anInt3770;
	public boolean aBoolean3771;
	public short[] srcModelColors;
	public static int anInt3773;
	public boolean aBoolean3774;
	public static int anInt3775;
	public int anInt3776;
	public static int anInt3777;
	public int anInt3778;
	public int anInt3779;
	public static int anInt3780;
	public static int anInt3781;
	public int anInt3782;
	public int anInt3783;
	public static int anInt3784 = 0;
	public boolean aBoolean3785;
	public int playerSetting;
	public int anInt3787;
	public int anInt3788;
	public short aShort3789;
	public int surroundings;
	public int anInt3791;
	public static boolean aBoolean3792 = false;
	public int anInt3793;
	public static int anInt3794;
	public int anInt3795;
	public boolean aBoolean3796;
	public String objectName;
	public static int anInt3798;
	public int[] anIntArray3799;
	public String[] objectOptions;
	public int[] anIntArray3801;
	public short[] aShortArray3802;
	public int anInt3803;
	public boolean aBoolean3804;
	public static String aClass16_3805;
	public int clipType;
	public byte[] description;

	private final void readValues(InputStream class23_sub5, int i, boolean bool) {

		if (i == 1) {
			int i_13_ = class23_sub5.readByte() & 0xff;
			if ((i_13_ ^ 0xffffffff) < -1) {
				if (anIntArray3799 != null) {
					class23_sub5.length += 3 * i_13_;
				} else {
					anIntArray3799 = new int[i_13_];
					anIntArray3767 = new int[i_13_];
					for (int i_14_ = 0; i_13_ > i_14_; i_14_++) {
						@SuppressWarnings("unused")
						int x = anIntArray3799[i_14_] = class23_sub5.readShort() & 0xFFFF;
						@SuppressWarnings("unused")
						int y = anIntArray3767[i_14_] = class23_sub5.readUnsignedByte();
					}
				}
			}
			/**
			 * int len = stream.readUnsignedByte(); if (len > 0) { if (anIntArray773 == null
			 * || lowMem) { anIntArray776 = new int[len]; anIntArray773 = new int[len]; for
			 * (int k1 = 0; k1 < len; k1++) { anIntArray773[k1] = stream.readUnsignedWord();
			 * anIntArray776[k1] = stream.readUnsignedByte(); } } else {
			 * stream.currentOffset += len * 3; } }
			 */
		} else if (i == 2) {
			objectName = class23_sub5.readString();
		} else if (i == 3) {
			description = class23_sub5.readStringBytes();
		} else if (i != 5) {
			if (i != 14) {
				if (i != 15) {
					if (i != 17) {
						if (i != 18) {
							if (i != 19) {
								if (i != 21) {
									if (i == 22) {
										aBoolean3804 = true;
									} else if (i != 23) {
										if (i != 24) {
											if (i != 27) {
												if (i != 28) {
													if (i == 29) {
														anInt3803 = class23_sub5.readByte();
													} else if (i == 39) {
														anInt3778 = 5 * class23_sub5.readByte();
													} else if (i >= 30 && i < 35) {
														objectOptions[-30 + i] = class23_sub5.readString();

													} else if (i == 40) {
														int i_2_ = class23_sub5.readByte() & 0xff;
														destModelColors = new short[i_2_];
														srcModelColors = new short[i_2_];
														for (int i_3_ = 0; (i_2_ ^ 0xffffffff) < (i_3_
																^ 0xffffffff); i_3_++) {
															srcModelColors[i_3_] = (short) class23_sub5
																	.readShortLE((byte) -125);
															destModelColors[i_3_] = (short) class23_sub5
																	.readShortLE((byte) -109);
														}
													} else if (i == 41) {
														int i_4_ = class23_sub5.readByte() & 0xff;
														aShortArray3802 = new short[i_4_];
														aShortArray3742 = new short[i_4_];
														for (int i_5_ = 0; (i_5_ ^ 0xffffffff) > (i_4_
																^ 0xffffffff); i_5_++) {
															aShortArray3742[i_5_] = (short) class23_sub5
																	.readShortLE((byte) -124);
															aShortArray3802[i_5_] = (short) class23_sub5
																	.readShortLE((byte) -109);
														}
													} else if (i == 42) {
														int i_6_ = class23_sub5.readByte() & 0xff;
														aByteArray3764 = new byte[i_6_];
														for (int i_7_ = 0; (i_6_ ^ 0xffffffff) < (i_7_
																^ 0xffffffff); i_7_++)
															aByteArray3764[i_7_] = (byte) class23_sub5.readByte();
													} else if (i != 60) {
														if (i != 62) {
															if (i == 64) {
																aBoolean3774 = false;
															} else if (i == 65) {
																anInt3761 = class23_sub5.readShortLE((byte) -100);
															} else if (i == 66) {
																anInt3787 = class23_sub5.readShortLE((byte) -113);
															} else if (i == 67) {
																anInt3740 = class23_sub5.readShortLE((byte) -108);
															} else if (i == 68) {
																anInt3744 = class23_sub5.readShortLE((byte) -100);
															} else if (i != 69) {
																if (i != 70) {
																	if (i != 71) {
																		if (i == 72) {
																			anInt3748 = class23_sub5
																					.readUnsignedSmart();
																		} else if (i == 73) {
																			aBoolean3785 = true;
																		} else if (i == 74) {
																			notCliped = true;
																		} else if (i == 75) {
																			anInt3783 = class23_sub5.readByte() & 0xff;
																		} else if (i != 77 && i != 92) {
																			if (i == 78) {
																				anInt3743 = class23_sub5
																						.readShortLE((byte) -111);
																				anInt3788 = class23_sub5.readByte()
																						& 0xff;
																			} else if (i != 79) {
																				if (i != 81) {
																					if (i != 82 && i != 88) {
																						if (i == 89) {
																							aBoolean3768 = false;
																						} else if (i == 90) {
																							aBoolean3741 = true;
																						} else if (i != 91) {
																							if (i != 93) {
																								if (i == 94) {
																									aByte3766 = (byte) 4;
																								} else if (i == 95) {
																									aByte3766 = (byte) 5;
																								}
																							} else {
																								aByte3766 = (byte) 3;
																								aShort3789 = (short) class23_sub5
																										.readShortLE(
																												(byte) -104);
																							}
																						} else {
																							aBoolean3759 = true;
																						}
																					}
																				} else {
																					aByte3766 = (byte) 2;
																					aShort3789 = (short) (class23_sub5
																							.readByte() & 0xff * 256);
																				}
																			} else {
																				anInt3755 = class23_sub5
																						.readShortLE((byte) -112);
																				anInt3782 = class23_sub5
																						.readShortLE((byte) -120);
																				anInt3788 = class23_sub5.readByte()
																						& 0xff;
																				int i_8_ = class23_sub5.readByte()
																						& 0xff;
																				anIntArray3801 = new int[i_8_];
																				for (int i_9_ = 0; (i_9_
																						^ 0xffffffff) > (i_8_
																								^ 0xffffffff); i_9_++)
																					anIntArray3801[i_9_] = class23_sub5
																							.readShortLE((byte) -109);
																			}
																		} else {
																			playerVariable = class23_sub5.readShort();
																			if ((playerVariable
																					^ 0xffffffff) == -65536) {
																				playerVariable = -1;
																			}
																			playerSetting = class23_sub5.readShort();
																			int i_10_ = -1;
																			if ((playerSetting
																					^ 0xffffffff) == -65536) {
																				playerSetting = -1;
																			}
																			/*
																			 * if (i == 92) { i_10_ =
																			 * class23_sub5.readShortLE((byte) -116); if
																			 * (i_10_ == 65535) { i_10_ = -1; } }
																			 */
																			int i_11_ = class23_sub5.readByte() & 0xff;
																			anIntArray3746 = new int[i_11_ + 2];
																			for (int i_12_ = 0; (i_12_
																					^ 0xffffffff) >= (i_11_
																							^ 0xffffffff); i_12_++) {
																				anIntArray3746[i_12_] = class23_sub5
																						.readShort();
																				if (anIntArray3746[i_12_] == 65535) {
																					anIntArray3746[i_12_] = -1;
																				}
																			}
																			anIntArray3746[i_11_ - -1] = i_10_;
																		}
																	} else {
																		anInt3795 = class23_sub5.readUnsignedSmart();
																	}
																} else {
																	anInt3756 = class23_sub5.readUnsignedSmart();
																}
															} else {
																surroundings = class23_sub5.readByte();
															}
														} else {
															aBoolean3796 = true;
														}
													} else {
														anInt3751 = class23_sub5.readShortLE((byte) -115);
													}
												} else {
													anInt3779 = class23_sub5.readByte() & 0xff;
												}
											} else {
												clipType = 1;
											}
										} else {
											anInt3776 = class23_sub5.readShortLE((byte) -123);
											if ((anInt3776 ^ 0xffffffff) == -65536) {
												anInt3776 = -1;
											}
										}
									} else {
										aBoolean3771 = true;
									}
								} else {
									aByte3766 = (byte) 1;
								}
							} else {
								anInt3793 = class23_sub5.readByte() & 0xff;
							}
						} else {
							aBoolean3763 = false;
						}
					} else {
						aBoolean3763 = false;
						clipType = 0;
					}
				} else {
					sizeY = class23_sub5.readByte() & 0xff;
				}
			} else {
				sizeX = class23_sub5.readByte() & 0xff;
			}
		} else {
			int i_13_ = class23_sub5.readByte() & 0xff;
			if ((i_13_ ^ 0xffffffff) < -1) {
				if (anIntArray3799 != null) {
					class23_sub5.length += 2 * i_13_;
				} else {
					anIntArray3799 = new int[i_13_];
					anIntArray3767 = null;
					for (int i_14_ = 0; i_13_ > i_14_; i_14_++)
						anIntArray3799[i_14_] = class23_sub5.readShort();
				}
			}
		}
		if (bool == true) {
			anInt3749++;
		}
	}

	public int getSurroundings() {
		return surroundings;
	}

	public final void readOpcodes(InputStream object) {
		for (;;) {
			try {
				int i_42_ = object.readByte() & 0xff;
				if (i_42_ == 0) {
					break;
				}
				readValues(object, i_42_, true);
			} catch (ArrayIndexOutOfBoundsException e) {

			}
		}
		anInt3780++;
	}

	private int id;
	public int opposite = -1;

	public CacheObjectDefinition() {
		anInt3743 = -1;
		anInt3756 = 0;
		anInt3748 = 0;
		anInt3751 = -1;
		aBoolean3768 = true;
		aBoolean3763 = true;
		anInt3744 = -1;
		anInt3776 = -1;
		anInt3740 = 128;
		aBoolean3785 = false;
		aBoolean3774 = true;
		sizeX = 1;
		sizeY = 1;
		anInt3778 = 0;
		anInt3787 = 128;
		aShort3789 = (short) -1;
		notCliped = false;
		objectOptions = new String[5];
		aBoolean3771 = false;
		anInt3779 = 16;
		aBoolean3796 = false;
		playerSetting = -1;
		anInt3793 = -1;
		anInt3795 = 0;
		aBoolean3759 = false;
		anInt3755 = 0;
		objectName = "null";
		anInt3783 = -1;
		anInt3761 = 128;
		anInt3788 = 0;
		anInt3803 = 0;
		aBoolean3741 = false;
		anInt3782 = 0;
		clipType = 2;
		aByte3766 = (byte) 0;
		aBoolean3804 = false;
		surroundings = 0;
	}

	public static final Map<Integer, CacheObjectDefinition> definitions = new HashMap<Integer, CacheObjectDefinition>();

	public boolean getNotClipped() {
		return notCliped;
	}

	public int clipType() {
		return clipType;
	}

	public static final Map<Integer, Integer> OPEN_DOOR_MAP = new HashMap<>();
	public static final Map<Integer, Integer> CLOSE_DOOR_MAP = new HashMap<>();

	public static CacheObjectDefinition forID(int i) {
		if (definitions.get(i) != null)
			return definitions.get(i);

		byte[] data = null;
		try {
			// data = World.cache.read(2, 6, i).array();

			// data = World.store.getData(2, 6, i);
			data = CacheManager.getData(2, 6, i);
		} catch (Exception e) {
			e.printStackTrace();
		}
		CacheObjectDefinition obj = new CacheObjectDefinition();
		if (data != null) {
			InputStream input = new InputStream(data);
			obj.readOpcodes(input);
		}
		obj.id = i;
		definitions.put(obj.id, obj);
		return obj;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return this.objectName;
	}

	public int getId() {
		return this.id;
	}

	public String[] getOptions() {
		return this.objectOptions;
	}

	public int getSizeX() {
		return sizeX;
	}

	public int getSizeY() {
		return sizeY;
	}

	public int getBiggestSize() {
		if (sizeY > sizeX)
			return sizeY;
		return sizeX;
	}

	public static GameObjectDefinition forId(int i) {
		// TODO Auto-generated method stub
		return null;
	}
}