package org.rs2server.cache;

import org.rs2server.cache.stream.RSByteArrayInputStream;
import org.rs2server.cache.stream.RSInputStream;
import org.rs2server.rs2.net.Packet;
import org.rs2server.rs2.net.PacketBuilder;
import org.rs2server.util.BufferUtils;

import java.io.IOException;
import java.util.zip.CRC32;

public class CacheManager {

    private static FileInformationTable[] informationTables;
    private static FileStore fs255;
    private static FileStore[] fileStores;
    private static Object[][][] archiveFiles;

    private static byte[] versionTable;

    public static void load(String path) throws Exception {
        fs255 = new FileStore(255, path);

        int cacheIndicies = fs255.length();

        informationTables = new FileInformationTable[cacheIndicies + 1];
        fileStores = new FileStore[cacheIndicies];
        archiveFiles = new Object[cacheIndicies][][];
        for (int i = 0; i < informationTables.length; i++) {
            byte[] data = fs255.get(i);
            if (data != null) {
                informationTables[i] = FileInformationTable.createFileInformationTable(i, data);
                archiveFiles[i] = new Object[informationTables[i].getEntryCount()][];
            }
        }
        for (int i = 0; i < fileStores.length; i++) {
            fileStores[i] = new FileStore(i, path);
        }
        CRC32 crc = new CRC32();
        PacketBuilder mainFileBuffer = new PacketBuilder();
        mainFileBuffer.put((byte) 0).putInt(cacheIndicies * 8);
        for (int i = 0; i < cacheIndicies; i++) {
            byte[] file = fs255.get(i);
            crc.update(file);
            mainFileBuffer.putInt((int) crc.getValue()).putInt(informationTables[i].getRevision());
            crc.reset();
        }
        versionTable = mainFileBuffer.toPacket1().array();
    }

    public static FileStore getCrc() {
        return fs255;
    }

    public static FileInformationTable getFIT(int cache) {
        return informationTables[cache];
    }

    public static byte[] getByName(int cache, String name) {
        int id = informationTables[cache].findName(name);
        return fileStores[cache].get(id);
    }
    
    public static int getArchiveName(int cache, String name) {
        int id = informationTables[cache].findName(name);
        return id;
    }

    public static byte[] getFile(int cache, int id) {
        if (cache == 255 && id == 255) {
            return versionTable;
        }
        if (cache == 255) {
            return fs255.get(id);
        }
        return fileStores[cache].get(id);
    }

    public static FileStore getFileStore(int cache) {
        if (cache == 255) {
            return fs255;
        }
        return fileStores[cache];
    }
    
	public static Packet generate(int opcode, int container, int file) throws IOException {
		byte[] cacheFile = getFile(container, file);

        if (cacheFile == null) {
            return null;
        }
		
        int compression = cacheFile[0] & 0xFF;
        int length = BufferUtils.readInt(1, cacheFile);
        PacketBuilder outBuffer = new PacketBuilder();
        outBuffer.put((byte) container);
        outBuffer.putShort((short) file);
        outBuffer.put((byte) compression);
        outBuffer.putInt(length);
        int realLength = compression != 0 ? length + 4 : length;
        
        for (int offset = 5; offset < realLength + 5; offset++) {
            if (outBuffer.position() % 512 == 0) {
                outBuffer.put((byte) 255);
            }
            outBuffer.put(cacheFile[offset]);
        }
        return outBuffer.toPacket();
	}

    public static boolean loadArchive(int cache, int main) throws IOException {
        if (archiveFiles[cache].length < main) {
            main = archiveFiles[cache].length;
        }
        int[] is_11_ = informationTables[cache].getEntry_sub_ptr()[main];
        boolean bool = true;
        int count = informationTables[cache].getArchiveCount()[main];
        if (archiveFiles[cache][main] == null)
            archiveFiles[cache][main] = new Object[informationTables[cache].getEntry_real_sub_count()[main]];
        Object[] objects = archiveFiles[cache][main];
        for (int i_13_ = 0; i_13_ < count; i_13_++) {
            int i_14_;
            if (is_11_ != null)
                i_14_ = is_11_[i_13_];
            else
                i_14_ = i_13_;
            if (objects[i_14_] == null) {
                bool = false;
                break;
            }
        }
        if (bool) {
            return true;
        }
        byte[] is_16_ = fileStores[cache].get(main);
        byte[] data = null;
        try {
            data = new CacheContainer(is_16_).decompress();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (data == null)
            return false;
        if (count > 1) {
            int i_18_ = data.length;
            int i_19_ = data[--i_18_] & 0xff;
            @SuppressWarnings("resource")
			RSInputStream stream = new RSInputStream(new RSByteArrayInputStream(data));
            i_18_ -= i_19_ * (count * 4);
            stream.seek(i_18_);
            int[] subWritePos = new int[count];
            for (int x = 0; i_19_ > x; x++) {
                int i_22_ = 0;
                for (int i = 0; i < count; i++) {
                    i_22_ += stream.readInt();
                    subWritePos[i] += i_22_;
                }
            }
            byte[][] subData = new byte[count][];
            for (int i = 0; i < count; i++) {
                subData[i] = new byte[subWritePos[i]];
                subWritePos[i] = 0;
            }
            int readPos = 0;
            stream.seek(i_18_);
            for (int i = 0; i < i_19_; i++) {
                int i_28_ = 0;
                for (int subId = 0; subId < count; subId++) {
                    i_28_ += stream.readInt();
                    System.arraycopy(data, readPos, subData[subId], subWritePos[subId], i_28_);
                    readPos += i_28_;
                    subWritePos[subId] += i_28_;
                }
            }
            for (int i = 0; i < count; i++) {
                int i_31_;
                if (is_11_ != null)
                    i_31_ = is_11_[i];
                else
                    i_31_ = i;
                objects[i_31_] = subData[i];
            }
        } else {
            int i_32_;
            if (is_11_ != null)
                i_32_ = is_11_[0];
            else
                i_32_ = 0;
            objects[i_32_] = data;
        }
        archiveFiles[cache][main] = objects;
        return true;
    }

    public static byte[] getData(int cache, int main, int child) throws IOException {
        if (!loadArchive(cache, main))
            throw new IOException("Data not available");
        if (archiveFiles[cache].length < main) {
            main = archiveFiles[cache].length;
        }
        return (byte[]) archiveFiles[cache][main][child];
    }

    public static int containerCount(int cache) {
        return archiveFiles[cache].length;
    }

    public static int cacheCFCount(int cache) {
        int lastcontainer = containerCount(cache) - 1;
        return 256 * lastcontainer + getRealContainerChildCount(cache, lastcontainer);
    }

    public static int cacheCFCount2(int cache) {
        int lastcontainer = containerCount(cache) - 1;
        return 128 * lastcontainer + getRealContainerChildCount(cache, lastcontainer);
    }

    public static int getRealContainerChildCount(int cache, int lastcontainer) {
        return informationTables[cache].getEntry_real_sub_count()[lastcontainer];
    }

    public static int getContainerChildCount(int cache, int lastcontainer) {
        return informationTables[cache].getArchiveCount()[lastcontainer];
    }

}