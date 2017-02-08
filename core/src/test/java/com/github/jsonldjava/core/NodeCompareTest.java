package com.github.jsonldjava.core;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import com.github.jsonldjava.core.RDFDataset.IRI;
import com.github.jsonldjava.core.RDFDataset.BlankNode;
import com.github.jsonldjava.core.RDFDataset.Literal;
import com.github.jsonldjava.core.RDFDataset.Node;
import com.github.jsonldjava.core.RDFDataset.Quad;

import junit.framework.Assert;

public class NodeCompareTest {

    @Test
    public void ordered() throws Exception {
        List<Node> expected = Arrays.asList(
                // While this order might not particularly make sense, it
                // is at least documented
                
                new Literal("1", JsonLdConsts.XSD_INTEGER, null),
                new Literal("10", JsonLdConsts.XSD_INTEGER, null),
                new Literal("2", JsonLdConsts.XSD_INTEGER, null), // still ordered by string value
                
                new Literal("a", JsonLdConsts.RDF_LANGSTRING, "en"), 
                new Literal("a", JsonLdConsts.RDF_LANGSTRING, "fr"), 
                new Literal("a", null, null), // equivalent to xsd:string
                new Literal("b", JsonLdConsts.XSD_STRING, null),
                new Literal("false", JsonLdConsts.XSD_BOOLEAN, null),
                new Literal("true", JsonLdConsts.XSD_BOOLEAN, null),

                new Literal("x", JsonLdConsts.XSD_STRING, null),

                new Literal("z", JsonLdConsts.RDF_LANGSTRING, "en"),
                new Literal("z", JsonLdConsts.RDF_LANGSTRING, "fr"),                
                new Literal("z", null, null),

                new BlankNode("a"),
                new BlankNode("f"),
                new BlankNode("z"),

                new IRI("http://example.com/ex1"),
                new IRI("http://example.com/ex2"),
                new IRI("http://example.org/ex"),
                new IRI("https://example.net/")
        );
        
        List<Node> shuffled = new ArrayList<>(expected);
        Random rand = new Random(1337); // fixed seed
        Collections.shuffle(shuffled, rand);
        //System.out.println("Shuffled:");
        //shuffled.stream().forEach(System.out::println);
        assertNotEquals(expected, shuffled);

        Collections.sort(shuffled);
        List<Node> sorted = shuffled;
        //System.out.println("Now sorted:");
        //sorted.stream().forEach(System.out::println);
        // Not so useful output from this
        //        assertEquals(expected, sorted);
        // so we'll instead do:
        for (int i=0; i<expected.size(); i++) {
            assertEquals("Wrong sort order at position " + i, 
                    expected.get(i), sorted.get(i));
        }
    }
    
    @Test
    public void literalSameValue() throws Exception {
        Literal l1 = new Literal("Same", null, null);
        Literal l2 = new Literal("Same", null, null);
        assertEquals(l1, l2);
        assertEquals(0, l1.compareTo(l2));
    }

    @Test
    public void literalDifferentValue() throws Exception {
        Literal l1 = new Literal("Same", null, null);
        Literal l2 = new Literal("Different", null, null);
        assertNotEquals(l1, l2);
        assertNotEquals(0, l1.compareTo(l2));
    }

    @Test
    public void literalSameValueSameLang() throws Exception {
        Literal l1 = new Literal("Same", JsonLdConsts.RDF_LANGSTRING, "en");
        Literal l2 = new Literal("Same", JsonLdConsts.RDF_LANGSTRING, "en");
        assertEquals(l1, l2);
        assertEquals(0, l1.compareTo(l2));
    }
    
    @Test
    public void literalDifferentValueSameLang() throws Exception {
        Literal l1 = new Literal("Same", JsonLdConsts.RDF_LANGSTRING, "en");
        Literal l2 = new Literal("Different", JsonLdConsts.RDF_LANGSTRING, "en");
        assertNotEquals(l1, l2);
        assertNotEquals(0, l1.compareTo(l2));
    }

    @Test
    public void literalSameValueDifferentLang() throws Exception {
        Literal l1 = new Literal("Same", JsonLdConsts.RDF_LANGSTRING, "en");
        Literal l2 = new Literal("Same", JsonLdConsts.RDF_LANGSTRING, "no");
        assertNotEquals(l1, l2);
        assertNotEquals(0, l1.compareTo(l2));
    }    

