/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2008-2012  Université de Caen Basse-Normandie, GREYC
 *                     		
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  <http://www.gnu.org/licenses/>
 *
 *  See AUTHORS for a list of contributors.
 */
package fr.unicaen.iota.xacml.pep;

/**
 *
 */
public interface MethodNamesAdmin {

    public int hello(String user, String partner, String module);

    public int userLookup(String user, String partner, String module);

    public int userCreate(String user, String partner, String module);

    public int userInfo(String user, String partner, String module);

    public int userUpdate(String user, String partner, String module);

    public int userDelete(String user, String partner, String module);

    public int partnerInfo(String user, String partner, String module);

    public int partnerUpdate(String user, String partner, String module);

    public int partnerDelete(String user, String partner, String module);

    public int createPartnerGroup(String user, String partner, String module);

    public int deletePartnerGroup(String user, String partner, String module);

    public int addPartnerToGroup(String user, String partner, String module);

    public int removePartnerFromGroup(String user, String partner, String module);

    public int addBizStepRestriction(String user, String partner, String module);

    public int removeBizStepRestriction(String user, String partner, String module);

    public int addEPCRestriction(String user, String partner, String module);

    public int removeEPCRestriction(String user, String partner, String module);

    public int addEPCClassRestriction(String user, String partner, String module);

    public int removeEPCClassRestriction(String user, String partner, String module);

    public int addTimeRestriction(String user, String partner, String module);

    public int removeTimeRestriction(String user, String partner, String module);

    public int switchBizStepPolicy(String user, String partner, String module);

    public int switchEPCPolicy(String user, String partner, String module);

    public int switchEPCClassPolicy(String user, String partner, String module);

    public int switchTimePolicy(String user, String partner, String module);

    public int switchUserPermissionPolicy(String user, String partner, String module);

    public int removeUserPermission(String user, String partner, String module);

    public int addUserPermission(String user, String partner, String module);

    public int updateGroupName(String user, String partner, String module);

    public int savePolicyPartner(String user, String partner, String module);

    //####################################################
    //############## Admin Module Section ################
    //####################################################
//    public int superadmin(String user, String partner, String module);
//    public int allAdminMethods(String user, String partner, String module);
//
//    public int allQueryMethods(String user, String partner, String module);
//
//    public int allCaptureMethods(String user, String partner, String module);
    public int createAdminPartnerGroup(String user, String partner, String module);

    public int deleteAdminPartnerGroup(String user, String partner, String module);

    public int addAdminPartnerToGroup(String user, String partner, String module);

    public int removeAdminPartnerFromGroup(String user, String partner, String module);

    public int switchAdminUserPermissionPolicy(String user, String partner, String module);

    public int removeAdminUserPermission(String user, String partner, String module);

    public int addAdminUserPermission(String user, String partner, String module);

    public int updateAdminGroupName(String user, String partner, String module);

    public int saveAdminPolicyPartner(String user, String partner, String module);
}
