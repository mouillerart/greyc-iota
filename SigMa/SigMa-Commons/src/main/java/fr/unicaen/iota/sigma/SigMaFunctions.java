/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2012-2013  Université de Caen Basse-Normandie, GREYC
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

import fr.unicaen.iota.mu.Constants;
import fr.unicaen.iota.mu.Utils;
import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Enumeration;
import java.util.List;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.bind.*;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.signature.XMLSignatureException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.fosstrak.epcis.model.*;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * This <code>SigMAFunctions</code> class contains the functions that enable the
 * signatures creation and verification.
 */
public class SigMaFunctions {

    private static final Log log = LogFactory.getLog(SigMaFunctions.class);
    private final String keyStoreFilePath;
    private final String keyStorePassword;
    private String signerId;

    static {
        // TODO: maybe this should be better done at the application level
        // BouncyCastleProvider provides RSA/ECB/PKCS1Padding algorithm
        Security.addProvider(new BouncyCastleProvider());
    }

    public SigMaFunctions(String keyStoreFilePath, String keyStorePassword) {
        this.keyStoreFilePath = keyStoreFilePath;
        this.keyStorePassword = keyStorePassword;
        this.signerId = null;
    }

    /**
     * Compute and insert a signature in an EPCISEventType.
     *
     * @param event the event to be signed.
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws InvalidCanonicalizerException
     * @throws CanonicalizationException
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     * @throws XMLSignatureException
     * @throws InvalidKeyException
     * @throws SignatureException
     * @throws NoSuchProviderException
     * @throws KeyStoreException
     * @throws FileNotFoundException
     * @throws IOException
     * @throws CertificateException
     * @throws UnrecoverableKeyException
     * @throws JAXBException
     * @throws TransformerConfigurationException
     * @throws TransformerException
     */
    public void sign(EPCISEventType event) throws ParserConfigurationException, SAXException, InvalidCanonicalizerException,
            CanonicalizationException, UnsupportedEncodingException, NoSuchAlgorithmException, XMLSignatureException,
            InvalidKeyException, SignatureException, NoSuchProviderException, KeyStoreException, FileNotFoundException,
            IOException, CertificateException, UnrecoverableKeyException, JAXBException, TransformerConfigurationException,
            TransformerException {
        String cForm = createCanonicalForm(event);
        log.debug(cForm);
        String signature = createECDSASignature(cForm);
        Utils.insertExtension(event, Constants.URN_IOTA, Constants.EXTENSION_SIGNATURE, signature);
        String cForm2 = createCanonicalForm(event);
        log.debug(cForm2);
    }

    /**
     * Compute and insert a signature in an EPCISEventType.
     *
     * @param event the event to be signed.
     * @param signerId the alias associated to the public key.
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws InvalidCanonicalizerException
     * @throws CanonicalizationException
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     * @throws XMLSignatureException
     * @throws InvalidKeyException
     * @throws SignatureException
     * @throws NoSuchProviderException
     * @throws KeyStoreException
     * @throws FileNotFoundException
     * @throws IOException
     * @throws CertificateException
     * @throws UnrecoverableKeyException
     * @throws JAXBException
     * @throws TransformerConfigurationException
     * @throws TransformerException
     */
    public void sign(EPCISEventType event, String signerId) throws ParserConfigurationException, SAXException, InvalidCanonicalizerException,
            CanonicalizationException, UnsupportedEncodingException, NoSuchAlgorithmException, XMLSignatureException,
            InvalidKeyException, SignatureException, NoSuchProviderException, KeyStoreException, FileNotFoundException, IOException,
            CertificateException, UnrecoverableKeyException, JAXBException, TransformerConfigurationException, TransformerException {
        this.signerId = signerId;
        String cForm = createCanonicalForm(event);
        log.debug(cForm);
        String signature = createECDSASignature(cForm);
        Utils.insertExtension(event, Constants.URN_IOTA, Constants.EXTENSION_SIGNATURE, signature);
        Utils.insertExtension(event, Constants.URN_IOTA, Constants.EXTENSION_SIGNER_ID, signerId);
        String cForm2 = createCanonicalForm(event);
        log.debug(cForm2);
    }

