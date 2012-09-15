/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2008-2012  Université de Caen Basse-Normandie, GREYC
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
/*
 * Derived from com.sun.xacml.support.finder.PolicyReader
 */
/*
 * Copyright 2006 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *   1. Redistribution of source code must retain the above copyright notice,
 *      this list of conditions and the following disclaimer.
 *
 *   2. Redistribution in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MICROSYSTEMS, INC. ("SUN")
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE,
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that this software is not designed or intended for use in
 * the design, construction, operation or maintenance of any nuclear facility.
 */
package fr.unicaen.iota.xacml.finder;

import com.sun.xacml.AbstractPolicy;
import com.sun.xacml.ParsingException;
import com.sun.xacml.finder.PolicyFinder;
import fr.unicaen.iota.xacml.policy.GroupPolicy;
import fr.unicaen.iota.xacml.policy.OwnerPolicies;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * @author alaurenc
 */
public class MyPolicyReader implements ErrorHandler {

    private static final Log log = LogFactory.getLog(MyPolicyReader.class);
    public static final String POLICY_SCHEMA_PROPERTY = "com.sun.xacml.PolicySchema";
    // the standard attribute for specifying the XML schema language
    private static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    // the standard identifier for the XML schema specification
    private static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
    // the standard attribute for specifying schema source
    private static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
    // the finder, which is used by PolicySets
    private PolicyFinder finder;
    // the builder used to create DOM documents
    private DocumentBuilder builder;

    public MyPolicyReader(PolicyFinder finder) {
        this(finder, null);
    }

    public MyPolicyReader(PolicyFinder finder, File schemaFile) {
        this.finder = finder;

        // create the factory
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringComments(true);
        factory.setNamespaceAware(true);

        // see if we want to schema-validate policies
        if (schemaFile == null) {
            factory.setValidating(false);
        } else {
            factory.setValidating(true);
            factory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
            factory.setAttribute(JAXP_SCHEMA_SOURCE, schemaFile);
        }

        // now use the factory to create the document builder
        try {
            builder = factory.newDocumentBuilder();
            builder.setErrorHandler((ErrorHandler) this);
        } catch (ParserConfigurationException pce) {
            throw new IllegalArgumentException("Filed to setup reader: "
                    + pce.toString());
        }
    }

    public synchronized AbstractPolicy readPolicy(File file) throws ParsingException {
        try {
            return handleDocument(builder.parse(file));
        } catch (IOException ioe) {
            throw new ParsingException("Failed to read the file", ioe);
        } catch (SAXException saxe) {
            throw new ParsingException("Failed to parse the file", saxe);
        }
    }

    public synchronized AbstractPolicy readPolicy(InputStream input) throws ParsingException {
        try {
            return handleDocument(builder.parse(input));
        } catch (IOException ioe) {
            throw new ParsingException("Failed to read the stream", ioe);
        } catch (SAXException saxe) {
            throw new ParsingException("Failed to parse the stream", saxe);
        }
    }

    public synchronized AbstractPolicy readPolicy(URL url) throws ParsingException {
        try {
            return readPolicy(url.openStream());
        } catch (IOException ioe) {
            throw new ParsingException("Failed to resolve the URL: "
                    + url.toString(), ioe);
        }
    }

    private AbstractPolicy handleDocument(Document doc) throws ParsingException {
        Element root = doc.getDocumentElement();
        String name = root.getTagName();

        if ("Policy".equals(name)) {
            return GroupPolicy.getInstance(root);
        } else if ("PolicySet".equals(name)) {
            return OwnerPolicies.getInstance(root, finder);
        } else {
            throw new ParsingException("Unknown root document type: " + name);
        }
    }

    @Override
    public void warning(SAXParseException exception) throws SAXException {
        if (log.isWarnEnabled()) {
            log.warn("Warning on line " + exception.getLineNumber()
                    + ": " + exception.getMessage());
        }
    }

    @Override
    public void error(SAXParseException exception) throws SAXException {
        if (log.isErrorEnabled()) {
            log.error("Error on line " + exception.getLineNumber()
                    + ": " + exception.getMessage() + " ... "
                    + "Policy will not be available");
        }
        throw new SAXException("error parsing policy");
    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
        if (log.isFatalEnabled()) {
            log.fatal("Fatal error on line " + exception.getLineNumber()
                    + ": " + exception.getMessage() + " ... "
                    + "Policy will not be available");
        }
        throw new SAXException("fatal error parsing policy");
    }
}
