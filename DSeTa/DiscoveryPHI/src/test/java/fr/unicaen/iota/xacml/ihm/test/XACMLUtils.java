/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.unicaen.iota.xacml.ihm.test;

import com.sun.xacml.ctx.Result;

/**
 *
 * @author jlemoule
 */
public class XACMLUtils {


    public static int createXACMLResponse(String resp){
        return resp.equals("ACCEPT") ? Result.DECISION_PERMIT : Result.DECISION_DENY;
    }

}
