package com.github.ggeorgovassilis.springjsonmapper;

import java.util.Date;

import javax.annotation.Resource;

import org.junit.runner.RunWith;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(value=SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-context.xml")
public class SpringJsonMapperTest {

    @Resource
    BookService bookService;

    @Resource
    AnimasciService animasciService;
    
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

    @Test
    public void testCRUD(){
	Animation a1 = new Animation();
	a1.setTitle("spring json mapper test");
	a1.setComment("Unit test for the spring json mapper");
	a1.setFrameSize(10);
	a1.setLastUpdated(new Date());
	a1.setStatus("draft");
	
	Frame frame = new Frame();
	frame.setDuration(1000);
	frame.setText("Hello world!");
	a1.getFrames().add(frame);
	
	Animation a2 = animasciService.createNewAnimation(a1);
	assertNotNull(a2.getId());
	System.out.println("Posted animation "+a2.getId());
	assertEquals(a1.getTitle(), a2.getTitle());
	assertEquals(a1.getComment(), a2.getComment());
	assertEquals(a1.getFrames().size(), a2.getFrames().size());
	assertEquals(a1.getFrames().get(0).getText(), a2.getFrames().get(0).getText());
    }

}
