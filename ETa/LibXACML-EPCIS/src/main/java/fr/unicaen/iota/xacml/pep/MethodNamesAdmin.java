/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2011-2012  Université de Caen Basse-Normandie, GREYC
 *  Copyright © 2011       Orange Labs
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

public interface MethodNamesAdmin {

    public int hello(String user, String partner);

    public int userLookup(String user, String partner);

    public int userCreate(String user, String partner);

    public int userInfo(String user, String partner);

    public int userUpdate(String user, String partner);

    public int userDelete(String user, String partner);

    public int partnerInfo(String user, String partner);

    public int partnerUpdate(String user, String partner);

    public int partnerDelete(String user, String partner);

    public int createPartnerGroup(String user, String partner);

    public int deletePartnerGroup(String user, String partner);

    public int addPartnerToGroup(String user, String partner);

    public int removePartnerFromGroup(String user, String partner);

    public int addBizStepRestriction(String user, String partner);

    public int removeBizStepRestriction(String user, String partner);

    public int switchBizStepPolicy(String user, String partner);

    public int addEpcRestriction(String user, String partner);

    public int removeEpcRestriction(String user, String partner);

    public int switchEpcPolicy(String user, String partner);

    public int addEventTimeRestriction(String user, String partner);

    public int removeEventTimeRestriction(String user, String partner);

    public int switchEventTimePolicy(String user, String partner);

    public int addRecordTimeRestriction(String user, String partner);

    public int removeRecordTimeRestriction(String user, String partner);

    public int switchRecordTimePolicy(String user, String partner);

    public int addEventTypeRestriction(String user, String partner);

    public int removeEventTypeRestriction(String user, String partner);

    public int switchEventTypePolicy(String user, String partner);

    public int addOperationRestriction(String user, String partner);

    public int removeOperationRestriction(String user, String partner);

    public int switchOperationPolicy(String user, String partner);

    public int addParentIdRestriction(String user, String partner);

    public int removeParentIdRestriction(String user, String partner);

    public int switchParentIdPolicy(String user, String partner);

    public int addChildEpcRestriction(String user, String partner);

    public int removeChildEpcRestriction(String user, String partner);

    public int switchChildEpcPolicy(String user, String partner);

    public int addQuantityRestriction(String user, String partner);

    public int removeQuantityRestriction(String user, String partner);

    public int switchQuantityPolicy(String user, String partner);

    public int addReadPointRestriction(String user, String partner);

    public int removeReadPointRestriction(String user, String partner);

    public int switchReadPointPolicy(String user, String partner);

    public int addBizLocRestriction(String user, String partner);

    public int removeBizLocRestriction(String user, String partner);

    public int switchBizLocPolicy(String user, String partner);

    public int addBizTransRestriction(String user, String partner);

    public int removeBizTransRestriction(String user, String partner);

    public int switchBizTransPolicy(String user, String partner);

    public int addDispositionRestriction(String user, String partner);

    public int removeDispositionRestriction(String user, String partner);

    public int switchDispositionPolicy(String user, String partner);

    public int addMasterDataIdRestriction(String user, String partner);

    public int removeMasterDataIdRestriction(String user, String partner);

    public int switchMasterDataIdPolicy(String user, String partner);

    public int addExtensionRestriction(String user, String partner);

    public int removeExtensionRestriction(String user, String partner);

    public int switchExtensionPolicy(String user, String partner);

    public int switchUserPermissionPolicy(String user, String partner);

    public int removeUserPermission(String user, String partner);

    public int addUserPermission(String user, String partner);

    public int updateGroupName(String user, String partner);

    public int savePolicyPartner(String user, String partner);

    //####################################################
    //############## Admin Module Section ################
    //####################################################
//    public int superadmin(String user, String partner);
//    public int allAdminMethods(String user, String partner);
//
//    public int allQueryMethods(String user, String partner);
//
//    public int allCaptureMethods(String user, String partner);
    public int createAdminPartnerGroup(String user, String partner);

    public int deleteAdminPartnerGroup(String user, String partner);

    public int addAdminPartnerToGroup(String user, String partner);

    public int removeAdminPartnerFromGroup(String user, String partner);

    public int switchAdminUserPermissionPolicy(String user, String partner);

    public int removeAdminUserPermission(String user, String partner);

    public int addAdminUserPermission(String user, String partner);

    public int updateAdminGroupName(String user, String partner);

    public int saveAdminPolicyPartner(String user, String partner);
}
