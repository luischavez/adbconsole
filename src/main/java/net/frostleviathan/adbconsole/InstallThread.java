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
 * Hilo que realiza la instalacion de archivos en el dispositivo.
 *
 * @author Frost
 */
public class InstallThread extends Thread implements Runnable {

    /**
     * Estado del hilo.
     */
    private boolean alive = true;

    /**
     * Instancia de la consola.
     */
    private final Console console;

    /**
     * Dispositivo en espera.
     */
    private final Device device;

    /**
     * Comando base de instalacion.
     */
    private final String baseCommand;

    /**
     * Codigo a ejecutar al finalizar la instalacion.
     */
    private final OnInstallCallback callback;

    /**
     * Archivos a instalar.
     */
    private final File[] files;

    /**
     * Construye el hilo a partir de los valores especificados.
     *
     * @param console consola
     * @param device dispositivo
     * @param baseCommand comando base de instalacion
     * @param files archivos a instalar
     * @param callback codigo a ejecutar al finalizar
     */
    public InstallThread(Console console, Device device, String baseCommand,
            File[] files, OnInstallCallback callback) {
        this.console = console;
        this.device = device;
        this.baseCommand = baseCommand;
        this.files = files;
        this.callback = callback;
    }

    public void stopInstall() {
        alive = false;
    }

    @Override
    public void run() {
        int success = 0;
        for (File file : files) {
            if (!alive) {
                break;
            }

            String command = String.format("%s %s",
                    baseCommand, file.getAbsolutePath());
            AdbResult result = console.execute(command);

            if (result.output.contains("Failed")) {
                break;
            }

            if (result.success) {
                success++;
            }
        }

        console.cancel(device);

        if (null != callback) {
            callback.onInstall(device, files, success == files.length);
        }
    }

}
