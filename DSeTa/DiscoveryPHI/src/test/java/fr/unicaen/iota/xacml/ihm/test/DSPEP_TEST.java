package fr.unicaen.iota.xacml.ihm.test;

import com.sun.xacml.ctx.RequestCtx;
import fr.unicaen.iota.xacml.ihm.Module;
import fr.unicaen.iota.xacml.pep.MethodNamesCapture;
import fr.unicaen.iota.xacml.pep.MethodNamesQuery;
import fr.unicaen.iota.xacml.pep.XACMLDSEvent;
import fr.unicaen.iota.xacml.request.EventRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Date;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is used to manage the DS access control policy
 */
public class DSPEP_TEST {

    private static final Log log = LogFactory.getLog(DSPEP_TEST.class);

    public static void main(String[] args) {
        XACMLDSEvent dSEvent = new XACMLDSEvent("epcistest", "bizstep", "urn:epc:id:sgtin:1.3.325", "object", new Date());
        int result = eventLookup("epcistest", dSEvent, "Query");
        log.trace(result);
    }

    /**
     * process access control policy for the Hello method.
     * @param userId      connected user
     * @param partnerId   corresponding partnerId
     * @param module      Query, Capture or Admin
     * @return
     */
    public static int hello(String userId, String partnerId, String module) {
        log.trace("process hello policy for user : " + userId);
        EventRequest eventRequest = new EventRequest(userId, "hello", partnerId, module);
        return processXACMLRequest(eventRequest);
    }

    /**
     * process access control policy for the partnerInfo method.
     * @param userId      connected user
     * @param partnerId   partner concerned by the request
     * @param module      Query, Capture or Admin
     * @return
     */
    public static int partnerInfo(String userId, String partnerId, String module) {
        log.trace("process partnerInfo policy for user : " + userId);
        EventRequest eventRequest = new EventRequest(userId, "partnerInfo", partnerId, module);
        return processXACMLRequest(eventRequest);
    }

    /**
     * process access control policy for the eventLookup method for each retrieved event retriefed.
     * @param userId    connected user
     * @param dsEvent   the event
     * @param module    Query, Capture or Admin
     * @return
     */
    public static int eventLookup(String userId, XACMLDSEvent dsEvent, String module) {
        log.trace("process eventLookup policy for user : " + userId);
        EventRequest eventRequest = new EventRequest(userId, "eventLookup", dsEvent, module);
        return processXACMLRequest(eventRequest);
    }

    /**
     * process access control policy for the eventInfo method.
     * @param userId    connected user
     * @param dsEvent
     * @param module
     * @return
     */
    public static int eventInfo(String userId, XACMLDSEvent dsEvent, String module) {
        log.trace("process eventInfo policy for user : " + userId);
        EventRequest eventRequest = new EventRequest(userId, "eventInfo", dsEvent, module);
        return processXACMLRequest(eventRequest);
    }

    public static int eventCreate(String userId, XACMLDSEvent dsEvent, String module) {
        log.trace("process eventCreate policy for user : " + userId);
        EventRequest eventRequest = new EventRequest(userId, "eventCreate", dsEvent, module);
        return processXACMLRequest(eventRequest);
    }

    public static int voidEvent(String userId, XACMLDSEvent dsEvent, String module) {
        log.trace("process voidEvent policy for user : " + userId);
        EventRequest eventRequest = new EventRequest(userId, "voidEvent", dsEvent, module);
        return processXACMLRequest(eventRequest);
    }

    public static int multipleEventCreate(String userId, XACMLDSEvent dsEvent, String module) {
        log.trace("process multipleEventCreate policy for user : " + userId);
        EventRequest eventRequest = new EventRequest(userId, "eventLookup", dsEvent, module);
        return processXACMLRequest(eventRequest);
    }

    public static int userLookup(String userId, String partner, String module) {
        log.trace("process userLookup policy for user : " + userId);
        EventRequest eventRequest = new EventRequest(userId, "userLookup", partner, module);
        return processXACMLRequest(eventRequest);
    }

    public static int userCreate(String userId, String partner, String module) {
        log.trace("process userCreate policy for user : " + userId);
        EventRequest eventRequest = new EventRequest(userId, "userCreate", partner, module);
        return processXACMLRequest(eventRequest);
    }

    public static int userInfo(String userId, String partner, String module) {
        log.trace("process userInfo policy for user : " + userId);
        EventRequest eventRequest = new EventRequest(userId, "userInfo", partner, module);
        return processXACMLRequest(eventRequest);
    }

    public static int userUpdate(String userId, String partner, String module) {
        log.trace("process userUpdate policy for user : " + userId);
        EventRequest eventRequest = new EventRequest(userId, "userUpdate", partner, module);
        return processXACMLRequest(eventRequest);
    }

    public static int userDelete(String userId, String partner, String module) {
        log.trace("process userDelete policy for user : " + userId);
        EventRequest eventRequest = new EventRequest(userId, "userDelete", partner, module);
        return processXACMLRequest(eventRequest);
    }

    public static int partnerUpdate(String userId, String partner, String module) {
        log.trace("process partnerUpdate policy for user : " + userId);
        EventRequest eventRequest = new EventRequest(userId, "partnerUpdate", partner, module);
        return processXACMLRequest(eventRequest);
    }

    public static int partnerLookup(String userId, String partner, String module) {
        log.trace("process partnerLookup policy for user : " + userId);
        EventRequest eventRequest = new EventRequest(userId, "partnerLookup", partner, module);
        return processXACMLRequest(eventRequest);
    }

    public static int partnerDelete(String userId, String partner, String module) {
        log.trace("process partnerDelete policy for user : " + userId);
        EventRequest eventRequest = new EventRequest(userId, "partnerDelete", partner, module);
        return processXACMLRequest(eventRequest);
    }

    public static int partnerCreate(String userId, String partner, String module) {
        log.trace("process partnerCreate policy for user : " + userId);
        EventRequest eventRequest = new EventRequest(userId, "partnerCreate", partner, module);
        return processXACMLRequest(eventRequest);
    }

    private static String sendXACMLRequest(RequestCtx xacmlReq) throws IOException {
        Socket socket = new Socket("localhost", 9999);
        BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        xacmlReq.encode(socket.getOutputStream());
        String response = socketReader.readLine();
        socket.close();
        return response;
    }

    private static int processXACMLRequest(EventRequest eventRequest) {
        String response = "DENY";
        try {
            response = sendXACMLRequest(eventRequest.createRequest());
        } catch (IOException ex) {
            log.error(null, ex);
        }
        return XACMLUtils.createXACMLResponse(response);
    }
}
