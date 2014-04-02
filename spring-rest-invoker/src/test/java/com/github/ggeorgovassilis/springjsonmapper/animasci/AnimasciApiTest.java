package com.github.ggeorgovassilis.springjsonmapper.animasci;

import java.util.Date;

import org.junit.runner.RunWith;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.ggeorgovassilis.springjsonmapper.animasci.AnimasciService;
import com.github.ggeorgovassilis.springjsonmapper.animasci.Animation;
import com.github.ggeorgovassilis.springjsonmapper.animasci.Frame;

import static org.junit.Assert.*;

/**
 * Integration test with the animasci.com REST api
 * @author george georgovassilis
 *
 */
@RunWith(value=SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-context-animasci.xml")
public class AnimasciApiTest {

    @Autowired
    AnimasciService animasciService;
    
    /**
     * Posts an anonymous animation and verifies that the result is ok
     */
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
