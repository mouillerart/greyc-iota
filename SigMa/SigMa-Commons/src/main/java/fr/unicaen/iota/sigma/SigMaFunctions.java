/*
 *  This program is a part of the IoTa Project.
 *
 *  Copyright © 2012  Université de Caen Basse-Normandie, GREYC
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  <http://www.gnu.org/licenses/>
 *
 *  See AUTHORS for a list of contributors.
 */
package fr.unicaen.iota.sigma;

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.util.Enumeration;
import java.util.List;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.security.cert.CertificateException;
import javax.xml.bind.*;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.fosstrak.epcis.model.*;
import org.xml.sax.SAXException;

/**
 * This
 * <code>SigMAFunctions</code> class contains the functions that enable the
 * signatures creation and verification.
 */
public class SigMaFunctions {

    private static final Log log = LogFactory.getLog(SigMaFunctions.class);
    private final String keyStoreFilePath;
    private final String keyStorePassword;

    static {
        // TODO: maybe this should be better done at the application level
        // BouncyCastleProvider provides RSA/ECB/PKCS1Padding algorithm
        Security.addProvider(new BouncyCastleProvider());
    }

    public SigMaFunctions(String keyStoreFilePath, String keyStorePassword) {
        this.keyStoreFilePath = keyStoreFilePath;
        this.keyStorePassword = keyStorePassword;
    }