    /**
     * Verify a signature of an EPCISEventType.
     *
     * @param event the signed event.
     * @return <code>true</code> if the signature is correct.
     * @throws FileNotFoundException
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SignatureException
     * @throws java.security.cert.CertificateException
     * @throws NoSuchPaddingException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws InvalidCanonicalizerException
     * @throws CanonicalizationException
     * @throws KeyStoreException
     * @throws IOException
     * @throws JAXBException
     * @throws TransformerConfigurationException
     * @throws TransformerException
     */
    public boolean verify(EPCISEventType event) throws FileNotFoundException, CertificateException, NoSuchAlgorithmException,
            InvalidKeyException, SignatureException, java.security.cert.CertificateException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException, ParserConfigurationException, SAXException,
            InvalidCanonicalizerException, CanonicalizationException, KeyStoreException, IOException, JAXBException,
            TransformerConfigurationException, TransformerException {
        // get the the signature in event's extensions.
        this.signerId = getSignerId(event);
        deleteSignerId(event);
        String signature = getSignature(event);
        deleteSignature(event);
        String cForm = createCanonicalForm(event);
        log.debug(cForm);
        PublicKey publicKey = getPublicKey();
        Signature ecdsa = Signature.getInstance("SHA1withECDSA");
        ecdsa.initVerify(publicKey);
        byte[] cFormByte = cForm.getBytes("UTF-8");
        ecdsa.update(cFormByte);

        byte[] signatureByte = stringToByte(signature);
        return ecdsa.verify(signatureByte);
        // Get an SHA-1 message digest object and compute the plaintext digest
//        MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
        // To verify, start by decrypting the signature with the
        // RSA private key
//        Cipher cipher = Cipher.getInstance("EC");
//        cipher.init(Cipher.DECRYPT_MODE, getPublicKey());
        // the sigOffset permits the substraction of the sign byte that the BigInteger
        // class adds.
//        int sigOffset = signatureByte.length % 2;
//        byte[] newMD = cipher.doFinal(signatureByte, sigOffset, (signatureByte.length - sigOffset));

        // Then, recreate the message digest from the plaintext
        // to simulate what a recipient must do
//        int offset = cFormByte.length % 2;
//        messageDigest.reset();
//        messageDigest.update(cFormByte, offset, (cFormByte.length - offset));
//        byte[] oldMD = messageDigest.digest();

        // Verify that the two message digests match
//        int len = newMD.length;
//        if (len > oldMD.length) {
//            return false;
//        }
//        for (int i = 0; i < len; ++i) {
//            if (oldMD[i] != newMD[i]) {
//                return false;
//            }
//        }
//        return true;
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

    private String createCanonicalForm(EPCISEventType event) throws ParserConfigurationException, SAXException,
            InvalidCanonicalizerException, CanonicalizationException, JAXBException, IOException,
            TransformerConfigurationException, TransformerException {
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

    private String createCanonicalForm(AggregationEventType event) throws InvalidCanonicalizerException,
            ParserConfigurationException, SAXException, CanonicalizationException, JAXBException, IOException,
            TransformerConfigurationException, TransformerException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JAXBContext jc = JAXBContext.newInstance(AggregationEventType.class);
        Marshaller m = jc.createMarshaller();
        m.marshal(new JAXBElement<AggregationEventType>(new QName("", "AggregationEvent"),
                AggregationEventType.class, event), baos);
        byte[] nonCanonicalXML = baos.toByteArray();
        Node node = byteArrayToNode(nonCanonicalXML);
        byte[] canonicalXML = canonicalizeXML(node);
        return new String(canonicalXML);
    }

    private String createCanonicalForm(ObjectEventType event) throws InvalidCanonicalizerException,
            ParserConfigurationException, IOException, SAXException, CanonicalizationException, JAXBException,
            TransformerConfigurationException, TransformerException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JAXBContext jc = JAXBContext.newInstance(ObjectEventType.class);
        Marshaller m = jc.createMarshaller();
        m.marshal(new JAXBElement<ObjectEventType>(new QName("", "ObjectEvent"),
                ObjectEventType.class, event), baos);
        byte[] nonCanonicalXML = baos.toByteArray();
        Node node = byteArrayToNode(nonCanonicalXML);
        byte[] canonicalXML = canonicalizeXML(node);
        return new String(canonicalXML);
    }

