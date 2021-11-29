package org.apache.maven.util;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;

/**
 * 文件处理类
 */
public class FileUtil {

    /** * 16进制字符集 */
    private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    /** * 指定算法为MD5的MessageDigest */
    private static MessageDigest messageDigest = null;

    /** * 初始化messageDigest的加密算法为MD5 */
    static {
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


    /**
     * * 获取文件的MD5值
     *
     * @param file
     *            目标文件
     *
     * @return MD5字符串
     */
    public static String getFileMD5String(File file) {
        String ret = "";
        FileInputStream in = null;
        FileChannel ch = null;
        try {
            in = new FileInputStream(file);
            ch = in.getChannel();
            ByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0,
                    file.length());
            messageDigest.update(byteBuffer);
            ret = bytesToHex(messageDigest.digest());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (ch != null) {
                try {
                    ch.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return ret;
    }

    /**
     * * 将字节数组中指定区间的子数组转换成16进制字符串
     *
     * @param bytes
     *            目标字节数组
     *
     * @param start
     *            起始位置（包括该位置）
     *
     * @param end
     *            结束位置（不包括该位置）
     *
     * @return 转换结果
     */
    public static String bytesToHex(byte bytes[], int start, int end) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < start + end; i++) {
            sb.append(byteToHex(bytes[i]));
        }
        return sb.toString();

    }

    /**
     * * 将单个字节码转换成16进制字符串
     *
     * @param bt
     *            目标字节
     *
     * @return 转换结果
     */
    public static String byteToHex(byte bt) {
        return new StringBuilder(HEX_DIGITS[(bt & 0xf0) >> 4]).append(HEX_DIGITS[bt & 0xf]).toString();

    }

    /**
     * * 将字节数组转换成16进制字符串
     *
     * @param bytes
     *            目标字节数组
     *
     * @return 转换结果
     */
    public static String bytesToHex(byte bytes[]) {
        return bytesToHex(bytes, 0, bytes.length);

    }

    public static void replaceContent(File file, Map<String, String> replaceMap) throws IOException {
        FileReader in = null;
        BufferedReader bufIn = null;
        FileWriter out =  null;
        CharArrayWriter tempStream = null;

        try {
            in = new FileReader(file);
            bufIn = new BufferedReader(in);
            tempStream = new CharArrayWriter();
            String line = null;
            while((line = bufIn.readLine())!= null) {
                line = replaceLineByMap(line, replaceMap);
                tempStream.write(line);
                tempStream.write(System.lineSeparator());
            }
            out = new FileWriter(file);
            tempStream.writeTo(out);
        } catch (IOException e) {
            throw e;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufIn != null) {
                try {
                    bufIn.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private static String replaceLineByMap(String line, Map<String, String> replaceMap) {
        Iterator<String> i = replaceMap.keySet().iterator();
        while(i.hasNext()) {
            String replaceStr = i.next();
            line = line.replaceAll(replaceStr, replaceMap.get(replaceStr));
        }
        return line;
    }
}
