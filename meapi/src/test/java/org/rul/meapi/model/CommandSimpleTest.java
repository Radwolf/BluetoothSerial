package org.rul.meapi.model;

import junit.framework.Assert;

import org.junit.Test;
import org.rul.meapi.MeConstants;

/**
 * Created by rgonzalez on 20/05/2016.
 */
public class CommandSimpleTest {

    @Test
    public void testCommandSimpleValidacionIncompleto(){
        CommandSimple command = new CommandSimple("Prueba validacion incompleto", 1, 5, MeConstants.WRITEMODULE);
        Assert.assertFalse(command.isCommandComplet());
    }

}
