package com.csiro.teststuff;

import com.bea.wlevs.management.configuration.EPLProcessorMBean;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import javax.management.JMException;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.remote.*;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import com.bea.wlevs.management.configuration.CQLProcessorMBean;
import javax.management.MalformedObjectNameException;
import javax.naming.Context;

public class ProcessorControlBean {

    private CQLProcessorMBean processor;

    public static void main(String[] args) throws Exception {

        ProcessorControlBean c = new ProcessorControlBean();
        c.afterConfig();
    }

    public void afterConfig() throws Exception {
        initConnection("54.149.122.214", 9002, "wlevs", new char[]{'w', 'l', 'e', 'v', 's'});

        Map<String, String> rules = processor.getAllRules();

        for (String key : rules.keySet()) {
            System.out.println("rule id-" + key + " rule-" + rules.get(key));
        }
    }

    public void setProcessor(CQLProcessorMBean processor) {
        this.processor = processor;
    }

    public static void initConnection(String hostname, int port, String username, char[] password)
            throws IOException, MalformedURLException, MalformedObjectNameException, JMException {

        Map<String, Object> env = new HashMap<String, Object>();

        env.put(JMXConnector.CREDENTIALS, new Serializable[]{username, password});
        env.put("jmx.remote.authenticator", "com.bea.core.jmx.server.CEAuthenticator");
        System.setProperty("jmx.remote.authenticator", "com.bea.core.jmx.server.CEAuthenticator");
          env.put(JMXConnectorFactory.DEFAULT_CLASS_LOADER,
                              com.bea.core.jmx.remote.provider.msarmi.ServerProvider.class
                              .getClassLoader());
          env.put(JMXConnectorFactory.PROTOCOL_PROVIDER_CLASS_LOADER,
                              com.bea.core.jmx.remote.provider.msarmi.ServerProvider.class
                              .getClassLoader());

          env.put(JMXConnectorFactory.PROTOCOL_PROVIDER_PACKAGES,
                              "com.bea.core.jmx.remote.provider");

          System.setProperty("mx4j.remote.resolver.pkgs",
                              "com.bea.core.jmx.remote.resolver");
        env.put("jmx.remote.protocol.provider.pkgs", "com.bea.core.jmx.remote.provider");
        env.put("mx4j.remote.resolver.pkgs", "com.bea.core.jmx.remote.resolver");
        env.put("java.naming.factory.initial", "com.bea.core.jndi.context.ContextFactory");  

        JMXServiceURL serviceUrl = new JMXServiceURL(
                "MSARMI", hostname, port, "/jndi/jmxconnector"
        );
        System.out.println("Service: " + serviceUrl.toString());

        JMXConnector connector = JMXConnectorFactory.connect(serviceUrl, env);

        MBeanServerConnection connection = connector.getMBeanServerConnection();
        ObjectName eplName
                = ObjectName.getInstance("com.bea.wlevs:Name=CarSegStrQueryProcessor,Type=CQLProcessor,Application=LinearRoad");

        CQLProcessorMBean eplMBean = (CQLProcessorMBean) MBeanServerInvocationHandler.newProxyInstance(
                connection,
                ObjectName.getInstance(eplName),
                CQLProcessorMBean.class,
                true);

        Map<String, String> rules = eplMBean.getAllRules();
        for (String key: rules.keySet()) {
            System.out.println("rule id-" + key + " rule-" + rules.get(key));
        }
    }


}

