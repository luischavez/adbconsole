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
 * Se ejecuta al finalizar la instalacion de archivos en el dispositivo.
 *
 * @author Frost
 */
@FunctionalInterface
public interface OnInstallCallback {

    /**
     * Se ejecuta al terminal la instalacion de los <b>archivos</b>
     * en el <b>dispositivo</b>.
     *
     * @param targetDevice dispositivo
     * @param files archivos a instalar
     * @param success resultado de la instalacion
     */
    public void onInstall(Device targetDevice, File[] files, boolean success);

}
