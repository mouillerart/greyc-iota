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
package fr.unicaen.iota.utils;

import fr.unicaen.iota.xacml.ihm.Module;
import fr.unicaen.iota.xacml.pep.MethodNamesAdmin;
import fr.unicaen.iota.xacml.pep.MethodNamesCapture;
import fr.unicaen.iota.xacml.pep.MethodNamesQuery;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public final class HTMLUtilities {

    private HTMLUtilities() {
    }

    public static String createPartnerSelect() {
        StringBuilder res = new StringBuilder();
        res.append("<select class=\"partnerSelector\" id=\"groupPartnerName\">");
        for (String partnerId : listAllPartners()) {
            res.append("<option label=\"partner: ");
            res.append(partnerId);
            res.append("\" value=\"");
            res.append(partnerId);
            res.append("\">");
            res.append(partnerId);
            res.append("</option>");
        }
        res.append("</select>");
        return res.toString();
    }

    public static String createMethodSelect(Module m) {
        StringBuilder res = new StringBuilder();

        switch (m) {
            case adminModule:
                res.append("<select class=\"methodSelector\" id=\"methodNameAdmin\">");
                for (Method method1 : MethodNamesAdmin.class.getMethods()) {
                    res.append("<option label=\"method: ");
                    res.append(method1.getName());
                    res.append("\" value=\"");
                    res.append(method1.getName());
                    res.append("\">");
                    res.append(method1.getName());
                    res.append("</option>");
                }
                break;
            case captureModule:
                res.append("<select class=\"methodSelector\" id=\"methodNameCapture\">");
                for (Method method2 : MethodNamesCapture.class.getMethods()) {
                    res.append("<option label=\"method: ");
                    res.append(method2.getName());
                    res.append("\" value=\"");
                    res.append(method2.getName());
                    res.append("\">");
                    res.append(method2.getName());
                    res.append("</option>");
                }
                break;
            case queryModule:
                res.append("<select class=\"methodSelector\" id=\"methodNameQuery\">");
                for (Method method3 : MethodNamesQuery.class.getMethods()) {
                    res.append("<option label=\"method: ");
                    res.append(method3.getName());
                    res.append("\" value=\"");
                    res.append(method3.getName());
                    res.append("\">");
                    res.append(method3.getName());
                    res.append("</option>");
                }
                break;
        }
        res.append("</select>");
        return res.toString();
    }
    private static final String[] SERVICES = {"ds", "epcis", "html", "ws", "xmlrpc"};

    public static String createSelectServiceType(String serviceType, String idSuffix) {
        StringBuilder res = new StringBuilder();
        res.append("<select id=\"serviceType");
        res.append(idSuffix);
        res.append("\" >");
        for (String s : SERVICES) {
            res.append("<option ");
            res.append(serviceType.equals(s) ? "selected" : "");
            res.append(" value=\"");
            res.append(s);
            res.append("\">");
            res.append(s);
            res.append("</option>");
        }
        res.append("</select>");
        return res.toString();
    }
    private static final String[] TYPES = {"object", "aggregation", "quantity", "transaction"};

    public static String createSelectEventClassFilter() {
        StringBuilder res = new StringBuilder();
        res.append("<select id=\"epcClassFilterName\" >");
        for (String s : TYPES) {
            res.append("<option value=\"");
            res.append(s);
            res.append("\">");
            res.append(s);
            res.append("</option>");
        }
        res.append("</select>");
        return res.toString();
    }

    private static Iterable<String> listAllPartners() {

        // TODO: TODOTODO !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        List<String> partnerIdList = new ArrayList<String>();
        partnerIdList.add("partner1");
        partnerIdList.add("partner2");
        partnerIdList.add("partner3");
        partnerIdList.add("partner4");
        partnerIdList.add("partner5");

        return partnerIdList;
    }

    private static Iterable<String> listAllMethods() {

        // TODO: TODOTODO !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        List<String> methodList = new ArrayList<String>();
        methodList.add("m1");
        methodList.add("m2");
        methodList.add("m3");
        methodList.add("m4");
        methodList.add("m5");

        return methodList;
    }
}
