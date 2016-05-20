package org.rul.meapi.model;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Created by rgonzalez on 20/05/2016.
 */
public class CommandSimpleTest {

    @Test
    public void testCommandSimpleValidacionIncompleto(){
        CommandSimple command = new CommandSimple("Prueba validacion incompleto", 5);
        Assert.assertFalse(command.isCommandComplet());
    }

}
