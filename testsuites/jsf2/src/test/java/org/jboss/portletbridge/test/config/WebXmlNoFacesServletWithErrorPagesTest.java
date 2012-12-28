/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.portletbridge.test.config;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.portal.api.PortalTest;
import org.jboss.arquillian.portal.api.PortalURL;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.portletbridge.test.TestDeployment;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.webapp30.WebAppDescriptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.net.URL;

import static org.junit.Assert.assertTrue;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
@RunWith(Arquillian.class)
@PortalTest
public class WebXmlNoFacesServletWithErrorPagesTest {

    @Deployment
    public static WebArchive createWebXmlNoFacesWithErrorPagesDeployment() {
        WebAppDescriptor webApp = buildPlainWebXml();
        webApp.createErrorPage()
                .exceptionType("java.lang.Exception")
                .location("/faces/error.xhtml")
                .up()
              .createErrorPage()
                .exceptionType("java.lang.ServletException")
                .location("/faces/error.xhtml")
                .up();

        return TestDeployment.createDeploymentWithFacesConfig()
                .addAsWebInfResource(new StringAsset(TestDeployment.createPortletXmlDescriptor("webXmlNoFacesServletWithErrorPages").exportAsString()), "portlet.xml")
                .addAsWebInfResource(new StringAsset(webApp.exportAsString()), "web.xml")
                .addAsWebResource("pages/config/header.xhtml", "home.xhtml");
    }

    @FindBy(id = "output")
    private WebElement output;

    @ArquillianResource
    @PortalURL
    URL portalURL;

    @Drone
    WebDriver browser;

    @Test
    @RunAsClient
    public void webXmlWithNoFacesServletButWithErrorPages() throws Exception {
        browser.get(portalURL.toString());

        assertTrue("Check that page contains output element", Graphene.element(output).isVisible().apply(browser));

        assertTrue("output text should contain: Portlet",
                Graphene.element(output).textEquals("Portlet").apply(browser));
    }

    private static WebAppDescriptor buildPlainWebXml() {
        WebAppDescriptor webApp = Descriptors.create(WebAppDescriptor.class);
        webApp.addDefaultNamespaces()
              .version("3.0");
        return webApp;
    }
}
