package com.github.ggeorgovassilis.springjsonmapper;

import javax.annotation.Resource;

import org.junit.runner.RunWith;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;

@RunWith(value=SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-context.xml")
public class SpringJsonMapperTest {

    @Resource(name="RemoteBookService")
    BookService bookService;
    
    @Test
    public void testFindBooksByTitle(){
	QueryResult result = bookService.findBooksByTitle("\"Philosophiae naturalis principia mathematica\"");
	assertTrue(result.getItems().size()>0);
	VolumeInfo info = result.getItems().get(0).getVolumeInfo();
	assertEquals("Philosophiae naturalis principia mathematica", info.getTitle());
	assertEquals("Isaac Newton", info.getAuthors().get(0));
    }

    @Test
    public void testFindBooksByid(){
	Item item = bookService.findBookById("3h9_GY8v-hgC");
	VolumeInfo info = item.getVolumeInfo();
	assertEquals("Philosophiae naturalis principia mathematica", info.getTitle());
	assertEquals("Isaac Newton", info.getAuthors().get(0));
    }
}
