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
package fr.unicaen.iota.validator;

import fr.unicaen.iota.application.model.DSEvent;
import fr.unicaen.iota.validator.listener.AnalyserListener;
import fr.unicaen.iota.validator.model.BaseEvent;
import fr.unicaen.iota.validator.model.EPC;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class AnalyserResult extends HashMap<String, ResultRaw> implements AnalyserListener {

    public AnalyserResult() {
    }

    @Override
    public void analysedObject() {
    }

    @Override
    public void errorFound() {
    }

    public int getErrorCount(String epc) {
        ResultRaw result = this.get(epc);
        return result.getDsEvents().size()
                + result.getEpcisEvents().size()
                + result.getDsToDsEvents().size();
    }

    @Override
    public void logFileAnalysed(Map<String, Integer> epcisResult, Map<String, Integer> dsResult, Map<String, Integer> dsToDsResult) {
    }

    @Override
    public void publishResults(List<EPC> containerList, Map<EPC, List<BaseEvent>> epcisResults, Map<EPC, List<DSEvent>> dsResults, Map<EPC, List<DSEvent>> dsToDsResults) {
        ResultRaw resultRaw = new ResultRaw(containerList);
        for (EPC container : containerList) {
            if (Configuration.ANALYSE_EPCIS_EVENTS) {
                resultRaw.getEpcisEvents().addAll(epcisResults.get(container));
            }
            if (Configuration.ANALYSE_EPCIS_TO_DS_EVENTS) {
                resultRaw.getDsEvents().addAll(dsResults.get(container));
            }
            if (Configuration.ANALYSE_DS_TO_DS_EVENTS) {
                resultRaw.getDsToDsEvents().addAll(dsToDsResults.get(container));
            }
            this.put(container.getEpc(), resultRaw);
        }
    }

    public void updateRawForDStoDS(EPC cont, List<DSEvent> dsEvents) {
        ResultRaw resultRaw = this.get(cont.getEpc());
        resultRaw.getDsToDsEvents().clear();
        resultRaw.getDsToDsEvents().addAll(dsEvents);
    }

    public void updateRawForDStoEPCIS(EPC cont, List<DSEvent> dsEvents) {
        ResultRaw resultRaw = this.get(cont.getEpc());
        resultRaw.getDsEvents().clear();
        resultRaw.getDsEvents().addAll(dsEvents);
    }

    public void updateRawForEPCIS(EPC cont, List<BaseEvent> epcisEvt) {
        ResultRaw resultRaw = this.get(cont.getEpc());
        resultRaw.getEpcisEvents().clear();
        resultRaw.getEpcisEvents().addAll(epcisEvt);
    }
}
