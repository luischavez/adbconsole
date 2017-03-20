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
 * Se encarga de manejar la conexion con el dispositivo cuando se conecta y es
 * aprobado por el usuario.
 *
 * @author Frost
 */
@FunctionalInterface
public interface OnDeviceCallback {

    /**
     * Metodo a ejecutar cuando se acepta la conexion.
     *
     * @param device dispositivo conectado
     * @param connected indica si el dipositivo esta conectado
     */
    public void onDevice(Device device, boolean connected);

}
