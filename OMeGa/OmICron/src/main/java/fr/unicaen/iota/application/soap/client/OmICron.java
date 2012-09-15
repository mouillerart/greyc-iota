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
package fr.unicaen.iota.application.soap.client;

import java.rmi.RemoteException;
import org.apache.axis2.AxisFault;

/**
 *
 */
public class OmICron {

    public static void main(String[] arg) throws AxisFault, RemoteException {
        String service = "http://localhost:8080/omega/services/IOTA_Service/";
        String epc = "urn:epc:id:sgtin:40000.00002.1298283877319";
        if (arg.length != 2) {
            System.err.println("Usage: OmICron <OMeGa Web Service URL> <EPC URN ID>");
            System.err.println();
            System.err.println("example: OmICron " + service + " " + epc);
            System.exit(-1);
        } else {
            service = arg[0];
            epc = arg[1];
        }
        IOTA_ServiceStub iota_ServiceStub = new IOTA_ServiceStub(service);
        System.out.println("Processing hello ...");
        IOTA_ServiceStub.HelloRequest hello = new IOTA_ServiceStub.HelloRequest();
        hello.setHelloRequest(new IOTA_ServiceStub.HelloRequestIn());
        IOTA_ServiceStub.HelloResponse resp = iota_ServiceStub.hello(hello);
        System.out.println("  Hello response: " + resp.getHelloResponse().getHello());
        System.out.println("Processing traceEPC ...");
        IOTA_ServiceStub.TraceEPCRequest traceEPCRequest = new IOTA_ServiceStub.TraceEPCRequest();
        IOTA_ServiceStub.TraceEPCRequestIn in = new IOTA_ServiceStub.TraceEPCRequestIn();
        in.setEpc(epc);
        traceEPCRequest.setTraceEPCRequest(in);
        IOTA_ServiceStub.TraceEPCResponse respTrac = iota_ServiceStub.traceEPC(traceEPCRequest);
        IOTA_ServiceStub.Event[] events = respTrac.getTraceEPCResponse().getEventList().getEvent();
        if (events == null) {
            System.out.println("  No events found.");
        } else {
            for (IOTA_ServiceStub.Event e : events) {
                System.out.println("  Event found: " + e.getBizStep() + " " + e.getDisposition());
            }
        }
        System.out.println("Bye.");
    }
}
