package fr.unicaen.iota.discovery.client;

import java.rmi.RemoteException;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import fr.unicaen.iota.discovery.client.model.Event;
import fr.unicaen.iota.discovery.client.model.EventInfo;
import fr.unicaen.iota.discovery.client.model.PartnerId;
import fr.unicaen.iota.discovery.client.model.Service;
import fr.unicaen.iota.discovery.client.model.Session;
import fr.unicaen.iota.discovery.client.model.UserInfo;
import fr.unicaen.iota.discovery.client.model.UserId;
import fr.unicaen.iota.discovery.client.util.EnhancedProtocolException;
import org.apache.axis2.databinding.types.URI;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
public class DsClientTest {

    private static final Log log = LogFactory.getLog(DsClientTest.class);

    public DsClientTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        try {
            String userid = "jerome";
            String userpass = "jerome";
            Session s = instance.userLogin(TestControler.sessionId, userid, userpass);
            TestControler.sessionId = s.getSessionId();
        } catch (RemoteException ex) {
            log.fatal(null, ex);
            System.exit(-1);
        } catch (EnhancedProtocolException ex) {
            log.fatal(null, ex);
            System.exit(-1);
        }
    }

    @After
    public void tearDown() {
        try {
            instance.userLogout(TestControler.sessionId);
        } catch (RemoteException ex) {
            log.fatal(null, ex);
            System.exit(-1);
        } catch (EnhancedProtocolException ex) {
            log.fatal(null, ex);
            System.exit(-1);
        }
    }
    /**
     * Test of hello method, of class DsClient.
     */
    DsClient instance = new DsClient("http://localhost:8080/dws/services/ESDS_Service/");
    String defaultSessionId = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"; // must be 32 chars
    String userID = "testU2";                               // must be between 2 and 16 chars
    String partnerId = "testP3";                            // must be between 6 and 16 chars
    String userPawword = "aaaaaa";                         // must be between 6 and 16 chars

    @Test
    public void testHello() throws Exception {
        log.trace("hello -> ");
        try {
            String result = instance.hello(TestControler.sessionId);
            assertTrue(result.contains("urn:epc:id:gsrn"));
            log.trace("[OK]");
        } catch (EnhancedProtocolException ex) {
            log.error(ex.getResultCode() + " : " + ex.getMessage(), ex);
        }
    }

    /**
     * Test of userLookup method, of class DsClient.
     */
    @Test
    public void testUserLookup() throws Exception {
        log.trace("userLookup -> ");
        try {
            List<UserId> result = instance.userLookup(TestControler.sessionId, userID);
            assertTrue(result != null);
            log.trace("[OK]");
        } catch (EnhancedProtocolException ex) {
            log.error(ex.getResultCode() + " : " + ex.getMessage(), ex);
        }
    }

    /**
     * Test of userCreate method, of class DsClient.
     */
    @Test
    public void testUserCreate() throws Exception {
        log.trace("userCreate -> ");
        try {
            int ttl = 10;
            int result = instance.userCreate(TestControler.sessionId, partnerId, userID, userPawword, ttl);
            assertTrue(result > 0);
            log.trace("[OK]");
        } catch (EnhancedProtocolException ex) {
            log.error(ex.getResultCode() + " : " + ex.getMessage(), ex);
        }
    }

    /**
     * Test of userInfo method, of class DsClient.
     */
    @Test
    public void testUserInfo() throws Exception {
        log.trace("userInfo -> ");
        try {
            UserInfo userInfo = instance.userInfo(TestControler.sessionId, userID);
            log.trace("[OK]");
        } catch (EnhancedProtocolException ex) {
            log.error(ex.getResultCode() + " : " + ex.getMessage(), ex);
        }
    }

    /**
     * Test of userUpdate method, of class DsClient.
     */
    @Test
    public void testUserUpdate() throws Exception {
        log.trace("userUpdate -> ");
        try {
            int userUID = 7;
            int ttl = 0;
            instance.userUpdate(TestControler.sessionId, userUID, partnerId, userID, userPawword, ttl);
            log.trace("[OK]");
        } catch (EnhancedProtocolException ex) {
            log.trace(ex.getResultCode() + " : " + ex.getMessage(), ex);
        }
    }

    /**
     * Test of userDelete method, of class DsClient.
     */
    @Test
    public void testUserDelete() throws Exception {
        log.trace("userDelete -> ");
        try {
            instance.userDelete(TestControler.sessionId, userID);
            log.trace("[OK]");
        } catch (EnhancedProtocolException ex) {
            log.error(ex.getResultCode() + " : " + ex.getMessage(), ex);
        }
    }

    /**
     * Test of userLogin method, of class DsClient.
     */
    @Test
    public void testUserLogin() throws Exception {
        log.trace("userLogin -> ");
        try {
            instance.userLogin(defaultSessionId, userID + "___", userPawword);
            log.trace("[OK]");
        } catch (EnhancedProtocolException ex) {
            log.error(ex.getResultCode() + " : " + ex.getMessage(), ex);
        }
    }

    /**
     * Test of userLogout method, of class DsClient.
     */
    @Test
    public void testUserLogout() throws Exception {
        log.trace("userLogout -> ");
        try {
            instance.userLogout(defaultSessionId);
            log.trace("[OK]");
        } catch (EnhancedProtocolException ex) {
            log.error(ex.getResultCode() + " : " + ex.getMessage(), ex);
        }
    }

    /**
     * Test of partnerLookup method, of class DsClient.
     */
    @Test
    public void testPartnerLookup() throws Exception {
        log.trace("partnerLookup -> ");
        try {
            Collection<PartnerId> pList = instance.partnerLookup(TestControler.sessionId, partnerId);
            log.trace("[OK]");
        } catch (EnhancedProtocolException ex) {
            log.error(ex.getResultCode() + " : " + ex.getMessage(), ex);
        }
    }

    /**
     * Test of partnerCreate method, of class DsClient.
     */
    @Test
    public void testPartnerCreate() throws Exception {
        log.trace("partnerCreate -> ");
        try {
            List<Service> services = new ArrayList<Service>();
            Service service = new Service("epcis", "epcis", new URI("http://localhost/test"));
            services.add(service);
            instance.partnerCreate(TestControler.sessionId, partnerId, services);
            log.trace("[OK]");
        } catch (EnhancedProtocolException ex) {
            log.error(ex.getResultCode() + " : " + ex.getMessage(), ex);
        }
    }

    /**
     * Test of partnerInfo method, of class DsClient.
     */
    @Test
    public void testPartnerInfo() throws Exception {
        log.trace("partnerInfo -> ");
        try {
            instance.partnerInfo(TestControler.sessionId, partnerId);
            log.trace("[OK]");
        } catch (EnhancedProtocolException ex) {
            log.error(ex.getResultCode() + " : " + ex.getMessage(), ex);
        }
    }

    /**
     * Test of partnerUpdate method, of class DsClient.
     */
    @Test
    public void testPartnerUpdate() throws Exception {
        log.trace("partnerUpdate -> ");
        try {
            int partnerUID = 1;
            List<Service> services = new ArrayList<Service>();
            Service service = new Service("epcis", "epcis", new URI("http://localhost/test"));
            services.add(service);
            instance.partnerUpdate(TestControler.sessionId, partnerUID, partnerId, services);
            log.trace("[OK]");
        } catch (EnhancedProtocolException ex) {
            log.error(ex.getResultCode() + " : " + ex.getMessage(), ex);
        }
    }

    /**
     * Test of partnerDelete method, of class DsClient.
     */
    @Test
    public void testPartnerDelete() throws Exception {
        log.trace("partnerDelete -> ");
        try {
            instance.partnerDelete(TestControler.sessionId, partnerId);
            log.trace("[OK]");
        } catch (EnhancedProtocolException ex) {
            log.error(ex.getResultCode() + " : " + ex.getMessage(), ex);
        }
    }

    /**
     * Test of eventLookup method, of class DsClient.
     */
    @Test
    public void testEventLookup() throws Exception {
        log.trace("eventLookup -> ");
        try {
            String objectId = "opac-id-2";
            Calendar start = null;
            Calendar end = null;
            String BizStep = null;
            instance.eventLookup(TestControler.sessionId, objectId, start, end, BizStep);
            log.trace("[OK]");
        } catch (EnhancedProtocolException ex) {
            log.error(ex.getResultCode() + " : " + ex.getMessage(), ex);
        }
    }

    /**
     * Test of eventCreate method, of class DsClient.
     */
    @Test
    public void testEventCreate() throws Exception {
        log.trace("eventCreate -> ");
        try {
            String objectId = "opac-id2";
            String bizStep = "opac-bizStep";
            String eventClass = "object";
            Calendar sourceTimeStamp = new GregorianCalendar();
            int ttl = 30;
            List<String> serviceIds = new ArrayList<String>();
            serviceIds.add("epcis");
            int priority = 0;
            instance.eventCreate(TestControler.sessionId, partnerId, objectId, bizStep, eventClass,
                    sourceTimeStamp, ttl, serviceIds, priority, new HashMap<String, String>());
            log.trace("[OK]");
        } catch (EnhancedProtocolException ex) {
            log.error(ex.getResultCode() + " : " + ex.getMessage(), ex);
        }
    }

    /**
     * Test of eventInfo method, of class DsClient.
     */
    @Test
    public void testEventInfo() throws Exception {
        log.trace("eventInfo -> ");
        try {
            int eventUID = 1;
            instance.eventInfo(TestControler.sessionId, eventUID);
            log.trace("[OK]");
        } catch (EnhancedProtocolException ex) {
            log.error(ex.getResultCode() + " : " + ex.getMessage(), ex);
        }
    }

    /**
     * Test of multipleEventCreate method, of class DsClient.
     */
    @Test
    public void testMultipleEventCreate() throws Exception {
        log.trace("multipleEventCreate -> ");
        try {
            List<EventInfo> eventList = new ArrayList<EventInfo>();
            Event e1 = new Event(1, "opac-id-1", partnerId, userID, "test", "object", "object",
                    Calendar.getInstance(), Calendar.getInstance(), new HashMap<String, String>());
            Event e2 = new Event(1, "opac-id-2", partnerId, userID, "test", "object", "object",
                    Calendar.getInstance(), Calendar.getInstance(), new HashMap<String, String>());
            EventInfo eventInfo1 = new EventInfo(e1, 1, 10);
            EventInfo eventInfo2 = new EventInfo(e2, 1, 10);
            eventList.add(eventInfo1);
            eventList.add(eventInfo2);
            instance.multipleEventCreate(TestControler.sessionId, partnerId, eventList);
            log.trace("[OK]");
        } catch (EnhancedProtocolException ex) {
            log.error(ex.getResultCode() + " : " + ex.getMessage(), ex);
        }
    }
}
