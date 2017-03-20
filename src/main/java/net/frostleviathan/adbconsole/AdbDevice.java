/*
 * Copyright (C) 2017 Frost
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.frostleviathan.adbconsole;

/**
 * Implementacion base de los dispositivos de adb.
 *
 * @author Frost
 */
public class AdbDevice implements Device {

    /**
     * Identificador del dispositivo.
     */
    private final String deviceId;

    /**
     * Modelo del dispositivo.
     */
    private final String model;

    /**
     * Estado del dispositivo.
     */
    private final boolean authorized;

    public AdbDevice(String deviceId, String model, boolean authorized) {
        this.deviceId = deviceId;
        this.model = model;
        this.authorized = authorized;
    }

    @Override
    public String deviceId() {
        return deviceId;
    }

    @Override
    public String model() {
        return model;
    }

    @Override
    public boolean isAuthorized() {
        return authorized;
    }

}
