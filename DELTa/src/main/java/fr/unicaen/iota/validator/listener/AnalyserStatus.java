/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2008-2013  Université de Caen Basse-Normandie, GREYC
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

import fr.unicaen.iota.ds.model.DSEvent;
import fr.unicaen.iota.validator.model.BaseEvent;
import fr.unicaen.iota.validator.model.EPC;
import java.util.List;
import java.util.Map;
import javax.swing.event.EventListenerList;

/**
 *
 */
public class AnalyserStatus {

    private final EventListenerList listeners = new EventListenerList();

    public AnalyserStatus() {
    }

    public synchronized void analysedObject() {
        fireAnalysedObject();
    }

    public void logFileAnalysed(Map<String, Integer> epcisResult, Map<String, Integer> dsResult, Map<String, Integer> dsToDsResult) {
        fireLogFileAnalysed(epcisResult, dsResult, dsToDsResult);
    }

    public synchronized void errorFound() {
        fireErrorFound();
    }

    public void addListener(AnalyserListener listener) {
        listeners.add(AnalyserListener.class, listener);
    }

    public void publishResults(List<EPC> epcList, Map<EPC, List<BaseEvent>> epcisResults, Map<EPC, List<DSEvent>> dsResults, Map<EPC, List<DSEvent>> dsToDsResults) {
        firePublishResults(epcList, epcisResults, dsResults, dsToDsResults);
    }

    public void removeListener(AnalyserListener listener) {
        listeners.remove(AnalyserListener.class, listener);
    }

    public AnalyserListener[] getListeners() {
        return listeners.getListeners(AnalyserListener.class);
    }

    protected void fireAnalysedObject() {
        for (AnalyserListener listener : getListeners()) {
            listener.analysedObject();
        }
    }

    private void fireErrorFound() {
        for (AnalyserListener listener : getListeners()) {
            listener.errorFound();
        }
    }

    private void fireLogFileAnalysed(Map<String, Integer> epcisResult, Map<String, Integer> dsResult, Map<String, Integer> dsToDsResult) {
        for (AnalyserListener listener : getListeners()) {
            listener.logFileAnalysed(epcisResult, dsResult, dsToDsResult);
        }
    }

    private void firePublishResults(List<EPC> epcList, Map<EPC, List<BaseEvent>> epcisResults, Map<EPC, List<DSEvent>> dsResults, Map<EPC, List<DSEvent>> dsToDsResults) {
        for (AnalyserListener listener : getListeners()) {
            listener.publishResults(epcList, epcisResults, dsResults, dsToDsResults);
        }
    }
}
