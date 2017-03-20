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
import java.util.List;

/**
 * Especificaciones de la consola de adb.
 *
 * @author Frost
 */
public interface Console {

    /**
     * Agrega un <b>listener</b> a la consola.
     *
     * @param listener
     */
    public void addListener(ConsoleListener listener);

    /**
     * Remueve un <b>listener</b> de la consola.
     *
     * @param listener
     */
    public void removeListener(ConsoleListener listener);

    /**
     * Listado de <b>dispositivos</b> conectados.
     *
     * @return dispositivos conectados
     * @throws AdbException
     */
    public List<Device> devices() throws AdbException;

    /**
     * Actualiza la informacion del <b>dispositivo</b>.
     *
     * @param device dispositivo
     * @return dispositivo actualizado o null si no esta presente
     * @throws AdbException
     */
    public Device update(Device device) throws AdbException;

    /**
     * Ejecuta el <b>callback</b> cuando el dispositivo es aceptado.
     *
     * @param device dispositivo
     * @param callback codigo a ejecutar cuando se acepta el dispositivo o null
     * @throws AdbException
     */
    public void waitFor(Device device, OnDeviceCallback callback)
            throws AdbException;

    /**
     * Deja de esperar al <b>dispositivo</b> indicado.
     *
     * @param device dispositivo
     * @throws AdbException
     */
    public void stopWait(Device device) throws AdbException;

    /**
     * Verifica si el <b>dispositivo</b> esta en estado de espera.
     *
     * @param device dispositivo
     * @return <b>true</b> si esta en estado de espera, <b>false</b> si no
     * @throws AdbException
     */
    public boolean isWaiting(Device device) throws AdbException;

    /**
     * Verifica si el <b>dispositivo</b> fue aceptado.
     *
     * @param device dispositivo
     * @return <b>true</b> si fue aceptado, <b>false</b> si no
     * @throws AdbException
     */
    public boolean isAuthorized(Device device) throws AdbException;

    /**
     * Instala un archivo en el dispositivo.
     *
     * @param device dispositivo
     * @param file archivo a instalar
     * @param force sobreescribe el archivo en caso de que exista
     * @param callback codigo a ejecutar al finalizar la instalacion o null
     * @throws AdbException
     * @throws UnauthorizedDeviceException si el dispositivo no fue aceptado
     */
    public void install(Device device, File file, boolean force,
            OnInstallCallback callback)
            throws AdbException, UnauthorizedDeviceException;

}
