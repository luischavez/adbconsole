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
    private final OnInstallCallback installCallback;

    /**
     * Codigo a ejecutar cuando se actualize la instalacion.
     */
    private final OnInstallingCallback installingCallback;

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
     * @param installCallback codigo a ejecutar al finalizar
     */
    public InstallThread(Console console, Device device, String baseCommand,
            File[] files,
            OnInstallCallback installCallback,
            OnInstallingCallback installingCallback) {
        this.console = console;
        this.device = device;
        this.baseCommand = baseCommand;
        this.files = files;
        this.installCallback = installCallback;
        this.installingCallback = installingCallback;
    }

    public void stopInstall() {
        alive = false;
    }

    @Override
    public void run() {
        for (int current = 0; current < files.length; current++) {
            File file = files[current];

            if (!alive) {
                break;
            }

            installingCallback.onInstalling(device, file,
                    current + 1, files.length);

            String fileName = file.getName().replace(".apk", "");
            List<String> installedApps = console.listInstalledApps(device);

            if (installedApps.contains(fileName)) {
                continue;
            }

            AdbResult result;
            do {
                String command = String.format("%s %s",
                        baseCommand, file.getAbsolutePath());
                result = console.execute(command);
            } while (result == null
                    || !result.success
                    || result.output.contains("Failed"));
        }

        console.cancel(device);

        int installed = 0;
        List<String> installedApps = console.listInstalledApps(device);
        for (File file : files) {
            String fileName = file.getName().replace(".apk", "");
            if (installedApps.contains(fileName)) {
                installed++;
            }
        }

        if (null != installCallback) {
            installCallback.onInstall(device, files, installed == files.length);
        }
    }

}
