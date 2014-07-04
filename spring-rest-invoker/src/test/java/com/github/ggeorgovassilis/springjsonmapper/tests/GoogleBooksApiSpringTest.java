package com.github.ggeorgovassilis.springjsonmapper.tests;

import org.junit.runner.RunWith;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.ggeorgovassilis.springjsonmapper.spring.SpringAnnotationsHttpJsonInvokerFactoryProxyBean;
import com.github.ggeorgovassilis.springjsonmapper.support.BookServiceSpring;
import com.github.ggeorgovassilis.springjsonmapper.support.Item;
import com.github.ggeorgovassilis.springjsonmapper.support.QueryResult;
import com.github.ggeorgovassilis.springjsonmapper.support.VolumeInfo;

import static org.junit.Assert.*;

/**
 * Integration test with the google books API using the {@link SpringAnnotationsHttpJsonInvokerFactoryProxyBean}
 * @author george georgovassilis
 */
@RunWith(value = SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-context-googlebooks.xml")
public class GoogleBooksApiSpringTest {

	@Autowired
	BookServiceSpring bookService;

	@Test
	public void testFindBooksByTitle() {
		QueryResult result = bookService.findBooksByTitle("\"Philosophiae naturalis principia mathematica\"");
		assertTrue(result.getItems().size() > 0);
		boolean found = false;
		for (Item item : result.getItems()) {
			VolumeInfo info = item.getVolumeInfo();
			found |= ("Philosophiae naturalis principia mathematica".equals(info.getTitle()) && "Isaac Newton".equals(info.getAuthors()
					.get(0)));
		}
		assertTrue(found);
	}

	@Test
	public void testFindBooksByid() {
		Item item = bookService.findBookById("3h9_GY8v-hgC");
		VolumeInfo info = item.getVolumeInfo();
		assertEquals("Philosophiae naturalis principia mathematica", info.getTitle());
		assertEquals("Isaac Newton", info.getAuthors().get(0));
	}

}
