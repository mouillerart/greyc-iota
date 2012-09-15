/*
 *  This program is a part of the IoTa Project.
 *
 *  Copyright © 2008-2012  Université de Caen Basse-Normandie, GREYC
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
package fr.unicaen.iota.xacml.policy;

import fr.unicaen.iota.xacml.cond.OneOrGlobalFunction;
import java.util.List;

public abstract class SCExtensionBivaluesRule extends SCExtensionRule {

    public static String EXTENSIONTYPE = "BiValuesExtension";

    public SCExtensionBivaluesRule(String ruleId, String extensionName, List values) throws Exception {
        super(ruleId, extensionName, values);
        if (values.size() % 2 != 0) {
            throw new Exception("values have to be added in pair");
        }
    }

    public SCExtensionBivaluesRule(String ruleId, String extensionName, List values, String actions) throws Exception {
        super(ruleId, extensionName, values, actions);
        if (values.size() % 2 != 0) {
            throw new Exception("values have to be added in pair");
        }
    }

    public SCExtensionBivaluesRule(String ruleId, String extensionName, List values, OneOrGlobalFunction extensionsFilterFunction) {
        super(ruleId, extensionName, values, extensionsFilterFunction);
    }
}