    private String createCanonicalForm(QuantityEventType event) throws InvalidCanonicalizerException,
            ParserConfigurationException, SAXException, IOException, CanonicalizationException, JAXBException,
            TransformerConfigurationException, TransformerException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JAXBContext jc = JAXBContext.newInstance(QuantityEventType.class);
        Marshaller m = jc.createMarshaller();
        m.marshal(new JAXBElement<QuantityEventType>(new QName("", "QuantityEvent"),
                QuantityEventType.class, event), baos);
        byte[] nonCanonicalXML = baos.toByteArray();
        Node node = byteArrayToNode(nonCanonicalXML);
        byte[] canonicalXML = canonicalizeXML(node);
        return new String(canonicalXML);
    }

    private String createCanonicalForm(TransactionEventType event) throws InvalidCanonicalizerException,
            ParserConfigurationException, IOException, SAXException, CanonicalizationException, JAXBException,
            TransformerConfigurationException, TransformerException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JAXBContext jc = JAXBContext.newInstance(TransactionEventType.class);
        Marshaller m = jc.createMarshaller();
        m.marshal(new JAXBElement<TransactionEventType>(new QName("", "TransactionEvent"),
                TransactionEventType.class, event), baos);
        byte[] nonCanonicalXML = baos.toByteArray();
        Node node = byteArrayToNode(nonCanonicalXML);
        byte[] canonicalXML = canonicalizeXML(node);
        return new String(canonicalXML);
    }

    private byte[] canonicalizeXML(byte[] xml) throws InvalidCanonicalizerException, ParserConfigurationException,
            IOException, SAXException, CanonicalizationException {
        org.apache.xml.security.Init.init();
        Canonicalizer canonicalizer = Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
        return canonicalizer.canonicalize(xml);
    }

    private byte[] canonicalizeXML(Node xml) throws InvalidCanonicalizerException, ParserConfigurationException,
            IOException, SAXException, CanonicalizationException {
        org.apache.xml.security.Init.init();
        Canonicalizer canonicalizer = Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
        return canonicalizer.canonicalizeSubtree(xml);
    }

    private Node byteArrayToNode(byte[] xml) throws ParserConfigurationException, SAXException, IOException,
            TransformerConfigurationException, TransformerException {
        InputStream namespaceInput = SigMaFunctions.class.getClassLoader().getResourceAsStream("removeNamespaces.xml");
        Transformer xformer = TransformerFactory.newInstance().newTransformer(new StreamSource(namespaceInput));
        InputStream is = new ByteArrayInputStream(xml);
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        xformer.transform(new StreamSource(is), new StreamResult(byteOutput));
        InputStream transformedInput = new ByteArrayInputStream(byteOutput.toByteArray());
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        org.w3c.dom.Document doc = builder.parse(transformedInput);
        Element nroot = doc.getDocumentElement();
        Node node = nroot.getParentNode();
        return node;
    }

    private KeyStore getKeyStore(String filename) throws KeyStoreException, FileNotFoundException,
            IOException, NoSuchAlgorithmException, CertificateException, java.security.cert.CertificateException {
        KeyStore ks;
        char[] password;
        ks = KeyStore.getInstance("PKCS12");
        password = keyStorePassword.toCharArray();
        ks.load(new FileInputStream(filename), password);
        return ks;
    }

    private PrivateKey getPrivateKey() throws KeyStoreException, FileNotFoundException, IOException,
            NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException {
        PrivateKey privateKey = null;
        char[] password = keyStorePassword.toCharArray();
        KeyStore ks = getKeyStore(keyStoreFilePath);
        if (this.signerId == null || this.signerId.isEmpty()) {
            Enumeration<String> en = ks.aliases();
            while (en.hasMoreElements()) {
                String alias = en.nextElement();
                if (ks.isKeyEntry(alias)) {
                    this.signerId = alias;
                    break;
                }
            }
        }
        privateKey = (PrivateKey) ks.getKey(this.signerId, password);
        return privateKey;
    }

    private PublicKey getPublicKey() throws KeyStoreException, FileNotFoundException, IOException,
            NoSuchAlgorithmException, CertificateException {
        KeyStore ks = getKeyStore(keyStoreFilePath);
        if (this.signerId == null || this.signerId.isEmpty()) {
            Enumeration<String> en = ks.aliases();
            while (en.hasMoreElements()) {
                String alias = en.nextElement();
                if (ks.isKeyEntry(alias)) {
                    this.signerId = alias;
                    break;
                }
            }
        }
        PublicKey publicKey = ks.getCertificate(this.signerId).getPublicKey();
        return publicKey;
    }

    private String createECDSASignature(String event) throws KeyStoreException, FileNotFoundException, IOException,
            NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, InvalidKeyException, SignatureException {
        PrivateKey privateKey = getPrivateKey();
        byte[] plainText = event.getBytes("UTF-8");
        Signature signature = Signature.getInstance("SHA1withECDSA");
        signature.initSign(privateKey);
        signature.update(plainText);
        byte[] signatureBytes = signature.sign();
        return byteToString(signatureBytes);
    }

    private String createRSASignature(String event) throws UnsupportedEncodingException, KeyStoreException,
            FileNotFoundException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] plainText = event.getBytes("UTF-8");
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
        messageDigest.update(plainText);
        byte[] md = messageDigest.digest();
        KeyPair key = new KeyPair(getPublicKey(), getPrivateKey());
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key.getPrivate());
        byte[] cipherText = cipher.doFinal(md);
        return byteToString(cipherText);
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
            Element elem = (Element) object;
            if ((Constants.URN_IOTA.equals(elem.getNamespaceURI()) && Constants.EXTENSION_SIGNATURE.equals(elem.getLocalName()))) {
                signature = elem.getTextContent().toString();
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

        Element elem = null;
        for (Object object : extensions) {
            Element elemTmp = (Element) object;
            if ((Constants.URN_IOTA.equals(elemTmp.getNamespaceURI()) && Constants.EXTENSION_SIGNATURE.equals(elemTmp.getLocalName()))) {
                elem = elemTmp;
                break;
            }
        }
        if (elem != null) {
            extensions.remove(elem);
        }
    }

    private String getSignerId(EPCISEventType event) {
        String signId = "";
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
            Element elem = (Element) object;
            if ((Constants.URN_IOTA.equals(elem.getNamespaceURI()) && Constants.EXTENSION_SIGNER_ID.equals(elem.getLocalName()))) {
                signId = elem.getTextContent().toString();
            }
        }
        return signId;
    }

    private void deleteSignerId(EPCISEventType event) {
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

        Element elem = null;
        for (Object object : extensions) {
            Element elemTmp = (Element) object;
            if ((Constants.URN_IOTA.equals(elemTmp.getNamespaceURI()) && Constants.EXTENSION_SIGNER_ID.equals(elemTmp.getLocalName()))) {
                elem = elemTmp;
                break;
            }
        }
        if (elem != null) {
            extensions.remove(elem);
        }
    }

}
