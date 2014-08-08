package com.github.ggeorgovassilis.springjsonmapper.tests;

import org.junit.runner.RunWith;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.ggeorgovassilis.springjsonmapper.services.BookService;
import com.github.ggeorgovassilis.springjsonmapper.services.Item;
import com.github.ggeorgovassilis.springjsonmapper.services.QueryResult;
import com.github.ggeorgovassilis.springjsonmapper.services.VolumeInfo;
import com.github.ggeorgovassilis.springjsonmapper.spring.SpringAnnotationsHttpJsonInvokerFactoryProxyBean;

import static org.junit.Assert.*;

/**
 * Integration test with the google books API using the
 * {@link SpringAnnotationsHttpJsonInvokerFactoryProxyBean}
 * 
 * @author george georgovassilis
 */
@RunWith(SpringJUnit4ClassRunner.class)
public abstract class AbstractGoogleBooksApiTest {

    @Autowired
    protected BookService bookService;

    @Test
    public void testFindBooksByTitle() throws Exception {

	QueryResult result = bookService
		.findBooksByTitle("\"Philosophiae naturalis principia mathematica\"");
	assertTrue(result.getItems().size() > 0);
	boolean found = false;
	for (Item item : result.getItems()) {
	    VolumeInfo info = item.getVolumeInfo();

	    found |= info != null
		    && info.getAuthors() != null
		    && !info.getAuthors().isEmpty()
		    && ("Philosophiae naturalis principia mathematica"
			    .equals(info.getTitle()) && "Isaac Newton"
			    .equals(info.getAuthors().get(0)));
	}
	assertTrue(found);
    }

    @Test
    public void testFindBooksById() {
	Item item = bookService.findBookById("3h9_GY8v-hgC");
	VolumeInfo info = item.getVolumeInfo();
	assertEquals("Philosophiae naturalis principia mathematica",
		info.getTitle());
	assertEquals("Isaac Newton", info.getAuthors().get(0));
    }

}
