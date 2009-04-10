package org.jboss.resteasy.test.providers.jaxb.collection;

import org.jboss.resteasy.annotations.providers.jaxb.Wrapped;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.test.BaseResourceTest;
import static org.jboss.resteasy.test.TestPortProvider.*;
import org.jboss.resteasy.util.GenericType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class CollectionTest2 extends BaseResourceTest
{
   @XmlRootElement
   @XmlAccessorType(XmlAccessType.FIELD)
   public static class Foo
   {
      @XmlAttribute
      private String test;

      public Foo()
      {
      }

      public Foo(String test)
      {
         this.test = test;
      }

      public String getTest()
      {
         return test;
      }

      public void setTest(String test)
      {
         this.test = test;
      }
   }

   @Path("/")
   public static class MyResource
   {
      @Path("/array")
      @Produces("application/xml")
      @Consumes("application/xml")
      @POST
      public Foo[] naked(Foo[] foo)
      {
         Assert.assertEquals(1, foo.length);
         Assert.assertEquals(foo[0].getTest(), "hello");
         return foo;
      }

      @Path("/list")
      @POST
      @Produces("application/xml")
      @Consumes("application/xml")
      @Wrapped(element = "list", namespace = "", prefix = "")
      public List<Foo> wrapped(@Wrapped(element = "list", namespace = "", prefix = "") List<Foo> list)
      {
         Assert.assertEquals(1, list.size());
         Assert.assertEquals(list.get(0).getTest(), "hello");
         return list;
      }


   }

   @Before
   public void setup()
   {
      addPerRequestResource(MyResource.class);
   }

   @Test
   public void testNakedArray() throws Exception
   {
      String xml = "<resteasy:collection xmlns:resteasy=\"http://jboss.org/resteasy\">"
              + "<foo test=\"hello\"/></resteasy:collection>";

      ClientRequest request = new ClientRequest(generateURL("/array"));
      request.body("application/xml", xml);
      ClientResponse<List<Foo>> response = request.post(new GenericType<List<Foo>>()
      {
      });
      List<Foo> list = response.getEntity();
      Assert.assertEquals(1, list.size());
      Assert.assertEquals(list.get(0).getTest(), "hello");

   }

   @Test
   public void testList() throws Exception
   {
      String xml = "<list>"
              + "<foo test=\"hello\"/></list>";

      ClientRequest request = new ClientRequest(generateURL("/list"));
      request.body("application/xml", xml);
      ClientResponse<Foo[]> response = request.post(new GenericType<Foo[]>()
      {
      });
      Foo[] list = response.getEntity();
      Assert.assertEquals(1, list.length);
      Assert.assertEquals(list[0].getTest(), "hello");

   }

   @Test
   public void testBadList() throws Exception
   {
      String xml = "<bad-list>"
              + "<foo test=\"hello\"/></bad-list>";

      ClientRequest request = new ClientRequest(generateURL("/list"));
      request.body("application/xml", xml);
      ClientResponse<Foo[]> response = request.post(new GenericType<Foo[]>()
      {
      });
      Assert.assertEquals(400, response.getStatus());

   }

}