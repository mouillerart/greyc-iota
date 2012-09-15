/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2008-2012  Université de Caen Basse-Normandie, GREYC
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, version 2 of the License.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  <http://www.gnu.org/licenses/>
 *
 *  See AUTHORS for a list of contributors.
 */
package fr.unicaen.iota.simulator.pnk;

import de.huberlin.informatik.pnk.kernel.Extendable;
import de.huberlin.informatik.pnk.netElementExtensions.base.Marking;
import fr.unicaen.iota.simulator.app.CommCenter;
import fr.unicaen.iota.simulator.app.EPC;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class Pipe extends Marking {

    public static enum Type {

        EXPEDITION,
        RECEPTION
    }
    private boolean isPipe;
    private String password;
    private String address;
    private String reservedId;
    private String pipeId;
    private Type type;

    public Pipe(Extendable ext) {
        super(ext);
    }

    public Pipe(Extendable e, String value) {
        super(e, value);
        parse(value);
    }

    @Override
    public boolean contains(Marking marking) {
        return false;
    }

    @Override
    protected void localAdd(Marking marking) {
    }

    @Override
    protected void localSub(Marking marking) {
    }

    @Override
    protected boolean isValid() {
        return true;
    }

    @Override
    protected boolean isValid(Extendable extendable) {
        return true;
    }

    @Override
    protected boolean isValid(String state) {
        return true;
    }

    @Override
    public void checkContextAndParse() {
        parse(toString());
    }

    @Override
    public String defaultToString() {
        StringBuilder res = new StringBuilder();
        res.append("isPipe = false\n");
        res.append("address = http://localhost:8080/SimulatorServer\n"); // TODO: hard value
        res.append("password = poudredeperl1p1p1\n");
        res.append("pipeId = pipe_1\n");
        res.append("type = expedition\n");
        return res.toString();
    }

    private void parse(String value) {
        Map<String, String> map = new HashMap<String, String>();
        String[] tab = value.split("\n");
        for (String s : tab) {
            String[] line = s.split("=");
            map.put(line[0].trim(), line[1].trim());
        }
        initMissingValues(map);
        isPipe = Boolean.parseBoolean(map.get("isPipe"));
        setPassword(map.get("password"));
        setAddress(map.get("address"));
        setPipeId(map.get("pipeId"));
        if (map.get("type").equals("expedition")) {
            type = Type.EXPEDITION;
        } else {
            type = Type.RECEPTION;
        }
    }

    public Type getType() {
        return type;
    }

    public boolean isPipe() {
        return isPipe;
    }

    public void send(Marking inscription) {
        EPCInscription epcInscription = (EPCInscription) inscription;
        StringBuilder epcList = new StringBuilder();
        int i = 0;
        int l = epcInscription.getEpcList().size();
        for (EPC epc : epcInscription.getEpcList()) {
            epcList.append(epc);
            if (i != l - 1) {
                epcList.append("%");
            }
            i++;
        }
        pipePuplication(epcList.toString());
        epcInscription.clearEpcList();
        this.reservedId = null;
    }

    private void pipePuplication(String epcList) {
        CommCenter.getInstance().publish(address, pipeId, password, reservedId, epcList);
    }

    public boolean reserve(int canalSize) {
        String res = CommCenter.getInstance().reserve(address, pipeId, password, reservedId, canalSize);
        if (res == null) {
            return false;
        }
        reservedId = res;
        return true;
    }

    public List<String> loadPipe() {
        return CommCenter.getInstance().loadPipe(address, pipeId, password);
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return the pipeId
     */
    public String getPipeId() {
        return pipeId;
    }

    /**
     * @param pipeId the pipeId to set
     */
    public void setPipeId(String pipeId) {
        this.pipeId = pipeId;
    }

    private void initMissingValues(Map<String, String> map) {
        if (map.get("isPipe") == null) {
            map.put("isPipe", "false");
        }
        if (map.get("password") == null) {
            map.put("password", "poudredeperl1p1p1");
        }
        if (map.get("address") == null) {
            map.put("address", "http://localhost:8080/SimulatorServer");
        }
        if (map.get("pipeId") == null) {
            map.put("pipeId", "pipe_1");
        }
        if (map.get("type") == null) {
            map.put("type", "expedition");
        }
    }
}
