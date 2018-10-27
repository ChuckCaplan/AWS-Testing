package com.chuckcaplan.aws;

import com.amazonaws.services.kms.*;
import com.amazonaws.services.kms.model.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Base64;

public class KMSTest {
	
	
	public static void main(String[] args) {
		// create a client connection based on credentials
                AWSKMS kmsClient = AWSKMSClientBuilder.defaultClient();

// Encrypt a data key
//
// Replace the following fictitious CMK ARN with a valid CMK ID or ARN
String keyId = args[0];
ByteBuffer plaintext = str_to_bb(args[1]);

EncryptRequest req = new EncryptRequest().withKeyId(keyId).withPlaintext(plaintext);
ByteBuffer ciphertext = kmsClient.encrypt(req).getCiphertextBlob();
String b64 = Base64.getEncoder().encodeToString(ciphertext.array());
System.out.println("Encrypted value: "+ b64);

// Decrypt a data key

DecryptRequest dreq = new DecryptRequest().withCiphertextBlob(ByteBuffer.wrap(Base64.getDecoder().decode(b64)));
ByteBuffer plainText = kmsClient.decrypt(dreq).getPlaintext();
System.out.println("Decrypted value: " + bb_to_str(plainText));
	}
	
public static ByteBuffer str_to_bb(String msg){
    return ByteBuffer.wrap(msg.getBytes(Charset.defaultCharset()));
}

public static String bb_to_str(ByteBuffer buffer){
    byte[] bytes;
    if(buffer.hasArray()) {
        bytes = buffer.array();
    } else {
        bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
    }
    return new String(bytes, Charset.defaultCharset());
}

}
