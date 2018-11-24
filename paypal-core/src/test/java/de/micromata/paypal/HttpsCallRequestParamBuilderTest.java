package de.micromata.paypal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpsCallRequestParamBuilderTest {

    @Test
    void builderTest() {
        String url = "http://www.acme.com/action";
        HttpsCallRequestParamBuilder pb = new HttpsCallRequestParamBuilder();
        assertEquals(url, pb.createUrl(url));
        assertEquals(url + "?a=b", pb.createUrl(url + "?a=b"));
        pb.add("test", "");
        assertEquals(url, pb.createUrl(url));
        assertEquals(url + "?a=b", pb.createUrl(url + "?a=b"));
        pb.add("test", null);
        assertEquals(url, pb.createUrl(url));
        assertEquals(url + "?a=b", pb.createUrl(url + "?a=b"));
        pb.add("test", "hurzel");
        assertEquals(url +"?test=hurzel", pb.createUrl(url));
        assertEquals(url +"?a=b&test=hurzel", pb.createUrl(url+ "?a=b"));
        pb.add("search", " schöner+Test");
        assertEquals(url + "?test=hurzel&search=+sch%C3%B6ner%2BTest", pb.createUrl(url));
        assertEquals(url + "?a=b&test=hurzel&search=+sch%C3%B6ner%2BTest", pb.createUrl(url+ "?a=b"));
    }
}