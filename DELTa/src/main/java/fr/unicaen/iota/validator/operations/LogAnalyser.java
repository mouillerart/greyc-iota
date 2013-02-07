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
package fr.unicaen.iota.validator.operations;

import fr.unicaen.iota.validator.Configuration;
import fr.unicaen.iota.validator.listener.AnalyserStatus;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class LogAnalyser {

    private AnalyserStatus analyserStatus;

    public LogAnalyser(AnalyserStatus analyserStatus) {
        this.analyserStatus = analyserStatus;
    }

    public void load() throws IOException {
        File logDirectory = new File(Configuration.LOG_DIRECTORY);
        for (File f : logDirectory.listFiles()) {

            FileInputStream fileInputStream = new FileInputStream(f);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = null;
            Map<String, Integer> epcisResult = new HashMap<String, Integer>();
            Map<String, Integer> dsResult = new HashMap<String, Integer>();
            Map<String, Integer> dsToDsResult = new HashMap<String, Integer>();
            while ((line = bufferedReader.readLine()) != null) {
                String[] split = line.split(" ");
                String epc = split[1];
                String type = split[0];
                int found = Integer.parseInt(split[2]);
                int require = Integer.parseInt(split[3]);
                if (type.equals(Configuration.EPCIS_LOG_TYPE)) {
                    epcisResult.put(epc, require - found);
                }
                if (type.equals(Configuration.DS_LOG_TYPE)) {
                    dsResult.put(epc, require - found);
                }
                if (type.equals(Configuration.DS_TO_DS_LOG_TYPE)) {
                    dsToDsResult.put(epc, require - found);
                }
            }
            analyserStatus.logFileAnalysed(epcisResult, dsResult, dsToDsResult);
            for (String k : epcisResult.keySet()) {
                if (epcisResult.get(k) != 0 || dsResult.get(k) != 0 || dsToDsResult.get(k) != 0) {
                    analyserStatus.errorFound();
                    break;
                }
            }
        }
    }
}
