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
package fr.unicaen.iota.validator.model;

/**
 *
 */
public class DSLink extends Link {

    private String login;
    private String password;

    public DSLink(String dsAddress, String login, String password, boolean active) {
        super(active, dsAddress);
        this.login = login;
        this.password = password;
    }

    /**
     * @return the dsAddress
     */
    public String getDsAddress() {
        return getServiceAddress();
    }

    /**
     * @param dsAddress the dsAddress to set
     */
    public void setDsAddress(String dsAddress) {
        setServiceAddress(dsAddress);
    }

    /**
     * @return the login
     */
    public String getLogin() {
        return login;
    }

    /**
     * @param login the login to set
     */
    public void setLogin(String login) {
        this.login = login;
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

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (o instanceof DSLink) {
            DSLink link = (DSLink) o;
            return link.getDsAddress().equals(getServiceAddress())
                && link.getLogin().equals(this.login)
                && link.getPassword().equals(this.password);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + (getServiceAddress() != null ? this.getServiceAddress().hashCode() : 0);
        hash = 71 * hash + (this.login != null ? this.login.hashCode() : 0);
        hash = 71 * hash + (this.password != null ? this.password.hashCode() : 0);
        return hash;
    }

    public String toXML() {
        StringBuilder result = new StringBuilder();
        result.append("<dsLink activeAnalyse=\"");
        result.append(isActiveAnalyse());
        result.append("\">\n");
        result.append("<wildCardAccount>\n");
        result.append("<login>");
        result.append(login);
        result.append("</login>\n");
        result.append("<password>");
        result.append(password);
        result.append("</password>\n");
        result.append("</wildCardAccount>\n");
        result.append("<serviceAddress>");
        result.append(getDsAddress());
        result.append("</serviceAddress>\n");
        result.append("</dsLink>\n");
        return result.toString();
    }
}
