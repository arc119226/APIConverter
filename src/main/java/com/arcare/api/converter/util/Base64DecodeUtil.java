package com.arcare.api.converter.util;
import java.util.Base64;

/**
 * 
 * @author FUHSIANG_LIU
 *
 */
public class Base64DecodeUtil {
	/**
	 * decode to byte[]
	 * @param base64
	 * @return byte[]
	 */
	public static byte[] decodeToByte(String base64) {
		Base64.Decoder dec = Base64.getDecoder();
		return dec.decode(base64);
	}
	/**
	 * 
	 * @param byte[]
	 * @return base64 string
	 */
	public static String encodeToString(byte[] input) {
		Base64.Encoder enc= Base64.getEncoder();
		return enc.encodeToString(input);
	}
}
