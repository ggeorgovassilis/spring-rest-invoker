package com.github.ggeorgovassilis.springjsonmapper;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface AnimasciService {

    @RequestMapping(value="/", method=RequestMethod.POST)
    Animation createNewAnimation(@RequestBody Animation animation);

    @RequestMapping(value="/{id}", method=RequestMethod.GET)
    Animation getAnimation(@PathVariable("id") String id);
}
