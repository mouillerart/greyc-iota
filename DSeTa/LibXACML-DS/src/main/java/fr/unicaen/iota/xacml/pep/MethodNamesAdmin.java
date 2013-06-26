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

    public int hello(String user, String owner);

    public int userLookup(String user, String owner);

    public int userCreate(String user, String owner);

    public int userInfo(String user, String owner);

    public int userUpdate(String user, String owner);

    public int userDelete(String user, String owner);

    public int ownerUpdate(String user, String owner);

    public int ownerDelete(String user, String owner);

    public int createOwnerGroup(String user, String owner);

    public int deleteOwnerGroup(String user, String owner);

    public int addOwnerToGroup(String user, String owner);

    public int removeOwnerFromGroup(String user, String owner);

    public int addBizStepRestriction(String user, String owner);

    public int removeBizStepRestriction(String user, String owner);

    public int addEPCRestriction(String user, String owner);

    public int removeEPCRestriction(String user, String owner);

    public int addEventTypeRestriction(String user, String owner);

    public int removeEventTypeRestriction(String user, String owner);

    public int addTimeRestriction(String user, String owner);

    public int removeTimeRestriction(String user, String owner);

    public int switchBizStepPolicy(String user, String owner);

    public int switchEPCPolicy(String user, String owner);

    public int switchEventTypePolicy(String user, String owner);

    public int switchTimePolicy(String user, String owner);

    public int switchUserPermissionPolicy(String user, String owner);

    public int removeUserPermission(String user, String owner);

    public int addUserPermission(String user, String owner);

    public int updateGroupName(String user, String owner);

    public int savePolicyOwner(String user, String owner);

    //####################################################
    //############## Admin Module Section ################
    //####################################################
//    public int superadmin(String user, String owner);
//    public int allAdminMethods(String user, String owner);
//
//    public int allQueryMethods(String user, String owner);
//
//    public int allCaptureMethods(String user, String owner);
    public int createAdminOwnerGroup(String user, String owner);

    public int deleteAdminOwnerGroup(String user, String owner);

    public int addAdminOwnerToGroup(String user, String owner);

    public int removeAdminOwnerFromGroup(String user, String owner);

    public int switchAdminUserPermissionPolicy(String user, String owner);

    public int removeAdminUserPermission(String user, String owner);

    public int addAdminUserPermission(String user, String owner);

    public int updateAdminGroupName(String user, String owner);

    public int saveAdminPolicyOwner(String user, String owner);
}
