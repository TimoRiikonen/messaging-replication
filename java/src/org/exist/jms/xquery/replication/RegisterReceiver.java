/*
 *  eXist Open Source Native XML Database
 *  Copyright (C) 2014 The eXist Project
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
 */
package org.exist.jms.xquery.replication;


import org.exist.jms.shared.Receiver;
import org.exist.dom.QName;
import org.exist.jms.shared.JmsConfiguration;
import org.exist.jms.shared.ReceiversManager;
import org.exist.jms.shared.Constants;
import org.exist.jms.replication.subscribe.ReplicationJmsListener;
import org.exist.jms.xquery.ReplicationModule;
import org.exist.xquery.BasicFunction;
import org.exist.xquery.Cardinality;
import org.exist.xquery.FunctionSignature;
import org.exist.xquery.XPathException;
import org.exist.xquery.XQueryContext;

import org.exist.xquery.functions.map.AbstractMapType;
import org.exist.xquery.value.*;

/**
 * Implementation of the replication:register() function.
 *
 * @author Dannes Wessels
 */
public class RegisterReceiver extends BasicFunction {

    public final static FunctionSignature signatures[] = {
        new FunctionSignature(
        new QName("register", ReplicationModule.NAMESPACE_URI, ReplicationModule.PREFIX),
        "Register function to receive JMS replication messages.", new SequenceType[]{
            new FunctionParameterSequenceType("jmsConfiguration", Type.MAP, Cardinality.EXACTLY_ONE, "JMS configuration"),},
        new FunctionReturnSequenceType(Type.STRING, Cardinality.ZERO_OR_ONE, "Receiver ID")
        ),};

    public RegisterReceiver(XQueryContext context, FunctionSignature signature) {
        super(context, signature);
    }

    @Override
    public Sequence eval(Sequence[] args, Sequence contextSequence) throws XPathException {

        // User must either be DBA or in the JMS group
        if (!context.getSubject().hasDbaRole() && !context.getSubject().hasGroup(Constants.JMS_GROUP)) {
            String txt = String.format("Permission denied, user '%s' must be a DBA or be in group '%s'",
                    context.getSubject().getName(), Constants.JMS_GROUP);
            XPathException ex = new XPathException(this, txt);
            LOG.error(txt, ex);
            throw ex;
        }

        try {
            // Get object that manages the receivers
            ReceiversManager manager = ReceiversManager.getInstance();

            // Get JMS configuration
            AbstractMapType configMap = (AbstractMapType) args[0].itemAt(0);
            JmsConfiguration config = new JmsConfiguration();
            config.loadConfiguration(configMap);

            // Setup listener, pass correct User object
            // get user via Broker for compatibility < existdb 2.2
            ReplicationJmsListener myListener = new ReplicationJmsListener(context.getBroker().getBrokerPool());

            // Create receiver
            Receiver receiver = new Receiver(config, myListener); // TODO check use .copyContext() ?

            // Register, initialize and start receiver
            manager.register(receiver);
            receiver.initialize();
            receiver.start();

            // Return identification
            return new IntegerValue(receiver.getId());

        } catch (XPathException ex) {
            LOG.error(ex.getMessage());
            ex.setLocation(this.line, this.column, this.getSource());
            throw ex;

        } catch (Throwable t) {
            LOG.error(t);
            XPathException ex = new XPathException(this, t);
            throw ex;
        }
    }
}
