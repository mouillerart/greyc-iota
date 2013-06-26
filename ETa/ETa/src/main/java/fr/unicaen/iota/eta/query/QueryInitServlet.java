/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2011-2013  Université de Caen Basse-Normandie, GREYC
 *  Copyright © 2011       Orange Labs
 *  Copyright © 2007       ETH Zurich
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
 * Derived from org.fosstrak.epcis.repository.query.QueryInitServlet
 */
package fr.unicaen.iota.eta.query;

import fr.unicaen.iota.eta.utils.Constants;
import fr.unicaen.iota.eta.soap.IDedEPCISServicePortType;
import fr.unicaen.iota.xi.client.EPCISPEP;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.sql.DataSource;
import javax.xml.ws.Endpoint;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.BusFactory;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.fosstrak.epcis.soap.EPCISServicePortType;

/**
 * This HttpServlet is used to initialize the QueryOperationsModule. It will
 * read the application's properties file from the class path, read the data
 * source from a JNDI name, and load the CXF Web service bus in order to set up
 * the QueryOperationsModule with the required values. <p> Note: this servlet is
 * only required if you do not wire the application with Spring! To use this
 * servlet, and bypass Spring, replace
 * <code>WEB-INF/web.xml</code> with
 * <code>WEB-INF/non-spring-web.xml</code>.
 */
public class QueryInitServlet extends CXFNonSpringServlet {

    private static final long serialVersionUID = -5839101192038037389L;
    private static final Log LOG = LogFactory.getLog(QueryInitServlet.class);
    private transient EPCISServicePortType service;
    private transient IDedEPCISServicePortType idedService;

    /**
     * {@inheritDoc}
     *
     * @see
     * org.apache.cxf.transport.servlet.CXFNonSpringServlet#loadBus(javax.servlet.ServletConfig)
     */
    @Override
    public void loadBus(ServletConfig servletConfig) {
        super.loadBus(servletConfig);
        BusFactory.setDefaultBus(getBus());
        if (LOG.isDebugEnabled()) {
            getBus().getInInterceptors().add(new LoggingInInterceptor());
            getBus().getOutInterceptors().add(new LoggingOutInterceptor());
            getBus().getOutFaultInterceptors().add(new LoggingOutInterceptor());
            getBus().getInFaultInterceptors().add(new LoggingInInterceptor());
        }
        setupQueryOperationsModules(servletConfig);

        LOG.debug("Publishing query operations module service at /query");
        Endpoint.publish("/query", service);
        service = null;

        LOG.debug("Publishing ided query operations module service at /ided_query");
        Endpoint.publish("/ided_query", idedService);
        idedService = null;
    }

    private void setupQueryOperationsModules(ServletConfig servletConfig) {
        DataSource dataSource = loadDataSource(Constants.JNDI_DATASOURCE_NAME);

        LOG.debug("Initializing query operations module");
        QueryOperationsModule module = new QueryOperationsModule();
        module.setServiceVersion(Constants.SERVICE_VERSION);
        module.setDataSource(dataSource);
        module.setServletContext(servletConfig.getServletContext());
        module.setBackend(new QueryOperationsBackendSQL());
        EPCISPEP epcisPEP = new EPCISPEP(Constants.XACML_URL, Constants.PKS_FILENAME, Constants.PKS_PASSWORD, Constants.TRUST_PKS_FILENAME, Constants.TRUST_PKS_PASSWORD);
        module.setQueryCheck(new QueryCheck(epcisPEP));

        LOG.debug("Initializing query operations web service");
        service = new QueryOperationsWebService(module);
        LOG.debug("Initializing ided query operations web service");
        idedService = new IDedQueryOperationsWebService(module);
    }

    /**
     * Loads the data source from the application context via JNDI.
     *
     * @param jndiName The name of the JNDI data source holding the connection
     * to the database.
     * @return The application DataSource instance.
     */
    private DataSource loadDataSource(String jndiName) {
        DataSource dataSource = null;
        try {
            Context ctx = new InitialContext();
            dataSource = (DataSource) ctx.lookup(jndiName);
            LOG.debug("Loaded data source via JNDI from " + jndiName);
        } catch (NamingException e) {
            LOG.error("Unable to load data source via JNDI from " + jndiName, e);
        }
        return dataSource;
    }
}
