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
package fr.unicaen.iota.auth;

import java.util.Date;

public class User {

    public static String PROP_LOGIN = "Login";
    public static String PROP_DATE = "Date";
    public static String PROP_USER_I_D = "UserID";
    public static String PROP_ID = "Id";
    public static String PROP_PARTNER = "Partner";
    public static String PROP_PASSWD = "Passwd";
    // primary key
    private Integer _id;
    // fields
    private String _passwd;
    private String _userID;
    private String _login;
    private Date _date;
    // many to one
    private Partner _partner;

    // constructors
    public User() {
        initialize();
    }

    /**
     * Constructor for primary key
     */
    public User(Integer _id) {
        this.setId(_id);
        initialize();
    }

    /**
     * Constructor for required fields
     */
    public User(Integer _id, Partner _partner, String _passwd, String _userID, String _login, Date _date) {
        this.setId(_id);
        this.setPartner(_partner);
        this.setPasswd(_passwd);
        this.setUserID(_userID);
        this.setLogin(_login);
        this.setDate(_date);
        initialize();
    }

    protected void initialize() {
    }

    public Integer getId() {
        return _id;
    }

    public void setId(Integer _id) {
        this._id = _id;
    }

    public String getPasswd() {
        return _passwd;
    }

    public void setPasswd(String _passwd) {
        this._passwd = _passwd;
    }

    public String getUserID() {
        return _userID;
    }

    public void setUserID(String _userID) {
        this._userID = _userID;
    }

    public String getLogin() {
        return _login;
    }

    public void setLogin(String _login) {
        this._login = _login;
    }

    public java.util.Date getDate() {
        return _date;
    }

    public void setDate(java.util.Date _date) {
        this._date = _date;
    }

    public Partner getPartner() {
        return this._partner;
    }

    public void setPartner(Partner _partner) {
        this._partner = _partner;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}