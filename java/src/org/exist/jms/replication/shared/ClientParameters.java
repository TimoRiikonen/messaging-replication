/*
 *  eXist Open Source Native XML Database
 *  Copyright (C) 2012 The eXist Project
 *  http://exist-db.org
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 *  $Id$
 */
package org.exist.jms.replication.shared;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.naming.Context;
import org.apache.log4j.Logger;
import org.exist.jms.shared.Constants;

/**
 *
 * @author Dannes Wessels
 */
public abstract class ClientParameters {

    protected final static Logger LOG = Logger.getLogger(ClientParameters.class);
    
//    public static final String CONNECTION_FACTORY = Constants.CONNECTION_FACTORY;
//    public static final String DESTINATION = Constants.DESTINATION;  //"topic";
//    public static final String CLIENT_ID = Constants.CLIENT_ID; //"client-id";
//    public static final String PARAMETER_GROUPING = "..";
   
//    public static final String CLIENT_ID = Constants.CLIENT_ID; //"client-id";
//    public static final String PARAMETER_GROUPING = "..";
   
    protected String connectionFactory = null;
    protected String clientId = null;
    protected String topic = null;
    protected String initialContextFactory = null;
    protected String providerUrl = null;
    protected String connectionUsername = null;
    protected String connectionPassword = null;

    protected Properties props = new Properties();

    /**
     *  Get all JMS  settings from supplied parameters.
     * 
     * @param params Multi value parameters
     */
    public void setMultiValueParameters(Map<String, List<?>> params) {

        // Iterate over parameters
        for (Map.Entry<String, List<?>> entry : params.entrySet()) {

            // Get key, values
            String key = entry.getKey();
            List<?> values = entry.getValue();

            if (values != null && !values.isEmpty()) {
                // Only get first value
                Object value = values.get(0);
                if (value instanceof String) {
                    props.setProperty(key, (String) value);
                }
            }
        }

    }

    /**
     *  Get all JMS settings from supplied parameters.
     * 
     * @param params Single valued parameters.

     */
    public void setSingleValueParameters(final Map<String, List<? extends Object>> params) {

        for(final String key : params.keySet()) {
            final String value = getConfigurationValue(params, key);
            if(value != null){
                props.setProperty(key, value);
            }
        }

    }

    /**
     * Retrieve configuration value when available as String.
     *
     * @param params Map containing all parameter values
     * @param name Name of configuration item
     * @return Value of item, or NULL if not existent or existent and not a
     * String object
     */
    private static String getConfigurationValue(final Map<String, List<? extends Object>> params, String name) {

        String retVal = null;

        final List<? extends Object> value = params.get(name);
        if(value != null) {
            if(value.size() > 0) {
                retVal = value.get(0).toString();
            }
        }

        return retVal;
    }

    /**
     * Fill properties object with default values for
     * java.naming.factory.initial and java.naming.provider.url if not provided.
     * Defaults are set to match the Apache ActiveMQ message broker on
     * localhost.
     *
     */
    protected void fillActiveMQbrokerDefaults() {

        if (props.getProperty(Context.INITIAL_CONTEXT_FACTORY) == null) {
            String defaultValue = "org.apache.activemq.jndi.ActiveMQInitialContextFactory";
            props.setProperty(Context.INITIAL_CONTEXT_FACTORY, defaultValue);
            LOG.info(String.format("No value set for '%s', using default value '%s' which is suitable for activeMQ", Context.INITIAL_CONTEXT_FACTORY, defaultValue));
        }

        if (props.getProperty(Context.PROVIDER_URL) == null) {
            String defaultValue = "tcp://localhost:61616";
            props.setProperty(Context.PROVIDER_URL, defaultValue);
            LOG.info(String.format("No value set for '%s', using default value '%s' which is suitable for activeMQ", Context.PROVIDER_URL, defaultValue));
        }

    }
    
    /**
     * Retrieve initial context properties, e.g. {@link Context#INITIAL_CONTEXT_FACTORY} and
     * {@link Context#PROVIDER_URL}. Only properties with key starting with     * ".java" are added to the result.
     *
     * @return Initial context properties
     */
    public Properties getInitialContextProps(){
        Properties contextProps = new Properties();
        
        // Copy all properties that start with "java."
        for(String key : props.stringPropertyNames()){
            if(key.startsWith("java.")){
                contextProps.setProperty(key, props.getProperty(key));
            }
        }
                
        return contextProps;
    }
    
    abstract public void processParameters() throws TransportException, ClientParameterException;
    
    abstract public String getReport();
    
    public String getConnectionFactory() {
        return connectionFactory;
    }

    public String getClientId() {
        return clientId; 
    }

    public String getDestination() {
        return topic;
    }

    public Properties getProps() {
        return props;
    }
    
    public String getInitialContextFactory() {
        return initialContextFactory;
    }

    public String getProviderUrl() {
        return providerUrl;
    }
    
    public String getConnectionUsername() {
        return connectionUsername;
    }
    
    public String getConnectionPassword() {
        return connectionPassword;
    }
    
    public String getParameterValue(String key){
        return props.getProperty(key);
    }
    
//     public String getParameterValue(String group, String key){
//        return props.getProperty(group + PARAMETER_GROUPING + key);
//    }
    
}
