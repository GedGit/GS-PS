package org.rs2server.jaglibs;


/* Rasterizer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */

public class Rasterizer
{
	private static boolean aBoolean1434;
	public static int[] pixels;
	public static int width;
	public static int height;
	public static int anInt1438;
	public static int ymax;
	public static int anInt1440;
	public static int anInt1441;
	public static int xmax;
	public static int anInt1443;
	public static int anInt1444;

	public static void init(int w, int h, int[] is) {
		pixels = is;
		width = w;
		height = h;
		setBounds(w, 0, h, 0);
	}

	public static void method329(int i) {
		if (i >= 4 && i <= 4) {
			anInt1440 = 0;
			anInt1438 = 0;
			anInt1441 = width;
			ymax = height;
			xmax = anInt1441 - 1;
			anInt1443 = anInt1441 / 2;
		}
	}

	public static void setBounds(int i, int i_2_, int i_3_, int i_4_) {
		if (i_2_ < 0) {
			i_2_ = 0;
		}
		if (i_4_ < 0) {
			i_4_ = 0;
		}
		if (i_3_ > width) {
			i_3_ = width;
		}
		if (i > height) {
			i = height;
		}
		anInt1440 = i_2_;
		anInt1438 = i_4_;
		anInt1441 = i_3_;
		ymax = i;
		xmax = anInt1441 - 1;
		anInt1443 = anInt1441 / 2;
		anInt1444 = ymax / 2;
	}

	public static void method331(boolean bool) {
		if (!bool) {
			aBoolean1434 = !aBoolean1434;
		}
		int i = width * height;
		for (int i_5_ = 0; i_5_ < i; i_5_++)
			pixels[i_5_] = 0;
	}

	public static void method332(int i, int i_6_, int i_7_, int i_8_, int i_9_, int i_10_, int i_11_) {
		if (i_11_ < anInt1440) {
			i_7_ -= anInt1440 - i_11_;
			i_11_ = anInt1440;
		}
		if (i_6_ < anInt1438) {
			i_8_ -= anInt1438 - i_6_;
			i_6_ = anInt1438;
		}
		if (i_11_ + i_7_ > anInt1441) {
			i_7_ = anInt1441 - i_11_;
		}
		if (i_6_ + i_8_ > ymax) {
			i_8_ = ymax - i_6_;
		}
		int i_12_ = 256 - i_9_;
		int i_13_ = (i >> 16 & 0xff) * i_9_;
		int i_14_ = (i >> 8 & 0xff) * i_9_;
		int i_15_ = (i & 0xff) * i_9_;
		int i_16_ = width - i_7_;
		int i_17_ = i_11_ + i_6_ * width;
		for (int i_18_ = 0; i_18_ < i_8_; i_18_++) {
			for (int i_19_ = -i_7_; i_19_ < 0; i_19_++) {
				int i_20_ = (pixels[i_17_] >> 16 & 0xff) * i_12_;
				int i_21_ = (pixels[i_17_] >> 8 & 0xff) * i_12_;
				int i_22_ = (pixels[i_17_] & 0xff) * i_12_;
				int i_23_ = (i_13_ + i_20_ >> 8 << 16) + (i_14_ + i_21_ >> 8 << 8) + (i_15_ + i_22_ >> 8);
				pixels[i_17_++] = i_23_;
			}
			i_17_ += i_16_;
		}
	}

	public static void method333(int i, int i_24_, int i_25_, int i_26_, int i_27_, int i_28_) {
		if (i_25_ < anInt1440) {
			i_27_ -= anInt1440 - i_25_;
			i_25_ = anInt1440;
		}
		if (i_24_ < anInt1438) {
			i -= anInt1438 - i_24_;
			i_24_ = anInt1438;
		}
		if (i_25_ + i_27_ > anInt1441) {
			i_27_ = anInt1441 - i_25_;
		}
		if (i_24_ + i > ymax) {
			i = ymax - i_24_;
		}
		int i_29_ = width - i_27_;
		int i_30_ = i_25_ + i_24_ * width;
		for (int i_31_ = -i; i_31_ < 0; i_31_++) {
			for (int i_32_ = -i_27_; i_32_ < 0; i_32_++)
				pixels[i_30_++] = i_26_;
			i_30_ += i_29_;
		}
	}

	public static void method334(int i, int i_33_, int i_34_, int i_35_, int i_36_) {
		method336(i_36_, i_35_, i_33_, i);
		method336(i_36_ + i_34_ - 1, i_35_, i_33_, i);
		method338(i_36_, i_35_, i_34_, i);
		method338(i_36_, i_35_, i_34_, i + i_33_ - 1);
	}

