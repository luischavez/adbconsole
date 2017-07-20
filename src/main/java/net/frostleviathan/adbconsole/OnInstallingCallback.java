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

import java.io.File;

/**
 *
 * @author Frost
 */
@FunctionalInterface
public interface OnInstallingCallback {

    /**
     * Muestra el estado actual de la instalacion en el dispositivo.
     *
     * @param targetDevice dispositivo
     * @param file instalacion actual
     * @param installed aplicaciones instaladas
     * @param total total de aplicaciones
     */
    public void onInstalling(Device targetDevice, File file, int installed, int total);
}
