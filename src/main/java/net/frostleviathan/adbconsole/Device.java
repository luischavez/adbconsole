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
 * Dispositivo base de adb.
 *
 * @author Frost
 */
public interface Device {

    /**
     * Identificador del dispositivo.
     *
     * @return identificador
     */
    public String deviceId();

    /**
     * Modelo del dispositivo.
     *
     * @return modelo
     */
    public String model();

    /**
     * Verifica si el dispositivo esta autorizado.
     *
     * @return <b>true</b> si esta autorizado, <b>false</b> si no
     */
    public boolean isAuthorized();

}