    @Test
    public void literalSameValueLangNull() throws Exception {
        Literal l1 = new Literal("Same", JsonLdConsts.RDF_LANGSTRING, "en");
        Literal l2 = new Literal("Same", JsonLdConsts.RDF_LANGSTRING, null);
        assertNotEquals(l1, l2);
        assertNotEquals(0, l1.compareTo(l2));
        assertNotEquals(0, l2.compareTo(l1));
    }    
    
    
    @Test
    public void literalSameValueSameType() throws Exception {
        Literal l1 = new Literal("1", JsonLdConsts.XSD_INTEGER, null);
        Literal l2 = new Literal("1", JsonLdConsts.XSD_INTEGER, null);
        assertEquals(l1, l2);
        assertEquals(0, l1.compareTo(l2));
    }

    @Test
    public void literalSameValueSameTypeNull() throws Exception {
        Literal l1 = new Literal("1", JsonLdConsts.XSD_STRING, null);
        Literal l2 = new Literal("1", null, null);
        assertEquals(l1, l2);
        assertEquals(0, l1.compareTo(l2));
    }

    
    @Test
    public void literalSameValueDifferentType() throws Exception {
        Literal l1 = new Literal("1", JsonLdConsts.XSD_INTEGER, null);
        Literal l2 = new Literal("1", JsonLdConsts.XSD_STRING, null);
        assertNotEquals(l1, l2);
        assertNotEquals(0, l1.compareTo(l2));
    }
    

    
    @Test
    public void literalsInDataset() throws Exception {
        RDFDataset dataset = new RDFDataset();
        dataset.addQuad("http://example.com/p", "http://example.com/p", "Same", null, null, "http://example.com/g1");
        dataset.addQuad("http://example.com/p", "http://example.com/p", "Different", null, null, "http://example.com/g1");
        List<Quad> quads = dataset.getQuads("http://example.com/g1");
        Quad q1 = quads.get(0);
        Quad q2 = quads.get(1);
        assertNotEquals(q1, q2);
        assertNotEquals(0, q1.compareTo(q2));
        assertNotEquals(0, q1.getObject().compareTo(q2.getObject()));
    }

    @Test
    public void iriDifferentLiteral() throws Exception {
        Node iri = new IRI("http://example.com/");
        Node literal = new Literal("http://example.com/", null, null);
        assertNotEquals(iri, literal);
        assertNotEquals(0, iri.compareTo(literal));
        assertNotEquals(0, literal.compareTo(iri));
    }

    @Test
    public void iriDifferentNull() throws Exception {
        Node iri = new IRI("http://example.com/");
        assertNotEquals(0, iri.compareTo(null));
    }

    @Test
    public void literalDifferentNull() throws Exception {
        Node literal = new Literal("hello", null, null);
        assertNotEquals(0, literal.compareTo(null));
    }    
    
    @Test
    public void iriDifferentIri() throws Exception {
        Node iri = new IRI("http://example.com/");
        Node other = new IRI("http://example.com/other");
        assertNotEquals(iri, other);
        assertNotEquals(0, iri.compareTo(other));
    }
    
    @Test
    public void iriSameIri() throws Exception {
        Node iri = new IRI("http://example.com/same");
        Node same = new IRI("http://example.com/same");
        assertEquals(iri, same);
        assertEquals(0, iri.compareTo(same));
    }
        
    @Test
    public void iriDifferentBlankNode() throws Exception {
        // We'll use a relative IRI to avoid :-issues
        Node iri = new IRI("b1");
        Node bnode = new BlankNode("b1");
        assertNotEquals(iri, bnode);
        assertNotEquals(bnode, iri);
        assertNotEquals(0, iri.compareTo(bnode));
        assertNotEquals(0, bnode.compareTo(iri));
    }

    @Test
    public void literalDifferentBlankNode() throws Exception {
        // We'll use a relative IRI to avoid :-issues
        Node literal = new Literal("b1", null, null);
        Node bnode = new BlankNode("b1");
        assertNotEquals(literal, bnode);
        assertNotEquals(bnode, literal);
        assertNotEquals(0, literal.compareTo(bnode));
        assertNotEquals(0, bnode.compareTo(literal));

    }
    
    
}
