package com.ezto;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Crypt extends Task{

    private static final char[] PASSWORD = "pww4hCrJrpTqtHZMnGPMTnJecTnf8zptDMAUhGAGR3CZHAYNFWkNhabGRBtR7ZcxfCZmrAYBJJHgMsPcthZxY29CSLAeKjkM".toCharArray();
    private static final byte[] SALT = {
        (byte) 0xc7, (byte) 0x56, (byte) 0x81, (byte) 0x92,
        (byte) 0x89, (byte) 0xfe, (byte) 0x76, (byte) 0x27,
    };
    private static int			ITERATION_COUNT = 1536;
    private static String		ENCRYPTION_ALGORITHM_KEY_FACTORY = "PBEWithMD5AndTripleDES";
    private static String		ENCRYPTION_ALGORITHM_CIPHER = "PBEWithMD5AndDES";
    private String credential = "";
    private String decryptedCredential = "";
    
    public static String encrypt(String property) throws GeneralSecurityException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ENCRYPTION_ALGORITHM_KEY_FACTORY);
        SecretKey key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));
        Cipher pbeCipher = Cipher.getInstance(ENCRYPTION_ALGORITHM_CIPHER);
        pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT, ITERATION_COUNT));
        return base64Encode(pbeCipher.doFinal(property.getBytes()));
    }

    private static String base64Encode(byte[] bytes) {
        // NB: This class is internal, and you probably should use another impl
        return new BASE64Encoder().encode(bytes);
    }
    
    /*public static void main(String[] args) throws Exception{
		String username = "ezto";
		System.out.println(encrypt(username));
	}*/
    
    public void execute() throws BuildException {
    	try{
    		credential = decrypt(credential);
    		Project project = getProject();
    		project.setProperty(decryptedCredential, credential);
    	}catch(Exception ex){}
    	  
    	//System.out.println("Message: " + message);
	}
    
    public void setCredential(String credential) {
		this.credential= credential;
	}
    
    public void setDecryptedCredential(String decryptedCredential) {
		this.decryptedCredential= decryptedCredential;
	}
    
    public static String decrypt(String property) throws GeneralSecurityException, IOException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ENCRYPTION_ALGORITHM_KEY_FACTORY);
        SecretKey key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));
        Cipher pbeCipher = Cipher.getInstance(ENCRYPTION_ALGORITHM_CIPHER);
        pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, ITERATION_COUNT));
        return new String(pbeCipher.doFinal(base64Decode(property)));
    }

    private static byte[] base64Decode(String property) throws IOException {
        // NB: This class is internal, and you probably should use another impl
        return new BASE64Decoder().decodeBuffer(property);
    }
    
    public static void main(String[] args) throws Exception{
		String dbEztoUserid = "EZTO";
		String dbEztoPassword = "EZTODPDEV";
		String dbEztoAppUserid = "EZTOAPP";
		String dbEztoAppPassword = "";
		
		System.out.println(dbEztoUserid + " -> " + encrypt(dbEztoUserid)); 
		System.out.println(dbEztoPassword + " -> " + encrypt(dbEztoPassword));
		System.out.println(dbEztoAppUserid + " -> " + encrypt(dbEztoAppUserid));
		System.out.println(dbEztoAppPassword + " -> " + encrypt(dbEztoAppPassword));
	}
}