    /**
     * Compute and insert a signature in an EPCISEventType.
     *
     * @param event the event to be signed.
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public void sign(EPCISEventType event) throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, ParserConfigurationException, SAXException, IOException {
        String cForm = createCanonicalForm(event);
        log.trace(cForm);
        String signature = createRSASignature(cForm, keyStoreFilePath);
        insertSignature(event, signature);
        String cForm2 = createCanonicalForm(event);
        log.trace(cForm2);
    }

    /**
     * Verify a signature of an EPCISEventType.
     *
     * @param event the signed event.
     * @return <code>true</code> if the siganture is correct.
     * @throws FileNotFoundException
     * @throws CertificateException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SignatureException
     * @throws java.security.cert.CertificateException
     * @throws NoSuchPaddingException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public boolean verify(EPCISEventType event) throws FileNotFoundException, CertificateException, IOException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, java.security.cert.CertificateException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, ParserConfigurationException, SAXException {
        // get the the signature in event's extensions.
        String signature = getSignature(event);
        deleteSignature(event);
        String cForm = createCanonicalForm(event);
        log.trace(cForm);
        PublicKey publicKey = getPublicKey(keyStoreFilePath);
        Signature rsa = Signature.getInstance("SHA1withRSA");
        rsa.initVerify(publicKey);
        byte[] cFormByte = cForm.getBytes("UTF-8");
        rsa.update(cFormByte);
        byte[] signatureByte = stringToByte(signature);
        // Get an SHA-1 message digest object and compute the plaintext digest
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
        // To verify, start by decrypting the signature with the
        // RSA private key
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, getPublicKey(keyStoreFilePath));
        // the sigOffset permits the substraction of the sign byte that the BigInteger
        // class adds. 
        int sigOffset = signatureByte.length % 2;
        byte[] newMD = cipher.doFinal(signatureByte, sigOffset, (signatureByte.length - sigOffset));

        // Then, recreate the message digest from the plaintext
        // to simulate what a recipient must do
        int offset = cFormByte.length % 2;
        messageDigest.reset();
        messageDigest.update(cFormByte, offset, (cFormByte.length - offset));
        byte[] oldMD = messageDigest.digest();

        // Verify that the two message digests match
        int len = newMD.length;
        if (len > oldMD.length) {
            return false;
        }
        for (int i = 0; i < len; ++i) {
            if (oldMD[i] != newMD[i]) {
                return false;
            }
        }
        return true;
    }

    private byte[] stringToByte(String text) {
        log.trace("Signature: " + text);
        BigInteger signatureBI = new BigInteger(text, 16);
        return signatureBI.toByteArray();
    }

    private String byteToString(byte[] bytes) {
        BigInteger sig = new BigInteger(1, bytes);
        return sig.toString(16);
    }

    private String createCanonicalForm(EPCISEventType event) throws ParserConfigurationException, SAXException, IOException {
        JAXBElement<EPCISEventType> elem = new JAXBElement<EPCISEventType>(new QName("urn:epcglobal:epcis:xsd:1", "XMLType"),
                EPCISEventType.class, event);

        log.trace(elem.getName().getNamespaceURI());
        log.trace(elem.getName().getPrefix());
        log.trace(elem.getName().getLocalPart());
        log.trace(elem.getName().toString());

        String eventXMLForm = "";
        if (event instanceof ObjectEventType) {
            ObjectEventType obEvent = (ObjectEventType) event;
            eventXMLForm = createCanonicalForm(obEvent);
        } else if (event instanceof AggregationEventType) {
            AggregationEventType aggEvent = (AggregationEventType) event;
            eventXMLForm = createCanonicalForm(aggEvent);
        } else if (event instanceof QuantityEventType) {
            QuantityEventType quaEvent = (QuantityEventType) event;
            eventXMLForm = createCanonicalForm(quaEvent);
        } else if (event instanceof TransactionEventType) {
            TransactionEventType traEvent = (TransactionEventType) event;
            eventXMLForm = createCanonicalForm(traEvent);
        }

        return eventXMLForm;
    }

    private String createCanonicalForm(AggregationEventType event) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            JAXBContext jc = JAXBContext.newInstance("org.fosstrak.epcis.model");
            Unmarshaller u = jc.createUnmarshaller();
            Marshaller m = jc.createMarshaller();
            m.marshal(new JAXBElement<AggregationEventType>(new QName("urn:epcglobal:epcis:xsd:1", "XMLType"),
                    AggregationEventType.class, event), baos);
        } catch (JAXBException ex) {
            log.fatal("Couldn’t marshal event", ex);
        }
        return baos.toString();
    }

    private String createCanonicalForm(ObjectEventType event) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            JAXBContext jc = JAXBContext.newInstance("org.fosstrak.epcis.model");
            Unmarshaller u = jc.createUnmarshaller();
            Marshaller m = jc.createMarshaller();
            m.marshal(new JAXBElement<ObjectEventType>(new QName("urn:epcglobal:epcis:xsd:1", "XMLType"),
                    ObjectEventType.class, event), baos);
        } catch (JAXBException ex) {
            log.fatal("Couldn’t marsal event", ex);
        }
        return baos.toString();
    }

    private String createCanonicalForm(QuantityEventType event) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            JAXBContext jc = JAXBContext.newInstance("org.fosstrak.epcis.model");
            Unmarshaller u = jc.createUnmarshaller();
            Marshaller m = jc.createMarshaller();
            m.marshal(new JAXBElement<QuantityEventType>(new QName("urn:epcglobal:epcis:xsd:1", "XMLType"),
                    QuantityEventType.class, event), baos);
        } catch (JAXBException ex) {
            log.fatal("Couldn’t marsal event", ex);
        }
        return baos.toString();
    }

    private String createCanonicalForm(TransactionEventType event) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            JAXBContext jc = JAXBContext.newInstance("org.fosstrak.epcis.model");
            Unmarshaller u = jc.createUnmarshaller();
            Marshaller m = jc.createMarshaller();
            m.marshal(new JAXBElement<TransactionEventType>(new QName("urn:epcglobal:epcis:xsd:1", "XMLType"),
                    TransactionEventType.class, event), baos);
        } catch (JAXBException ex) {
            log.fatal("Couldn’t marsal event", ex);
        }
        return baos.toString();
    }

    private KeyStore getKeyStore(String filename) {
        KeyStore ks;
        char[] password;

        try {
            ks = KeyStore.getInstance("PKCS12");
            password = keyStorePassword.toCharArray();
            ks.load(new FileInputStream(filename), password);
            return ks;
        } catch (Exception e) {
            log.warn("Keystor logging failed", e);
            return null;
        }
    }

    private PrivateKey getPrivateKey(String filename) {
        PrivateKey privateKey = null;
        char[] password = keyStorePassword.toCharArray();
        KeyStore ks = getKeyStore(filename);
        try {
            Enumeration<String> en = ks.aliases();
            // TODO: hard value
            String ALIAS = "anonymous";
            while (en.hasMoreElements()) {
                String alias = en.nextElement();
                if (ks.isKeyEntry(alias)) {
                    ALIAS = alias;
                    break;
                }
            }
            privateKey = (PrivateKey) ks.getKey(ALIAS, password);
        } catch (Exception e) {
            log.error("While seeking private key", e);
        }
        return privateKey;
    }

    private PublicKey getPublicKey(String filename) {
        KeyStore ks = getKeyStore(filename);
        PublicKey publicKey = null;

        try {
            Enumeration<String> en = ks.aliases();
            // TODO: hard value
            String ALIAS = "anonymous";
            while (en.hasMoreElements()) {
                String alias = en.nextElement();
                if (ks.isKeyEntry(alias)) {
                    ALIAS = alias;
                    break;
                }
            }
            publicKey = ks.getCertificate(ALIAS).getPublicKey();
        } catch (Exception e) {
            log.warn("While seeking public key", e);
        }
        return publicKey;
    }

    private String createRSASignature(String event, String keyStoreFilePath) throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] plainText = event.getBytes("UTF-8");
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
        messageDigest.update(plainText);
        byte[] md = messageDigest.digest();
        KeyPair key = new KeyPair(getPublicKey(keyStoreFilePath), getPrivateKey(keyStoreFilePath));
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key.getPrivate());
        byte[] cipherText = cipher.doFinal(md);
        return byteToString(cipherText);
    }

    private void insertSignature(EPCISEventType event, String signature) throws IOException, ParserConfigurationException, SAXException {
        JAXBElement<String> elem = new JAXBElement<String>(new QName("urn:unicaen:iota:sigma:signature", "signature"), String.class, signature);
        if (event instanceof ObjectEventType) {
            ((ObjectEventType) event).getAny().add(elem);
        } else if (event instanceof AggregationEventType) {
            ((AggregationEventType) event).getAny().add(elem);
        } else if (event instanceof QuantityEventType) {
            ((QuantityEventType) event).getAny().add(elem);
        } else if (event instanceof TransactionEventType) {
            ((TransactionEventType) event).getAny().add(elem);
        }
    }

    private String getSignature(EPCISEventType event) {
        String signature = "";
        List<Object> extensions;

        if (event instanceof ObjectEventType) {
            extensions = ((ObjectEventType) event).getAny();
        } else if (event instanceof AggregationEventType) {
            extensions = ((AggregationEventType) event).getAny();
        } else if (event instanceof QuantityEventType) {
            extensions = ((QuantityEventType) event).getAny();
        } else if (event instanceof TransactionEventType) {
            extensions = ((TransactionEventType) event).getAny();
        } else {
            return null;
        }
        for (Object object : extensions) {
            // we really don’t know what’s in an extension
            JAXBElement elem = (JAXBElement) object;
            if (("signature".equals(elem.getName().getLocalPart()))) {
                signature = elem.getValue().toString();
            }
        }
        return signature;
    }

    private void deleteSignature(EPCISEventType event) {
        List<Object> extensions;

        if (event instanceof ObjectEventType) {
            extensions = ((ObjectEventType) event).getAny();
        } else if (event instanceof AggregationEventType) {
            extensions = ((AggregationEventType) event).getAny();
        } else if (event instanceof QuantityEventType) {
            extensions = ((QuantityEventType) event).getAny();
        } else if (event instanceof TransactionEventType) {
            extensions = ((TransactionEventType) event).getAny();
        } else {
            return;
        }

        JAXBElement elem = null;
        for (Object object : extensions) {
            // we really don’t know what’s in an extension
            JAXBElement elemTmp = (JAXBElement) object;
            if (("signature".equals(elemTmp.getName().getLocalPart()))) {
                elem = elemTmp;
                break;
            }
        }
        if (elem != null) {
            extensions.remove(elem);
        }
    }
}
