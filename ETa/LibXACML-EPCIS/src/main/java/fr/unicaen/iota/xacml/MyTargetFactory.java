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
package fr.unicaen.iota.xacml;

import com.sun.xacml.PolicyMetaData;
import com.sun.xacml.Target;
import com.sun.xacml.TargetMatch;
import com.sun.xacml.TargetSection;
import java.util.List;

public class MyTargetFactory {

    private MyTargetFactory() {
    }

    public static Target getTargetInstance(List subjects, List resources, List actions) {
        TargetSection subjectsTarget = new TargetSection(subjects, TargetMatch.SUBJECT, PolicyMetaData.XACML_DEFAULT_VERSION);
        TargetSection resourcesTarget = new TargetSection(resources, TargetMatch.RESOURCE, PolicyMetaData.XACML_DEFAULT_VERSION);
        TargetSection actionsTarget = new TargetSection(actions, TargetMatch.ACTION, PolicyMetaData.XACML_DEFAULT_VERSION);
        return new Target(subjectsTarget, resourcesTarget, actionsTarget);
    }
}
