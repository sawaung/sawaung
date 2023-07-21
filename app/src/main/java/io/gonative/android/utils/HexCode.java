package io.gonative.android.utils;

import androidx.core.view.MotionEventCompat;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

public class HexCode {
    public static byte[] getBytes(char[] chars) {
        Charset cs = Charset.forName("UTF-8");
        CharBuffer cb = CharBuffer.allocate(chars.length);
        cb.put(chars);
        cb.flip();
        return cs.encode(cb).array();
    }

    public static char[] getChars(byte[] bytes) {
        Charset cs = Charset.forName("UTF-8");
        ByteBuffer bb = ByteBuffer.allocate(bytes.length);
        bb.put(bytes);
        bb.flip();
        return cs.decode(bb).array();
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (byte b : src) {
            String hv = Integer.toHexString(b & MotionEventCompat.ACTION_MASK);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static String toHexString(final byte[] buf) {
        return toHexString(buf, 0, buf.length);
    }

    public static String toHexString(final byte[] buf, int begin, int end) {
        StringBuilder sb = new StringBuilder(3 * (end - begin));
        toHexString(sb, buf, begin, end);
        return sb.toString();
    }

    public static void toHexString(StringBuilder sb, final byte[] buf) {
        toHexString(sb, buf, 0, buf.length);
    }

    public static void toHexString(StringBuilder sb, final byte[] buf, int begin, int end) {
        for (int pos = begin; pos < end; pos++) {
            if (sb.length() > 0)
                sb.append(' ');
            int c;
            c = (buf[pos] & 0xff) / 16;
            if (c >= 10) c += 'A' - 10;
            else c += '0';
            sb.append((char) c);
            c = (buf[pos] & 0xff) % 16;
            if (c >= 10) c += 'A' - 10;
            else c += '0';
            sb.append((char) c);
        }
    }

    public static byte[] fromHexString(final CharSequence s) {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        byte b = 0;
        int nibble = 0;
        for (int pos = 0; pos < s.length(); pos++) {
            if (nibble == 2) {
                buf.write(b);
                nibble = 0;
                b = 0;
            }
            int c = s.charAt(pos);
            if (c >= '0' && c <= '9') {
                nibble++;
                b *= 16;
                b += c - '0';
            }
            if (c >= 'A' && c <= 'F') {
                nibble++;
                b *= 16;
                b += c - 'A' + 10;
            }
            if (c >= 'a' && c <= 'f') {
                nibble++;
                b *= 16;
                b += c - 'a' + 10;
            }
        }
        if (nibble > 0)
            buf.write(b);
        return buf.toByteArray();
    }

    public static String hexToASCII(String hexValue)
    {
        StringBuilder output = new StringBuilder("");
        for (int i = 0; i < hexValue.length(); i += 2)
        {
            String str = hexValue.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }
        return output.toString();
    }
    public static String asciiToHex(String asciiValue)
    {
        char[] chars = asciiValue.toCharArray();
        StringBuilder hex = new StringBuilder();
        for (char aChar : chars) {
            hex.append(Integer.toHexString((int) aChar));
        }
        return hex.toString();
    }
}