	public static void method335(int i, int i_37_, int i_38_, int i_39_, int i_40_, int i_41_, int i_42_) {
		method337(i_39_, i_40_, i, true, i_38_, i_41_);
		method337(i_39_, i_40_, i + i_37_ - 1, true, i_38_, i_41_);
		method339(i_39_, i_41_, i_38_, i + 1, (byte) 3, i_37_ - 2);
		method339(i_39_, i_41_ + i_40_ - 1, i_38_, i + 1, (byte) 3, i_37_ - 2);
	}

	public static void method336(int i, int i_44_, int i_45_, int i_46_) {
		if (i >= anInt1438 && i < ymax) {
			if (i_46_ < anInt1440) {
				i_45_ -= anInt1440 - i_46_;
				i_46_ = anInt1440;
			}
			if (i_46_ + i_45_ > anInt1441) {
				i_45_ = anInt1441 - i_46_;
			}
			int i_47_ = i_46_ + i * width;
				for (int i_48_ = 0; i_48_ < i_45_; i_48_++) {
					pixels[i_47_ + i_48_] = i_44_;
				}
		}
	}

	public static void method337(int i, int i_49_, int i_50_, boolean bool, int i_51_, int i_52_) {
		if (i_50_ >= anInt1438 && i_50_ < ymax) {
			if (i_52_ < anInt1440) {
				i_49_ -= anInt1440 - i_52_;
				i_52_ = anInt1440;
			}
			if (i_52_ + i_49_ > anInt1441) {
				i_49_ = anInt1441 - i_52_;
			}
			int i_53_ = 256 - i_51_;
			int i_54_ = (i >> 16 & 0xff) * i_51_;
			int i_55_ = (i >> 8 & 0xff) * i_51_;
			int i_56_ = (i & 0xff) * i_51_;
			int i_57_ = i_52_ + i_50_ * width;
			for (int i_58_ = 0; i_58_ < i_49_; i_58_++) {
				int i_59_ = (pixels[i_57_] >> 16 & 0xff) * i_53_;
				int i_60_ = (pixels[i_57_] >> 8 & 0xff) * i_53_;
				int i_61_ = (pixels[i_57_] & 0xff) * i_53_;
				int i_62_ = (i_54_ + i_59_ >> 8 << 16) + (i_55_ + i_60_ >> 8 << 8) + (i_56_ + i_61_ >> 8);
				pixels[i_57_++] = i_62_;
			}
		}
	}

	public static void method338(int i, int i_63_, int i_64_, int i_65_) {
		if (i_65_ >= anInt1440 && i_65_ < anInt1441) {
			if (i < anInt1438) {
				i_64_ -= anInt1438 - i;
				i = anInt1438;
			}
			if (i + i_64_ > ymax) {
				i_64_ = ymax - i;
			}
			int i_67_ = i_65_ + i * width;
			for (int i_68_ = 0; i_68_ < i_64_; i_68_++)
				pixels[i_67_ + i_68_ * width] = i_63_;
		}
	}

	public static void method339(int i, int i_69_, int i_70_, int i_71_, byte b, int i_72_) {
		if (i_69_ >= anInt1440 && i_69_ < anInt1441) {
			if (i_71_ < anInt1438) {
				i_72_ -= anInt1438 - i_71_;
				i_71_ = anInt1438;
			}
			if (i_71_ + i_72_ > ymax) {
				i_72_ = ymax - i_71_;
			}
			int i_73_ = 256 - i_70_;
			int i_74_ = (i >> 16 & 0xff) * i_70_;
			int i_75_ = (i >> 8 & 0xff) * i_70_;
			int i_76_ = (i & 0xff) * i_70_;
			if (b == 3) {
				int i_77_ = i_69_ + i_71_ * width;
				for (int i_78_ = 0; i_78_ < i_72_; i_78_++) {
					int i_79_ = (pixels[i_77_] >> 16 & 0xff) * i_73_;
					int i_80_ = (pixels[i_77_] >> 8 & 0xff) * i_73_;
					int i_81_ = (pixels[i_77_] & 0xff) * i_73_;
					int i_82_ = (i_74_ + i_79_ >> 8 << 16) + (i_75_ + i_80_ >> 8 << 8) + (i_76_ + i_81_ >> 8);
					pixels[i_77_] = i_82_;
					i_77_ += width;
				}
			}
		}
	}
}
