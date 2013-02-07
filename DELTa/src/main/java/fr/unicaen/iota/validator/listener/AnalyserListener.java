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
package fr.unicaen.iota.validator.listener;

import fr.unicaen.iota.application.model.DSEvent;
import fr.unicaen.iota.validator.model.BaseEvent;
import fr.unicaen.iota.validator.model.EPC;
import java.util.EventListener;
import java.util.List;
import java.util.Map;

/**
 *
 */
public interface AnalyserListener extends EventListener {

    public void analysedObject();

    public void errorFound();

    public void logFileAnalysed(Map<String, Integer> epcisResult, Map<String, Integer> dsResult, Map<String, Integer> dsToDsResult);

    public void publishResults(List<EPC> epcList, Map<EPC, List<BaseEvent>> epcisResults, Map<EPC, List<DSEvent>> dsResults, Map<EPC, List<DSEvent>> dsToDsResults);
}
